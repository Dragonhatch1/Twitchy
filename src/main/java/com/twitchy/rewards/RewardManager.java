package com.twitchy.rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.twitchy.Twitchy;
import com.twitchy.api.TwitchApiClient;
import com.twitchy.api.TwitchModels.RewardRedemptionEvent;
import com.twitchy.auth.TwitchCredentials;
import com.twitchy.network.MessageRedeemAction;
import com.twitchy.network.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

/**
 * Client-side only. Handles an incoming redemption event: resolves the configured action and
 * either applies it immediately (pure client effects) or asks the server to apply it (anything
 * touching the world/players).
 */
public final class RewardManager {

    private RewardManager() {}

    public static void handleRedemption(RewardRedemptionEvent event) {
        Optional<String> maybeKey = RewardIdRegistry.keyForTwitchId(event.reward.id);
        if (maybeKey.isEmpty()) {
            Twitchy.LOG.info("Redemption for '{}' isn't one Twitchy created, ignoring.", event.reward.title);
            return;
        }
        dispatch(maybeKey.get(), event.user_login, event.user_name, event.user_input == null ? "" : event.user_input);
    }

    /** Simulates a redemption locally for testing (/twitchy testredeem), bypassing Twitch entirely.
     *  Returns false if no mapping exists for this key. */
    public static boolean testAction(String key) {
        if (RewardConfig.findByKey(key).isEmpty()) return false;
        dispatch(key, "testviewer", "TestViewer", "test input");
        return true;
    }

    private static void dispatch(String key, String viewerLogin, String viewerDisplayName, String userInput) {
        Optional<RewardAction> maybeAction = RewardConfig.findByKey(key);
        if (maybeAction.isEmpty()) return;
        RewardAction action = maybeAction.get();

        if (action.type == RewardActionType.CLIENT_EFFECT) {
            applyClientEffect(action, viewerDisplayName, userInput);
            return;
        }
        PacketHandler.sendToServer(new MessageRedeemAction(key, viewerLogin, viewerDisplayName, userInput));
    }

    private static void applyClientEffect(RewardAction action, String viewerDisplayName, String userInput) {
        Minecraft mc = Minecraft.getMinecraft();
        if (action.message != null && !action.message.isBlank() && mc.thePlayer != null) {
            String text = substitute(action.message, viewerDisplayName, userInput);
            mc.thePlayer.addChatMessage(new ChatComponentText(text));
        }
        if (action.sound != null && !action.sound.isBlank() && mc.thePlayer != null) {
            mc.getSoundHandler()
                .playSound(
                    new PositionedSoundRecord(
                        new ResourceLocation(action.sound),
                        1.0F,
                        1.0F,
                        (float) mc.thePlayer.posX,
                        (float) mc.thePlayer.posY,
                        (float) mc.thePlayer.posZ));
        }
    }

    private static String substitute(String template, String viewerDisplayName, String userInput) {
        return template.replace("{viewer}", viewerDisplayName == null ? "" : viewerDisplayName)
            .replace("{input}", userInput == null ? "" : userInput);
    }

    public static CompletableFuture<Void> syncRewardsToTwitch(TwitchCredentials creds) {
        RewardIdRegistry.load(creds.userId);
        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        for (RewardMapping mapping : RewardConfig.all()) {
            if (RewardIdRegistry.twitchIdForKey(mapping.key).isPresent()) {
                continue; // already created for this broadcaster
            }
            CompletableFuture<Void> task = TwitchApiClient
                .createCustomReward(creds, mapping.title, mapping.cost, mapping.prompt)
                .thenAccept(reward -> {
                    RewardIdRegistry.put(creds.userId, mapping.key, reward.id);
                    Twitchy.LOG.info("Created Twitch reward '{}' (id {}) for key '{}'", mapping.title, reward.id, mapping.key);
                });
            tasks.add(task);
        }
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
    }
}

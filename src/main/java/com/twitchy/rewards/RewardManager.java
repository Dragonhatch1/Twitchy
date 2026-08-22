package com.twitchy.rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import com.twitchy.Twitchy;
import com.twitchy.api.TwitchApiClient;
import com.twitchy.api.TwitchModels.RewardRedemptionEvent;
import com.twitchy.auth.TwitchCredentials;
import com.twitchy.client.CameraFlipEffect;
import com.twitchy.client.FovEffectManager;
import com.twitchy.client.KeySequenceChallengeManager;
import com.twitchy.client.ToastEffect;
import com.twitchy.client.TwitchSessionManager;
import com.twitchy.entity.MobSpawningConfig;
import com.twitchy.entity.ViewerFollowerGear;
import com.twitchy.network.*;

/**
 * Client-side only. Handles an incoming redemption event: resolves the configured action and
 * either applies it immediately (pure client effects) or asks the server to apply it (anything
 * touching the world/players).
 */
public final class RewardManager {

    private static final int CHALLENGE_MIN_LENGTH = 8;
    private static final int CHALLENGE_MAX_LENGTH = 20;

    private RewardManager() {}

    public static void handleRedemption(RewardRedemptionEvent event) {
        Optional<String> maybeKey = RewardIdRegistry.keyForTwitchId(event.reward.id);
        if (maybeKey.isEmpty()) {
            Twitchy.LOG.info("Redemption for '{}' isn't one Twitchy created, ignoring.", event.reward.title);
            return;
        }
        dispatch(
            maybeKey.get(),
            event.user_login,
            event.user_name,
            event.user_id,
            event.user_input == null ? "" : event.user_input,
            event.id,
            event.reward.id);
    }

    /**
     * Simulates a redemption locally for testing (/twitchy testredeem), bypassing Twitch entirely.
     * Returns false if no mapping exists for this key.
     */
    public static boolean testAction(String key) {
        if (RewardConfig.findByKey(key)
            .isEmpty()) return false;
        dispatch(key, "testviewer", "TestViewer", "test-user-id", "test input", null, null);
        return true;
    }

    private static void dispatch(String key, String viewerLogin, String viewerDisplayName, String viewerUserId,
        String userInput, String redemptionId, String twitchRewards) {
        Optional<RewardAction> maybeAction = RewardConfig.findByKey(key);
        if (maybeAction.isEmpty()) {
            fulfill(redemptionId, twitchRewards, false);
            return;
        }
        RewardAction action = maybeAction.get();

        if (action.type != RewardActionType.KEY_SEQUENCE_CHALLENGE && action.toastTitle != null
            && !action.toastTitle.isBlank()) {
            String title = substitute(action.toastTitle, viewerDisplayName, userInput);
            String subtitle = action.toastSubtitle == null ? ""
                : substitute(action.toastSubtitle, viewerDisplayName, userInput);
            ToastEffect.requestShow(title, subtitle, action.toastType);
        }

        if (action.type == RewardActionType.FOV_CHANGE) {
            FovEffectManager.requestApply(action.fovOffset);
            fulfill(redemptionId, twitchRewards, true);
            return;
        }
        if (action.type == RewardActionType.KEY_SEQUENCE_CHALLENGE) {
            String[] sequence = (action.keySequence != null && action.keySequence.length > 0) ? action.keySequence
                : generateRandomWasdSequence(randomChallengeLength());
            int seconds = (int) (sequence.length * 0.6F);

            String title = (action.toastTitle != null && !action.toastTitle.isBlank())
                ? substitute(action.toastTitle, viewerDisplayName, userInput)
                : null;
            String subtitle = (action.toastSubtitle != null && !action.toastSubtitle.isBlank())
                ? substitute(action.toastSubtitle, viewerDisplayName, userInput)
                : null;

            fulfill(redemptionId, twitchRewards, true);
            KeySequenceChallengeManager
                .requestStart(sequence, seconds, title, subtitle, action.regularSpawnCount, action.bossSpawnCount);
            return;
        }
        if (action.type == RewardActionType.RESIZE_FOLLOWER) {
            float newScale = ViewerFollowerGear.adjustScale(viewerUserId, action.resizeDelta);
            PacketHandler.sendToServer(new ResizeFollowerPacket(viewerUserId, newScale));
            fulfill(redemptionId, twitchRewards, true);
            return;
        }
        if (action.type == RewardActionType.HEAL_FOLLOWER) {
            PacketHandler.sendToServer(
                new HealFollowerPacket(viewerUserId, redemptionId, twitchRewards, action.healPercent));
            return;
        }
        if (action.type == RewardActionType.CLIENT_EFFECT) {
            applyClientEffect(action, viewerDisplayName, userInput);
            fulfill(redemptionId, twitchRewards, true);
            return;
        }
        if (action.type == RewardActionType.PLAY_SOUND) {
            if (action.sound != null && !action.sound.isBlank()) {
                playSound(action.sound, action.soundVolume, action.soundPitch);
            }
            fulfill(redemptionId, twitchRewards, true);
            return;
        }
        if (action.type == RewardActionType.GEAR_UPGRADE) {
            if (!com.twitchy.entity.ViewerFollowerGear.meetsRequirement(viewerUserId, action.prevItemReq)) {
                fulfill(redemptionId, twitchRewards, false);
                return;
            }
            if (!com.twitchy.entity.ViewerFollowerGear.hasEnoughKills(viewerUserId, action.requiredKills)) {
                fulfill(redemptionId, twitchRewards, false);
                return;
            }
            if (action.newItem == null || action.newItem.isEmpty()) {
                fulfill(redemptionId, twitchRewards, false);
                return;
            }
            ViewerFollowerGear.spendKills(viewerUserId, action.requiredKills);
            ViewerFollowerGear.applyUpgrade(viewerUserId, action.newItem);
            PacketHandler.sendToServer(new ApplyGearPacket(viewerUserId, action.newItem));
            fulfill(redemptionId, twitchRewards, true);
            return;
        }
        if (action.type == RewardActionType.SPAWN_RANDOM_MOBS) {
            if (action.regularSpawnCount > 0) {
                List<String> regularPicks = MobSpawningConfig.pickRandom(action.regularSpawnCount, false);
                PacketHandler.sendToServer(new RequestMobSpawnPacket(regularPicks, false));
            }
            if (action.bossSpawnCount > 0) {
                List<String> bossPicks = MobSpawningConfig.pickRandom(action.bossSpawnCount, true);
                PacketHandler.sendToServer(new RequestMobSpawnPacket(bossPicks, true));
            }
            fulfill(redemptionId, twitchRewards, true);
            return;
        }
        if (action.type == RewardActionType.GRAVITY_FLIP) {
            CameraFlipEffect.requestActivate(action.cameraFlipSeconds > 0 ? action.cameraFlipSeconds : 5);
        }

        PacketHandler.sendToServer(
            new RedeemActionPacket(
                key,
                viewerLogin,
                viewerDisplayName,
                viewerUserId,
                userInput,
                redemptionId,
                twitchRewards));
    }

    private static void applyClientEffect(RewardAction action, String viewerDisplayName, String userInput) {
        Minecraft mc = Minecraft.getMinecraft();
        if (action.message != null && !action.message.isBlank() && mc.thePlayer != null) {
            String text = substitute(action.message, viewerDisplayName, userInput);
            mc.thePlayer.addChatMessage(new ChatComponentText(text));
        }
        if (action.sound != null && !action.sound.isBlank() && mc.thePlayer != null) {
            playSound(action.sound, action.soundVolume, action.soundPitch);
        }
        if (action.cameraFlipSeconds > 0) {
            CameraFlipEffect.requestActivate(action.cameraFlipSeconds);
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
            Optional<String> existingId = RewardIdRegistry.twitchIdForKey(mapping.key);

            boolean requiresInput = mapping.requiresUserInput != null && mapping.requiresUserInput;

            if (existingId.isEmpty()) {
                if (!mapping.enabled) {
                    continue;
                }

                CompletableFuture<Void> task = TwitchApiClient
                    .createCustomReward(creds, mapping.title, mapping.cost, mapping.prompt, requiresInput)
                    .thenAccept(reward -> {
                        RewardIdRegistry.put(creds.userId, mapping.key, reward.id);
                        Twitchy.LOG.info(
                            "Created Twitch reward '{}' (id {}) for key '{}'",
                            mapping.title,
                            reward.id,
                            mapping.key);
                    });
                tasks.add(task);
            } else {
                // Already created previously - push the config's enabled state to Twitch every sync,
                // so toggling "enabled" in rewards.json actually pauses/resumes it on Twitch's side too.
                CompletableFuture<Void> task = TwitchApiClient
                    .updateCustomReward(creds, existingId.get(), mapping.enabled, requiresInput)
                    .exceptionally(ex -> {
                        // Don't let one stale/manually-deleted reward block the whole connect flow.
                        Twitchy.LOG
                            .warn("Failed to sync enabled state for reward key '{}': {}", mapping.key, ex.getMessage());
                        return null;
                    });
                tasks.add(task);
            }
        }
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
    }

    private static void fulfill(String redemptionId, String twitchRewardId, boolean success) {
        if (redemptionId == null || redemptionId.isBlank()) return; // test redemption, nothing to fulfill
        if (!TwitchSessionManager.INSTANCE.hasStoredToken()) return;
        TwitchApiClient
            .updateRedemptionStatus(TwitchSessionManager.INSTANCE.credentials(), twitchRewardId, redemptionId, success)
            .exceptionally(ex -> {
                Twitchy.LOG
                    .warn("Failed to mark redemption {} as fulfilled/canceled: {}", redemptionId, ex.getMessage());
                return null;
            });
    }

    private static void playSound(String soundName, float volume, float pitch) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        mc.getSoundHandler()
            .playSound(
                new PositionedSoundRecord(
                    new ResourceLocation(soundName),
                    volume,
                    pitch,
                    (float) mc.thePlayer.posX,
                    (float) mc.thePlayer.posY,
                    (float) mc.thePlayer.posZ));
    }

    private static final char[] WASD_POOL = { 'W', 'A', 'S', 'D' };

    private static String[] generateRandomWasdSequence(int length) {
        java.util.Random random = new java.util.Random();
        String[] seq = new String[length];
        for (int i = 0; i < length; i++) {
            seq[i] = String.valueOf(WASD_POOL[random.nextInt(WASD_POOL.length)]);
        }
        return seq;
    }

    private static int randomChallengeLength() {
        return CHALLENGE_MIN_LENGTH + new java.util.Random().nextInt(CHALLENGE_MAX_LENGTH - CHALLENGE_MIN_LENGTH + 1);
    }
}

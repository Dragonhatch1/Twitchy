package com.twitchy.client;

import com.twitchy.Config;
import com.twitchy.Twitchy;

import com.twitchy.gui.GearSetBuilderContainer;
import com.twitchy.gui.GearSetBuilderGui;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Client-side only. Tears down the live EventSub session whenever the player leaves a world -
 * whether that's exiting a singleplayer world (which also runs its own integrated server
 * connection under the hood) or disconnecting from a multiplayer server. Without this, staying
 * "connected" across world switches means Twitchy could end up listening from a stale session
 * while the player is free to run /twitchy connect again elsewhere.
 */
public class ClientEventHandler {

    private final ViewerFollowerClientPoller viewerFollowerPoller = new ViewerFollowerClientPoller();
    public static final KeyBinding OPEN_GEARSET_BUILDER = new KeyBinding(
        "key.twitchy.geargui", Keyboard.KEY_PERIOD, "key.categories.twitchy");

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (TwitchSessionManager.INSTANCE.isEventSubReady() || TwitchSessionManager.INSTANCE.hasStoredToken()) {
            Twitchy.LOG.info("Left the world - closing the Twitchy EventSub session (token kept).");
        }
        TwitchSessionManager.INSTANCE.disconnect();
        viewerFollowerPoller.reset();
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        TwitchSessionManager.INSTANCE.disconnect();
        viewerFollowerPoller.reset();
        if (!Config.autoConnectOnJoin) return;
        if (!TwitchSessionManager.INSTANCE.hasStoredToken()) return; // never auto-open the browser unprompted
        if (TwitchSessionManager.INSTANCE.isEventSubReady()) return; // already connected somehow

        Twitchy.LOG.info("Auto-connecting to Twitch (autoConnectOnJoin is enabled)...");
        TwitchSessionManager.INSTANCE.connect()
            .thenRun(() -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
                if (mc.thePlayer != null) {
                    mc.thePlayer.addChatMessage(
                        new net.minecraft.util.ChatComponentText(
                            "[Twitchy] Auto-connected to Twitch and listening for redemptions."));
                }
            })
            .exceptionally(ex -> {
                Twitchy.LOG.warn("Auto-connect to Twitch failed: {}", ex.getMessage());
                return null;
            });
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CameraFlipEffect.tick();
            FovEffectManager.tick();
            viewerFollowerPoller.tick();
            KeySequenceChallengeManager.tick();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (OPEN_GEARSET_BUILDER.isPressed()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null) { // don't yank the player out of another open GUI
                mc.displayGuiScreen(new GearSetBuilderGui(new GearSetBuilderContainer(mc.thePlayer.inventory)));
            }
        }
    }
}

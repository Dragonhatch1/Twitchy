package com.twitchy.client;

import com.twitchy.Config;
import com.twitchy.Twitchy;

import com.twitchy.api.TwitchApiClient;
import com.twitchy.api.TwitchModels;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

/**
 * Client-side only. Tears down the live EventSub session whenever the player leaves a world -
 * whether that's exiting a singleplayer world (which also runs its own integrated server
 * connection under the hood) or disconnecting from a multiplayer server. Without this, staying
 * "connected" across world switches means Twitchy could end up listening from a stale session
 * while the player is free to run /twitchy connect again elsewhere.
 */
public class ClientEventHandler {

    // TODO Remove when done with debug
    private static int chattersDebugTickCounter = 0;
    private static final int CHATTERS_DEBUG_INTERVAL_TICKS = 100; // Throttled to not hit HTTPs request time-out

    private final ViewerFollowerClientPoller viewerFollowerPoller = new ViewerFollowerClientPoller();

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (TwitchSessionManager.INSTANCE.isEventSubReady() || TwitchSessionManager.INSTANCE.hasStoredToken()) {
            Twitchy.LOG.info("Left the world - closing the Twitchy EventSub session (token kept).");
        }
        TwitchSessionManager.INSTANCE.disconnect();
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
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

        // TODO Twitch Viewer List Debug REMOVE AFTER DONE
        if (TwitchSessionManager.INSTANCE.hasStoredToken() && ++chattersDebugTickCounter >= CHATTERS_DEBUG_INTERVAL_TICKS) {
            chattersDebugTickCounter = 0;
            TwitchApiClient.getChatters(TwitchSessionManager.INSTANCE.credentials())
                .thenAccept(resp -> {
                    Twitchy.LOG.info("[DEBUG] Chatters ({} total):", resp.total);
                    for (TwitchModels.Chatter c : resp.data) {
                        Twitchy.LOG.info("  - {} (id={})", c.user_login, c.user_id);
                    }
                })
                .exceptionally(ex -> {
                    Twitchy.LOG.error("[DEBUG] Failed to fetch chatters", ex);
                    return null;
                });
        }
    }
}

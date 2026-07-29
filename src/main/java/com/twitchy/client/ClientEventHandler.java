package com.twitchy.client;

import com.twitchy.Twitchy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

/**
 * Client-side only. Tears down the live EventSub session whenever the player leaves a world -
 * whether that's exiting a singleplayer world (which also runs its own integrated server
 * connection under the hood) or disconnecting from a multiplayer server. Without this, staying
 * "connected" across world switches means Twitchy could end up listening from a stale session
 * while the player is free to run /twitchy connect again elsewhere.
 */
public class ClientEventHandler {

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (TwitchSessionManager.INSTANCE.isEventSubReady() || TwitchSessionManager.INSTANCE.hasStoredToken()) {
            Twitchy.LOG.info("Left the world - closing the Twitchy EventSub session (token kept).");
        }
        TwitchSessionManager.INSTANCE.disconnect();
    }
}

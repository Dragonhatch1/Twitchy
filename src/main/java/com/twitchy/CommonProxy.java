package com.twitchy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Twitchy.LOG.info("Twitchy {} loading (common)", Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    /**
     * Whether this side is allowed to own a live Twitch connection (OAuth/EventSub/chat).
     * Dedicated servers never do this - only a client (the streamer's own game) holds the Twitch session.
     */
    public boolean canOwnTwitchSession() {
        return false;
    }
}

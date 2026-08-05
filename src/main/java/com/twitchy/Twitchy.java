package com.twitchy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.twitchy.network.PacketHandler;
import com.twitchy.rewards.GravityFlipManager;
import com.twitchy.rewards.RewardConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Twitchy.MODID, version = Tags.VERSION, name = "Twitchy", acceptedMinecraftVersions = "[1.7.10]")
public class Twitchy {

    public static final String MODID = "twitchy";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.twitchy.ClientProxy", serverSide = "com.twitchy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        PacketHandler.init();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // Rewards config is shared: the server needs its own copy to validate/execute actions
        // requested by the client. /twitchy itself is a client-only command - see ClientProxy.
        RewardConfig.load();
        FMLCommonHandler.instance()
            .bus()
            .register(new GravityFlipManager());
        proxy.serverStarting(event);
    }
}

// TODO Double check all config, see if it can be condensed.
// TODO Potentially need it to pull current dimension when setStorage is being set. Rather than us supplying a
// dimension.
// TODO Redemption to put Unstable ingot in inventory. Have to get it out before it explodes.
// TODO Fix Dedicated Server Falling into ground, instead of up. Maybe -yMotion?
// TODO FOV Slider? i dunno how i wanna do it yet
// TODO Powerfail and Explosion Sound
// TODO Inventory Scramble

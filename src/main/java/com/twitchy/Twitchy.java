package com.twitchy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.twitchy.entity.EntityViewerFollower;
import com.twitchy.entity.ViewerFollowerHandler;
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
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid = Twitchy.MODID, version = Tags.VERSION, name = "Twitchy", acceptedMinecraftVersions = "[1.7.10]")
public class Twitchy {

    public static final String MODID = "twitchy";
    public static final Logger LOG = LogManager.getLogger(MODID);
    private static int nextEntityId = 1;

    @SidedProxy(clientSide = "com.twitchy.ClientProxy", serverSide = "com.twitchy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        PacketHandler.init();
        EntityRegistry
            .registerModEntity(EntityViewerFollower.class, "TwitchyViewerFollower", nextEntityId++, this, 64, 1, true);
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

        ViewerFollowerHandler viewerFollowerHandler = new ViewerFollowerHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(viewerFollowerHandler);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(viewerFollowerHandler);
    }
}

// TODO Hot Potato needs it own item
// TODO Mob Spawning every 10 Bits
// TODO Spawn entity on Sub | Twilight forest Entity | Ravens, Tiny Birds,
// TODO Pong as a captcha?

// TODO Spawn Mobs and Boss mobs on raid
// TODO Captcha Failure spawns clutch of 2 mobs
// TODO Add 3x random mob spawn | can add 5 minute cooldown on Twitch side
// TODO if Shrunken installed, allow users to adjust their size.

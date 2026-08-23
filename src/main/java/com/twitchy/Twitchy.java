package com.twitchy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.twitchy.entity.EntityListDiscovery;
import com.twitchy.entity.EntityViewerFollower;
import com.twitchy.entity.MobSpawningConfig;
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

        RewardConfig.load();
        MobSpawningConfig.load();
        EntityListDiscovery.generateIfMissing();

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
// TODO Mob Spawning every 10 Bits | Boss mob every 100 bits
// TODO Spawn entity on Sub | Twilight forest Entity | Ravens, Tiny Birds,
// TODO Pong as a captcha?

// TODO move to Equipment flags so we can have an equip system. Just unlock armor and equip it with !equip
// TODO Twilight Forest Models
// TODO Remove armor when Enderman

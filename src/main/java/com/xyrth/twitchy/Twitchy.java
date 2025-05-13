package com.xyrth.twitchy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xyrth.twitchy.client.handler.KeyInputEventHandler;
import com.xyrth.twitchy.network.TwitchyPacket;
import com.xyrth.twitchy.network.TwitchyPacketHandler;
import com.xyrth.twitchy.reference.Reference;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(
    modid = Reference.MOD_ID,
    version = Tags.VERSION,
    name = Reference.MOD_NAME,
    acceptedMinecraftVersions = "[1.7.10]")
public class Twitchy {

    public static final Logger LOG = LogManager.getLogger(Reference.MOD_ID);
    public static Twitchy instance;
    public static SimpleNetworkWrapper networkWrapper;

    @SidedProxy(clientSide = Reference.CLIENTSIDE, serverSide = Reference.SERVERSIDE)
    public static IProxy iproxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;

        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        // Initializing the network wrapper
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
        networkWrapper.registerMessage(TwitchyPacketHandler.class, TwitchyPacket.class, 0, Side.SERVER);

        // Registers Keybindings before game loads
        iproxy.registerKeyBindings();

        Twitchy.LOG.info("I am Twitchy at version " + Tags.VERSION);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {

        // Listening to key presses & what happens when they are pressed.
        FMLCommonHandler.instance()
            .bus()
            .register(new KeyInputEventHandler(networkWrapper));

    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}

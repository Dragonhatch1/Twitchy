package com.xyrth.twitchy;

import com.xyrth.twitchy.client.KeyInputEventHandler;
import com.xyrth.twitchy.client.Keybindings;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        FMLCommonHandler.instance()
            .bus()
            .register(new KeyInputEventHandler());

        ClientRegistry.registerKeyBinding(Keybindings.randomspawn);
        ClientRegistry.registerKeyBinding(Keybindings.randompotion);
        ClientRegistry.registerKeyBinding(Keybindings.mobraid);
        ClientRegistry.registerKeyBinding(Keybindings.substuff);
        ClientRegistry.registerKeyBinding(Keybindings.spawningtest);
    }
}

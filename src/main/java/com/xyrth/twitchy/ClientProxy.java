package com.xyrth.twitchy;

import com.xyrth.twitchy.client.settings.Keybindings;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(Keybindings.skeleton);
        ClientRegistry.registerKeyBinding(Keybindings.instantdamage);
        ClientRegistry.registerKeyBinding(Keybindings.wither);
        ClientRegistry.registerKeyBinding(Keybindings.paralysis);
        ClientRegistry.registerKeyBinding(Keybindings.possession);
        ClientRegistry.registerKeyBinding(Keybindings.speed);
        ClientRegistry.registerKeyBinding(Keybindings.resized);
        ClientRegistry.registerKeyBinding(Keybindings.firefuse);
        ClientRegistry.registerKeyBinding(Keybindings.randomspawn);
    }
}

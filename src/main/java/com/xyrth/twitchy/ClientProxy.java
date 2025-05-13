package com.xyrth.twitchy;

import com.xyrth.twitchy.client.settings.Keybindings;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(Keybindings.spawnskeleton);
        ClientRegistry.registerKeyBinding(Keybindings.potion);
    }
}

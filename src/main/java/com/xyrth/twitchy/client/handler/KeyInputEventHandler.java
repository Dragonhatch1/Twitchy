package com.xyrth.twitchy.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;

import com.xyrth.twitchy.client.settings.Keybindings;
import com.xyrth.twitchy.network.TwitchyPacket;
import com.xyrth.twitchy.reference.Key;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class KeyInputEventHandler {

    private final SimpleNetworkWrapper networkWrapper;

    public KeyInputEventHandler(SimpleNetworkWrapper networkWrapper) {
        this.networkWrapper = networkWrapper;
    }

    public static Key getPressedKeybinding() {

        if (Keybindings.randomspawn.isPressed()) {
            return Key.RANDOMSPAWN;
        } else if (Keybindings.randompotion.isPressed()) {
            return Key.RANDOMPOTION;
        }
        return Key.UNKNOWN;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event) {
        // LogHelper.info(getPressedKeybinding());

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

        if (Keybindings.randomspawn.isPressed()) {
            Events.randomspawn(player);
        } else if (Keybindings.randompotion.isPressed()) {
            Events.randompotion(player);
        }

        // send packet to server
        networkWrapper.sendToAll(new TwitchyPacket());
    }
}

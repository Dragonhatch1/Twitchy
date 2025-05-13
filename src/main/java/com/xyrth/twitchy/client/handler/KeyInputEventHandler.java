package com.xyrth.twitchy.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

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

        if (Keybindings.spawnskeleton.isPressed()) {
            return Key.SPAWNSKELETON;
        } else if (Keybindings.potion.isPressed()) {
            return Key.POTION;
        }
        return Key.UNKNOWN;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event) {
        // LogHelper.info(getPressedKeybinding());

        if (Keybindings.spawnskeleton.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            Events.spawnskeleton();
        } else if (Keybindings.potion.isPressed()) {
            Events.potion();
        }

        // send packet to server
        networkWrapper.sendToAll(new TwitchyPacket());
    }
}

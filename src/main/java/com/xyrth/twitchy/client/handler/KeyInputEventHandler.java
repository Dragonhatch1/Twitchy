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

        if (Keybindings.skeleton.isPressed()) {
            return Key.SKELETON;
        } else if (Keybindings.instantdamage.isPressed()) {
            return Key.INSTANTDAMAGE;
        } else if (Keybindings.wither.isPressed()) {
            return Key.WITHER;
        } else if (Keybindings.paralysis.isPressed()) {
            return Key.PARALYSIS;
        } else if (Keybindings.possession.isPressed()) {
            return Key.POSSESSION;
        } else if (Keybindings.speed.isPressed()) {
            return Key.SPEED;
        } else if (Keybindings.resized.isPressed()) {
            return Key.RESIZED;
        } else if (Keybindings.firefuse.isPressed()) {
            return Key.FIREFUSE;
        }
        return Key.UNKNOWN;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event) {
        // LogHelper.info(getPressedKeybinding());

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

        if (Keybindings.skeleton.isPressed()) {
            Events.skeleton(player);
        } else if (Keybindings.instantdamage.isPressed()) {
            Events.instantdamage(player);
        } else if (Keybindings.wither.isPressed()) {
            Events.wither(player);
        } else if (Keybindings.paralysis.isPressed()) {
            Events.paralysis(player);
        } else if (Keybindings.possession.isPressed()) {
            Events.possession(player);
        } else if (Keybindings.speed.isPressed()) {
            Events.speed(player);
        } else if (Keybindings.resized.isPressed()) {
            Events.resized(player);
        } else if (Keybindings.firefuse.isPressed()) {
            Events.firefuse(player);
        }

        // send packet to server
        networkWrapper.sendToAll(new TwitchyPacket());
    }
}

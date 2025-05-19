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

    // Setting up Player, World, and the other thingy
    private final SimpleNetworkWrapper networkWrapper;

    public KeyInputEventHandler(SimpleNetworkWrapper networkWrapper) {
        this.networkWrapper = networkWrapper;
    }

    public static Key getPressedKeybinding() {

        if (Keybindings.randomspawn.isPressed()) {
            return Key.RANDOMSPAWN;
        } else if (Keybindings.randompotion.isPressed()) {
            return Key.RANDOMPOTION;
        } else if (Keybindings.mobraid.isPressed()) {
            return Key.MOBRAID;
        } else if (Keybindings.substuff.isPressed()) {
            return Key.SUBSTUFF;
        } else if (Keybindings.spawningtest.isPressed()) {
            return Key.SPAWNINGTEST;
        }
        return Key.UNKNOWN;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event) {
        // LogHelper.info(getPressedKeybinding());
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        // World world = .getEntityWorld();

        if (Keybindings.randomspawn.isPressed()) {
            Events.randomspawn(player);
        } else if (Keybindings.randompotion.isPressed()) {
            Events.randompotion(player);
        } else if (Keybindings.mobraid.isPressed()) {
            Events.mobraid(player);
        } else if (Keybindings.substuff.isPressed()) {
            Events.substuff(player);
        }
        // } else if (Keybindings.spawningtest.isPressed()) {
        // Events.spawningtest(player, world);
        // }

        // send packet to server
        networkWrapper.sendToAll(new TwitchyPacket());
    }
}

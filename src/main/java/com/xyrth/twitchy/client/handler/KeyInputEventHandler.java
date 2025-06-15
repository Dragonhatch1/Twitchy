package com.xyrth.twitchy.client.handler;

import static com.xyrth.twitchy.client.handler.Events.guiclose;
import static com.xyrth.twitchy.client.handler.Events.randomspawn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import com.xyrth.twitchy.client.settings.Keybindings;
import com.xyrth.twitchy.network.TwitchyPacket;
import com.xyrth.twitchy.reference.Key;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class KeyInputEventHandler {

    private final SimpleNetworkWrapper networkWrapper;

    public KeyInputEventHandler(SimpleNetworkWrapper networkWrapper) {
        this.networkWrapper = networkWrapper;
    }

    public static Key getPressedKeybinding() {
        // close but no cigar. Try looking in the GUIOpenEvent and see if we just need to subscribe to that any time a
        // gui is opened.
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
        Minecraft mc = FMLClientHandler.instance()
            .getClient();
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        World world = mc.theWorld;

        if (Keybindings.randomspawn.isPressed()) {
            Events.randomspawn(player);
        } else if (Keybindings.randompotion.isPressed()) {
            Events.randompotion(player);
        } else if (Keybindings.mobraid.isPressed()) {
            Events.mobraid(player);
        } else if (Keybindings.substuff.isPressed()) {
            Events.substuff(player);
        } else if (Keybindings.spawningtest.isPressed()) {
            Events.spawningtest(player, world);
        }

        // send packet to server
        networkWrapper.sendToAll(new TwitchyPacket());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent tick) {
        Minecraft mc = FMLClientHandler.instance()
            .getClient();
        EntityClientPlayerMP player = mc.thePlayer;
        World world = mc.theWorld;

        if (tick.phase == TickEvent.Phase.START) {
            if (mc.theWorld != null && mc.thePlayer != null) {
                if (mc.currentScreen != null) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) {
                        guiclose();
                        randomspawn(player);
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)) {
                        guiclose();
                        Events.randompotion(player);
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD3)) {
                        guiclose();
                        Events.mobraid(player);
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) {
                        guiclose();
                        Events.substuff(player);
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
                        guiclose();
                        Events.spawningtest(player, world);
                    }
                } else {
                    return;
                }
            }
        }
    }
}

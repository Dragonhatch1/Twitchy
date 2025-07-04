package com.xyrth.twitchy.client;

import static com.xyrth.twitchy.event.Events.guiclose;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import com.xyrth.twitchy.event.Events;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class KeyInputEventHandler {

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event) {
        // LogHelper.info(getPressedKeybinding());
        Minecraft mc = FMLClientHandler.instance()
            .getClient();
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

        if (Keybindings.randomspawn.isPressed()) {
            Events.randomspawn(player);
        } else if (Keybindings.randompotion.isPressed()) {
            Events.randompotion(player);
        } else if (Keybindings.mobraid.isPressed()) {
            Events.mobraid(player);
        } else if (Keybindings.substuff.isPressed()) {
            Events.substuff(player);
        } else if (Keybindings.spawningtest.isPressed()) {
            Events.spawningtest(player);
        }

    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent tick) {
        Minecraft mc = FMLClientHandler.instance()
            .getClient();
        EntityClientPlayerMP player = mc.thePlayer;
        World world = mc.theWorld;

        if (tick.phase == TickEvent.Phase.START) {
            if (world != null && player != null) {
                if (mc.currentScreen != null) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) {
                        guiclose();
                        Events.randomspawn(player);
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
                        Events.spawningtest(player);
                    }
                } else {
                    return;
                }
            }
        }
    }
}

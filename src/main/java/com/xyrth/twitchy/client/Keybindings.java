package com.xyrth.twitchy.client;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.xyrth.twitchy.reference.TwitchEvent;

public class Keybindings {

    public static String KEY_CATEGORY = "Twitchy";
    public static KeyBinding randomspawn = new KeyBinding(
        TwitchEvent.RANDOMSPAWN.eventAction,
        Keyboard.KEY_NUMPAD1,
        KEY_CATEGORY);
    public static KeyBinding randompotion = new KeyBinding(
        TwitchEvent.RANDOMPOTION.eventAction,
        Keyboard.KEY_NUMPAD2,
        KEY_CATEGORY);
    public static KeyBinding mobraid = new KeyBinding(
        TwitchEvent.MOBRAID.eventAction,
        Keyboard.KEY_NUMPAD3,
        KEY_CATEGORY);
    public static KeyBinding substuff = new KeyBinding(
        TwitchEvent.SUBSTUFF.eventAction,
        Keyboard.KEY_NUMPAD3,
        KEY_CATEGORY);
    public static KeyBinding spawningtest = new KeyBinding(
        TwitchEvent.SPAWNINGTEST.eventAction,
        Keyboard.KEY_NUMPAD3,
        KEY_CATEGORY);
}

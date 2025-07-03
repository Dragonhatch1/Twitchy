package com.xyrth.twitchy.client;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

public class Keybindings {

    public static String KEY_CATEGORY = "Twitchy";
    public static KeyBinding randomspawn = new KeyBinding("Spawns Random Mpopup", Keyboard.KEY_NUMPAD1, KEY_CATEGORY);
    public static KeyBinding randompotion = new KeyBinding("Spawns Random Potion", Keyboard.KEY_NUMPAD2, KEY_CATEGORY);
    public static KeyBinding mobraid = new KeyBinding("Spawns Mob Raid", Keyboard.KEY_NUMPAD3, KEY_CATEGORY);
    public static KeyBinding substuff = new KeyBinding("Spawn Sub Stuff", Keyboard.KEY_NUMPAD4, KEY_CATEGORY);
    public static KeyBinding spawningtest = new KeyBinding("testing", Keyboard.KEY_NUMPAD5, KEY_CATEGORY);
}

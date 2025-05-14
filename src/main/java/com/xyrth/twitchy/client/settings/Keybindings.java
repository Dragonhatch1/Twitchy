package com.xyrth.twitchy.client.settings;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.xyrth.twitchy.reference.Names;

public class Keybindings {

    public static KeyBinding randomspawn = new KeyBinding(
        Names.Keys.RANDOMSPAWN,
        Keyboard.KEY_NUMPAD1,
        Names.Keys.CATEGORY);
    public static KeyBinding randompotion = new KeyBinding(
        Names.Keys.RANDOMPOTION,
        Keyboard.KEY_NUMPAD2,
        Names.Keys.CATEGORY);
    public static KeyBinding mobraid = new KeyBinding(Names.Keys.MOBRAID, Keyboard.KEY_NUMPAD3, Names.Keys.CATEGORY);
}

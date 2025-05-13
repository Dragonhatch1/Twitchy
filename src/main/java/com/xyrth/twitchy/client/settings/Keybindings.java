package com.xyrth.twitchy.client.settings;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.xyrth.twitchy.reference.Names;

public class Keybindings {

    public static KeyBinding spawnskeleton = new KeyBinding(Names.Keys.SPAWNSKELETON, Keyboard.KEY_NUMPAD9, Names.Keys.CATEGORY);
    public static KeyBinding potion = new KeyBinding(Names.Keys.POTION, Keyboard.KEY_NUMPAD8, Names.Keys.CATEGORY);
}

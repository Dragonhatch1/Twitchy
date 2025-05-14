package com.xyrth.twitchy.client.settings;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.xyrth.twitchy.reference.Names;

public class Keybindings {

    public static KeyBinding randomspawn = new KeyBinding(
        Names.Keys.RANDOMSPAWN,
        Keyboard.KEY_NUMPAD1,
        Names.Keys.CATEGORYMOB);
    public static KeyBinding instantdamage = new KeyBinding(
        Names.Keys.INSTANTDAMAGE,
        Keyboard.KEY_NUMPAD8,
        Names.Keys.CATEGORYPOTIONS);
    public static KeyBinding wither = new KeyBinding(
        Names.Keys.WITHER,
        Keyboard.KEY_NUMPAD7,
        Names.Keys.CATEGORYPOTIONS);
    public static KeyBinding paralysis = new KeyBinding(
        Names.Keys.PARALYSIS,
        Keyboard.KEY_NUMPAD6,
        Names.Keys.CATEGORYPOTIONS);
    public static KeyBinding possession = new KeyBinding(
        Names.Keys.POSSESSION,
        Keyboard.KEY_NUMPAD5,
        Names.Keys.CATEGORYPOTIONS);
    public static KeyBinding speed = new KeyBinding(Names.Keys.SPEED, Keyboard.KEY_NUMPAD4, Names.Keys.CATEGORYPOTIONS);
    public static KeyBinding resized = new KeyBinding(
        Names.Keys.RESIZED,
        Keyboard.KEY_NUMPAD3,
        Names.Keys.CATEGORYPOTIONS);
    public static KeyBinding firefuse = new KeyBinding(
        Names.Keys.FIREFUSE,
        Keyboard.KEY_NUMPAD2,
        Names.Keys.CATEGORYPOTIONS);
    public static KeyBinding randompotion = new KeyBinding(
        Names.Keys.RANDOMPOTION,
        Keyboard.KEY_NUMPAD2,
        Names.Keys.CATEGORYPOTIONS);
}

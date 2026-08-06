package com.twitchy.rewards;

/**
 * Describes one configured action for a reward redemption. Only the fields relevant to
 * {@link #type} need to be set in rewards.json; the rest are ignored.
 *
 * Placeholders usable in {@code command} / {@code message}: {viewer} (Twitch display name),
 * {input} (the viewer's redemption text input, if any), {player} (the resolved Minecraft player
 * name this action targets).
 */
public class RewardAction {

    public RewardActionType type;

    /** GIVE_ITEM: registry name, e.g. "minecraft:diamond" or "minecraft:golden_apple". */
    public String item;
    /** GIVE_ITEM: stack size. */
    public int amount = 1;
    /** GIVE_ITEM: item metadata/damage value. */
    public int metadata = 0;

    /** RUN_COMMAND: command to run as the server, without the leading slash, e.g. "weather rain". */
    public String command;

    /** SPAWN_ENTITY: entity registry/internal name, e.g. "Zombie", "Chicken". */
    public String entity;
    /** SPAWN_ENTITY: how many to spawn. */
    public int count = 1;

    /** SERVER_CHAT_MESSAGE / CLIENT_EFFECT: message text to display. */
    public String message;

    /** CLIENT_EFFECT: optional sound event name to play locally, e.g. "random.levelup". */
    public String sound;

    /**
     * Who an action targets, for GIVE_ITEM / SPAWN_ENTITY:
     * - "broadcaster" (default): the streamer's own configured Minecraft username
     * - "linked": resolved from the viewer's Twitch login via viewer-links.json, falling back to broadcaster
     */
    public String target = "broadcaster";

    public int cameraFlipSeconds = 0;

    /**
     * FOV_CHANGE: degrees to add to (positive) or subtract from (negative) the player's FOV,
     * persists until the next daily reset (see FovEffectManager.DAILY_RESET_HOUR).
     */
    public float fovOffset = 0.0F;

    /**
     * Optional, works with ANY action type: shows a big center-screen toast when redeemed.
     * toastTitle triggers it; toastSubtitle is optional. Both support {viewer}/{input} placeholders.
     */
    public String toastTitle;
    public String toastSubtitle;
}

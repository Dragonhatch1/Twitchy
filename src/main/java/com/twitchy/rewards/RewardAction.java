package com.twitchy.rewards;

import java.util.List;

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
    public int toastType = 3;
    public String toastTitle;
    public String toastSubtitle;

    /** Volume for CLIENT_EFFECT/PLAY_SOUND's sound field. Default 1.0. */
    public float soundVolume = 1.0F;
    /** Pitch for CLIENT_EFFECT/PLAY_SOUND's sound field. Default 1.0 (try 0.5-2.0 for variety). */
    public float soundPitch = 1.0F;

    /**
     * KEY_SEQUENCE_CHALLENGE: explicit sequence, e.g. ["W","A","S","D"]. If null/empty, a random
     * sequence of length challengeLength is generated from W/A/S/D each time it's redeemed.
     */
    public String[] keySequence;

    /** GEAR_UPGRADE: GearSets.json key the viewer must already have unlocked. Null = no requirement. */
    public String requiredUnlockKey;
    /** GEAR_UPGRADE: the GearSets.json key this redemption grants on success. */
    public String unlockKey;

    public static class GearPiece {

        /** Item ID string, same convention as GIVE_ITEM/DEPOSIT_ITEM elsewhere in this project. */
        public String item;
        public int metadata;
        /**
         * 0=held, 1=boots, 2=leggings, 3=chestplate, 4=helmet - confirmed from vanilla's own
         * EntityLiving.getArmorItemForSlot switch.
         */
        public int slot;
    }

    /**
     * GEAR_UPGRADE: minimum accumulated last-hit kills required, in addition to any prevItemReq.
     * 0 = no kill requirement. Spent (deducted) automatically once the redemption succeeds.
     */
    public int requiredKills = 0;

    /** KEY_SEQUENCE_CHALLENGE: how many regular-pool mobs to spawn on failure. 0 = none. */
    public int regularSpawnCount = 0;
    /** KEY_SEQUENCE_CHALLENGE: how many boss-pool mobs to spawn on failure. 0 = none. */
    public int bossSpawnCount = 0;
    /** RESIZE_FOLLOWER: amount to add to the viewer's current scale. Negative to shrink. */
    public float resizeDelta = 0.0F;
    /** HEAL_FOLLOWER: fraction of current max HP to heal, e.g. 0.75 for 75%. */
    public float healPercent = 0.0F;
}

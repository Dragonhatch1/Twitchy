package com.twitchy.rewards;

public enum RewardActionType {
    /** Gives an item stack to the target player. Runs on the server. */
    GIVE_ITEM,
    /** Runs a vanilla/mod command as the server, with placeholders substituted. Runs on the server. */
    RUN_COMMAND,
    /** Spawns an entity near the target player. Runs on the server. */
    SPAWN_ENTITY,
    /** Broadcasts a message in the Minecraft chat to all players. Runs on the server. */
    SERVER_CHAT_MESSAGE,
    /** Pure client-side cosmetic effect (local chat message, sound). No server round-trip needed. */
    CLIENT_EFFECT,
    /** Deposits an item into the configured storage container (see /twitchy setstorage). Runs on the server. */
    DEPOSIT_ITEM,
    /**
     * Camera flip (local, immediate) + reversed gravity and a flipped model visible to everyone
     * else. Runs on both client (camera) and server (gravity + broadcast).
     */
    GRAVITY_FLIP,
    /** Adjusts the player's FOV by a fixed offset until the next daily reset time. Client-only. */
    FOV_CHANGE,
    /** Randomly shuffles the target player's hotbar + main inventory slots. Runs on the server. */
    INVENTORY_SCRAMBLE,
    /** Plays a sound - vanilla, another mod's, or one bundled in Twitchy itself. Client-only, no server round-trip. */
    PLAY_SOUND,
    /**
     * Client-only GUI mini-game: hit a WASD sequence in order before time runs out. Fulfillment is
     * deferred until the challenge actually resolves, unlike the other instant client-side types.
     */
    KEY_SEQUENCE_CHALLENGE,
    /**
     * Grants a chatter a new gear set on their spawned entity, gated behind an optional prerequisite
     * set they must already have equipped. Persists to ChatterGear.json and live-updates their
     * entity immediately if one's currently spawned.
     */
    GEAR_UPGRADE,
    /**
     * Spawns a batch of random mobs near the redeeming streamer, drawn from their own local
     * MobSpawning.json pools - same pipeline the WASD captcha failure uses, just triggered directly
     * by redeeming rather than as a consequence.
     */
    SPAWN_RANDOM_MOBS,
    /**
     * Grows or shrinks the redeeming viewer's follower by resizeDelta (e.g. +0.10 or -0.10),
     * persisted and clamped in ViewerFollowerGear.
     */
    RESIZE_FOLLOWER,
    /**
     * Heals the redeeming viewer's follower by healPercent of its current max HP (scales
     * automatically with any future max-HP changes). Refunds if not currently spawned or already
     * at full health.
     */
    HEAL_FOLLOWER
}

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
    DEPOSIT_ITEM
}

package com.twitchy.chat;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.twitchy.api.TwitchModels.ChatMessageEvent;
import com.twitchy.client.FollowerModelRegistry;
import com.twitchy.client.TwitchSessionManager;
import com.twitchy.entity.ViewerFollowerProfile;
import com.twitchy.rewards.RewardAction.GearPiece;

/** Client-side only. Watches incoming Twitch chat messages for configured trigger phrases. */
public final class ChatCommandManager {

    private ChatCommandManager() {}

    private static final Map<String, Integer> SLOT_NAMES = Map.of(
        "head", 4, "chest", 3, "legs", 2, "feet", 1, "weapon", 0);

    public static void handleChatMessage(ChatMessageEvent event) {
        if (event.message == null || event.message.text == null) return;

        String firstWord = event.message.text.trim()
            .split("\\s+", 2)[0];
        if (firstWord.equalsIgnoreCase("!kills")) {
            handleKillsCommand(event);
            return;
        }

        if (firstWord.equalsIgnoreCase("!setname")) {
            handleSetNameCommand(event);
            return;
        }

        if (firstWord.equalsIgnoreCase("!model")) {
            handleModelsCommand(event);
            return;
        }

        if (firstWord.equalsIgnoreCase("!equip")) {
            handleEquipCommand(event);
            return;
        }

        Optional<ChatCommand> maybeCommand = ChatCommandConfig.findForMessage(event.message.text);
        if (maybeCommand.isEmpty()) return;
        ChatCommand command = maybeCommand.get();

        String key = command.trigger.toLowerCase();
        respond(command, event.chatter_user_name);
    }

    private static void handleKillsCommand(ChatMessageEvent event) {
        int kills = ViewerFollowerProfile.getLastHits(event.chatter_user_id);
        int bossKills = ViewerFollowerProfile.getBossLastHits(event.chatter_user_id);
        String name = event.chatter_user_name == null ? "" : event.chatter_user_name;
        String response = name + " has "
            + kills
            + " last hit"
            + (kills == 1 ? "" : "s")
            + " & "
            + bossKills
            + " boss kill"
            + (bossKills == 1 ? "" : "s")
            + "!";

        TwitchSessionManager.INSTANCE.sendChatMessage(response)
            .exceptionally(ex -> {
                com.twitchy.Twitchy.LOG.warn("Failed to respond to !kills: {}", ex.getMessage());
                return null;
            });
    }

    private static void handleSetNameCommand(ChatMessageEvent event) {
        String[] parts = event.message.text.trim()
            .split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            TwitchSessionManager.INSTANCE
                .sendChatMessage(event.chatter_user_name + " - usage: !setname <your Minecraft username>");
            return;
        }

        String username = parts[1].trim()
            .replaceAll("[^a-zA-Z0-9_]", "");
        if (username.isBlank()) {
            TwitchSessionManager.INSTANCE
                .sendChatMessage(event.chatter_user_name + " - that doesn't look like a valid Minecraft username.");
            return;
        }

        ViewerFollowerProfile.setMinecraftUsername(event.chatter_user_id, username);

        TwitchSessionManager.INSTANCE
            .sendChatMessage(event.chatter_user_name + " - your entity will now use " + username + "'s skin!");
    }

    private static void handleModelsCommand(ChatMessageEvent event) {
        String[] parts = event.message.text.trim()
            .split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            TwitchSessionManager.INSTANCE.sendChatMessage(
                event.chatter_user_name + " - usage: !model <model>. Available: "
                    + String.join(", ", FollowerModelRegistry.availableKeys()));
            return;
        }

        String choice = parts[1].trim()
            .toUpperCase();
        if (!FollowerModelRegistry.isAvailable(choice)) {
            TwitchSessionManager.INSTANCE.sendChatMessage(
                event.chatter_user_name + " - not available. Try: "
                    + String.join(", ", FollowerModelRegistry.availableKeys()));
            return;
        }

        ViewerFollowerProfile.setFollowerModel(event.chatter_user_id, choice);
        com.twitchy.network.PacketHandler
            .sendToServer(new com.twitchy.network.FollowerModelPacket(event.chatter_user_id, choice));
        TwitchSessionManager.INSTANCE
            .sendChatMessage(event.chatter_user_name + " - your follower is now " + choice.toLowerCase() + "!");
    }

    private static void handleEquipCommand(ChatMessageEvent event) {
        String[] parts = event.message.text.trim().split("\\s+", 3);
        if (parts.length < 2) {
            TwitchSessionManager.INSTANCE.sendChatMessage(
                event.chatter_user_name + " - usage: !equip <head|chest|legs|feet|weapon> [item]");
            return;
        }

        Integer slot = SLOT_NAMES.get(parts[1].toLowerCase());
        if (slot == null) {
            TwitchSessionManager.INSTANCE.sendChatMessage(
                event.chatter_user_name + " - valid slots: head, chest, legs, feet, weapon");
            return;
        }

        // "!equip <slot>" with no item - list what's available for that slot
        if (parts.length < 3) {
            List<String> unlocked = ViewerFollowerProfile.getUnlocked(event.chatter_user_id);
            List<GearPiece> options = com.twitchy.entity.GearSets.findAllPiecesForSlot(unlocked, slot);
            if (options.isEmpty()) {
                TwitchSessionManager.INSTANCE.sendChatMessage(
                    event.chatter_user_name + " - you haven't unlocked anything for that slot yet!");
                return;
            }
            String names = options.stream()
                .map(p -> p.item.contains(":") ? p.item.substring(p.item.indexOf(':') + 1) : p.item)
                .collect(java.util.stream.Collectors.joining(", "));
            TwitchSessionManager.INSTANCE.sendChatMessage(
                event.chatter_user_name + " - available for " + parts[1].toLowerCase() + ": " + names);
            return;
        }

        boolean equipped = ViewerFollowerProfile.equipPiece(event.chatter_user_id, slot, parts[2]);
        if (!equipped) {
            TwitchSessionManager.INSTANCE.sendChatMessage(
                event.chatter_user_name + " - you haven't unlocked that for that slot!");
            return;
        }

        List<GearPiece> gear = ViewerFollowerProfile.getEquippedGear(event.chatter_user_id);
        com.twitchy.network.PacketHandler.sendToServer(
            new com.twitchy.network.ApplyGearPacket(event.chatter_user_id, gear));

        TwitchSessionManager.INSTANCE.sendChatMessage(
            event.chatter_user_name + " - equipped " + parts[2] + "!");
    }

    /** Simulates a trigger locally for testing (/twitchy testchat), bypassing Twitch entirely. */
    public static boolean testTrigger(String trigger) {
        Optional<ChatCommand> maybeCommand = ChatCommandConfig.findForMessage(trigger);
        if (maybeCommand.isEmpty()) return false;
        respond(maybeCommand.get(), "TestViewer");
        return true;
    }

    private static void respond(ChatCommand command, String viewerName) {
        String response = command.response.replace("{viewer}", viewerName == null ? "" : viewerName);
        TwitchSessionManager.INSTANCE.sendChatMessage(response)
            .exceptionally(ex -> {
                com.twitchy.Twitchy.LOG
                    .warn("Failed to respond to chat command '{}': {}", command.trigger, ex.getMessage());
                return null;
            });
    }
}

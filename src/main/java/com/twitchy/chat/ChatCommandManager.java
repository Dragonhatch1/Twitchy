package com.twitchy.chat;

import java.util.Optional;

import com.twitchy.api.TwitchModels.ChatMessageEvent;
import com.twitchy.client.TwitchSessionManager;
import com.twitchy.entity.ViewerFollowerGear;

/** Client-side only. Watches incoming Twitch chat messages for configured trigger phrases. */
public final class ChatCommandManager {

    private ChatCommandManager() {}

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

        Optional<ChatCommand> maybeCommand = ChatCommandConfig.findForMessage(event.message.text);
        if (maybeCommand.isEmpty()) return;
        ChatCommand command = maybeCommand.get();

        String key = command.trigger.toLowerCase();
        respond(command, event.chatter_user_name);
    }

    private static void handleKillsCommand(ChatMessageEvent event) {
        int kills = ViewerFollowerGear.getLastHits(event.chatter_user_id);
        int bossKills = ViewerFollowerGear.getBossLastHits(event.chatter_user_id);
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
        String[] parts = event.message.text.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            TwitchSessionManager.INSTANCE.sendChatMessage(
                event.chatter_user_name + " - usage: !setname <your Minecraft username>");
            return;
        }

        String username = parts[1].trim();
        ViewerFollowerGear.setMinecraftUsername(event.chatter_user_id, username);

        TwitchSessionManager.INSTANCE.sendChatMessage(
            event.chatter_user_name + " - your entity will now use " + username + "'s skin!");
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

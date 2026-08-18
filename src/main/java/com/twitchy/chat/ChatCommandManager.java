package com.twitchy.chat;

import java.util.Optional;

import com.twitchy.Twitchy;
import com.twitchy.api.TwitchModels.ChatMessageEvent;
import com.twitchy.client.TwitchSessionManager;
import com.twitchy.entity.ViewerFollowerGear;

/** Client-side only. Watches incoming Twitch chat messages for configured trigger phrases. */
public final class ChatCommandManager {

    private ChatCommandManager() {}

    public static void handleChatMessage(ChatMessageEvent event) {
        if (event.message == null || event.message.text == null) return;

        String trimmed = event.message.text.trim();
        if (trimmed.equalsIgnoreCase("!kills")) {
            handleKillsCommand(event);
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
        String name = event.chatter_user_name == null ? "" : event.chatter_user_name;
        String response = name + " has " + kills + " last hit" + (kills == 1 ? "" : "s") + "!";

        TwitchSessionManager.INSTANCE.sendChatMessage(response)
            .exceptionally(ex -> {
                Twitchy.LOG.warn("Failed to respond to !kills: {}", ex.getMessage());
                return null;
            });
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
                Twitchy.LOG.warn("Failed to respond to chat command '{}': {}", command.trigger, ex.getMessage());
                return null;
            });
    }
}

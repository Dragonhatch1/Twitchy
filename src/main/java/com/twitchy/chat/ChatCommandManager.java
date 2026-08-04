package com.twitchy.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.twitchy.Twitchy;
import com.twitchy.api.TwitchModels.ChatMessageEvent;
import com.twitchy.client.TwitchSessionManager;

/** Client-side only. Watches incoming Twitch chat messages for configured trigger phrases. */
public final class ChatCommandManager {

    private static final Map<String, Long> cooldownUntil = new HashMap<>();

    private ChatCommandManager() {}

    public static void handleChatMessage(ChatMessageEvent event) {
        if (event.message == null || event.message.text == null) return;

        Optional<ChatCommand> maybeCommand = ChatCommandConfig.findForMessage(event.message.text);
        if (maybeCommand.isEmpty()) return;
        ChatCommand command = maybeCommand.get();

        String key = command.trigger.toLowerCase();
        long now = System.currentTimeMillis();
        Long until = cooldownUntil.get(key);
        if (until != null && now < until) return; // on cooldown, ignore silently

        cooldownUntil.put(key, now + (command.cooldownSeconds * 1000L));
        respond(command, event.chatter_user_name);
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

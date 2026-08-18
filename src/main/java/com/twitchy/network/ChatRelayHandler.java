package com.twitchy.network;

import com.twitchy.Twitchy;
import com.twitchy.client.TwitchSessionManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ChatRelayHandler implements IMessageHandler<ChatRelayPacket, IMessage> {

    @Override
    public IMessage onMessage(ChatRelayPacket message, MessageContext ctx) {
        if (message.message == null || message.message.isBlank()) return null;
        TwitchSessionManager.INSTANCE.sendChatMessage(message.message)
            .exceptionally(ex -> {
                Twitchy.LOG.error("Failed to relay message to Twitch chat", ex);
                return null;
            });
        return null;
    }
}

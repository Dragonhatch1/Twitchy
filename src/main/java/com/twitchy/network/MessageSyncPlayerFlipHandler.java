package com.twitchy.network;

import com.twitchy.client.PlayerFlipRenderState;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncPlayerFlipHandler implements IMessageHandler<MessageSyncPlayerFlip, IMessage> {

    @Override
    public IMessage onMessage(MessageSyncPlayerFlip message, MessageContext ctx) {
        if (message.flipped) {
            PlayerFlipRenderState.add(message.playerId);
        } else {
            PlayerFlipRenderState.remove(message.playerId);
        }
        return null;
    }
}

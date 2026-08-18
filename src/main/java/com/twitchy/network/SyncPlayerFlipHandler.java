package com.twitchy.network;

import com.twitchy.client.PlayerFlipRenderState;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SyncPlayerFlipHandler implements IMessageHandler<SyncPlayerFlipPacket, IMessage> {

    @Override
    public IMessage onMessage(SyncPlayerFlipPacket message, MessageContext ctx) {
        if (message.flipped) {
            PlayerFlipRenderState.add(message.playerId);
        } else {
            PlayerFlipRenderState.remove(message.playerId);
        }
        return null;
    }
}

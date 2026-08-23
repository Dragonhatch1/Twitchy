package com.twitchy.network;

import com.twitchy.entity.ViewerFollowerManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class FollowerModelHandler implements IMessageHandler<FollowerModelPacket, IMessage> {

    @Override
    public IMessage onMessage(FollowerModelPacket message, MessageContext ctx) {
        ViewerFollowerManager.setFollowerModel(message.userId, message.model);
        return null;
    }
}

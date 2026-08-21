package com.twitchy.network;

import com.twitchy.entity.ViewerFollowerManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ResizeFollowerHandler implements IMessageHandler<ResizeFollowerPacket, IMessage> {

    @Override
    public IMessage onMessage(ResizeFollowerPacket message, MessageContext ctx) {
        ViewerFollowerManager.applyScale(message.userId, message.newScale);
        return null;
    }
}

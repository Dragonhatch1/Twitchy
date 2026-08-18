package com.twitchy.network;

import com.twitchy.entity.ViewerFollowerManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ApplyGearHandler implements IMessageHandler<ApplyGearPacket, IMessage> {

    @Override
    public IMessage onMessage(ApplyGearPacket message, MessageContext ctx) {
        ViewerFollowerManager.applyGear(message.viewerUserId, message.pieces);
        return null;
    }
}

package com.twitchy.network;

import com.twitchy.entity.ViewerFollowerManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DespawnAllViewerFollowersHandler implements IMessageHandler<DespawnAllViewerFollowersPacket, IMessage> {

    @Override
    public IMessage onMessage(DespawnAllViewerFollowersPacket message, MessageContext ctx) {
        ViewerFollowerManager.despawnAll();
        return null;
    }
}

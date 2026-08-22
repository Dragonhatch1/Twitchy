package com.twitchy.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.twitchy.entity.ViewerFollowerManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SyncViewerListHandler implements IMessageHandler<SyncViewerListPacket, IMessage> {

    @Override
    public IMessage onMessage(SyncViewerListPacket message, MessageContext ctx) {
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        if (sender == null) return null;
        ViewerFollowerManager.reconcile(
            sender,
            message.userIds,
            message.userLogins,
            message.gearPerUser,
            message.minecraftUsernames,
            message.scales,
            message.followerModels);
        return null;
    }
}

package com.twitchy.network;

import com.twitchy.entity.ViewerFollowerManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageSyncViewerListHandler implements IMessageHandler<MessageSyncViewerList, IMessage> {

    @Override
    public IMessage onMessage(MessageSyncViewerList message, MessageContext ctx) {
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        if (sender == null) return null;
        ViewerFollowerManager.reconcile(sender, message.userIds, message.userLogins);
        return null;
    }
}

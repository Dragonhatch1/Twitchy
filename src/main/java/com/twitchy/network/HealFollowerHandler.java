package com.twitchy.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.twitchy.entity.ViewerFollowerManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class HealFollowerHandler implements IMessageHandler<HealFollowerPacket, IMessage> {

    @Override
    public IMessage onMessage(HealFollowerPacket message, MessageContext ctx) {
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        if (sender == null) return null;

        boolean success = ViewerFollowerManager.healFollower(message.userId, message.healPercent);
        PacketHandler.sendTo(new RedeemResultPacket(message.redemptionId, message.twitchRewardId, success), sender);
        return null;
    }
}

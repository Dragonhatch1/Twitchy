package com.twitchy.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.twitchy.entity.MobSpawnManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class RequestMobSpawnHandler implements IMessageHandler<RequestMobSpawnPacket, IMessage> {

    @Override
    public IMessage onMessage(RequestMobSpawnPacket message, MessageContext ctx) {
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        if (sender == null) return null;
        MobSpawnManager.spawnRequested(sender, message.entityNames, message.boss);
        return null;
    }
}

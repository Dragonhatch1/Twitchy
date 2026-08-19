package com.twitchy.network;

import com.twitchy.entity.MobSpawningConfig;
import com.twitchy.entity.ViewerFollowerGear;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class KillCreditHandler implements IMessageHandler<KillCreditPacket, IMessage> {

    @Override
    public IMessage onMessage(KillCreditPacket message, MessageContext ctx) {
        ViewerFollowerGear.addKill(message.viewerUserId);
        if (message.killedEntityName != null && MobSpawningConfig.isAllowed(message.killedEntityName)) {
            ViewerFollowerGear.addBossKill(message.viewerUserId);
        }
        return null;
    }
}

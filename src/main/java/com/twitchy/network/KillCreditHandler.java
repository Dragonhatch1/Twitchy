package com.twitchy.network;

import com.twitchy.entity.MobSpawningConfig;
import com.twitchy.entity.ViewerFollowerProfile;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class KillCreditHandler implements IMessageHandler<KillCreditPacket, IMessage> {

    @Override
    public IMessage onMessage(KillCreditPacket message, MessageContext ctx) {
        ViewerFollowerProfile.addKill(message.viewerUserId);
        if (message.killedEntityName != null && MobSpawningConfig.isBoss(message.killedEntityName)) {
            ViewerFollowerProfile.addBossKill(message.viewerUserId);
        }
        return null;
    }
}

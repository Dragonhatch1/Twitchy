package com.twitchy.network;

import java.util.List;

import com.twitchy.Twitchy;
import com.twitchy.entity.MobSpawningConfig;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class RerollMobRequestHandler implements IMessageHandler<RerollMobRequestPacket, IMessage> {

    private static final int MAX_ATTEMPTS = 5;

    @Override
    public IMessage onMessage(RerollMobRequestPacket message, MessageContext ctx) {
        if (message.attemptsRemaining <= 0) {
            Twitchy.LOG.warn(
                "Gave up rerolling a {} mob after {} attempts - your local pool may not match the server's.",
                message.boss ? "boss" : "regular",
                MAX_ATTEMPTS);
            return null;
        }

        List<String> pick = MobSpawningConfig.pickRandom(1, message.boss);
        if (pick.isEmpty()) return null; // local pool for this category is empty - nothing to send

        PacketHandler.sendToServer(new RequestMobSpawnPacket(pick, message.boss));
        return null;
    }
}

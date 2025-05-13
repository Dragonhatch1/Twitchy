package com.xyrth.twitchy.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import com.xyrth.twitchy.Twitchy;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class TwitchyPacketHandler implements IMessageHandler<TwitchyPacket, IMessage> {

    @Override
    public IMessage onMessage(TwitchyPacket message, MessageContext ctx) {
        // Server-Side Handling
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;

        if (player != null) {
            player.addChatMessage(new ChatComponentText("Spawning Mobs"));

            // Send an update to the client to sync the changes
            Twitchy.networkWrapper.sendToAll(new TwitchyPacket());
        }

        return null;
    }
}

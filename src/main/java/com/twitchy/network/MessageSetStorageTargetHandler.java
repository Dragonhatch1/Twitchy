package com.twitchy.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import com.twitchy.Config;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageSetStorageTargetHandler implements IMessageHandler<MessageSetStorageTarget, IMessage> {

    @Override
    public IMessage onMessage(MessageSetStorageTarget message, MessageContext ctx) {
        Config.setStorageTarget(message.x, message.y, message.z, message.dimension);
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        if (sender != null) {
            sender.addChatMessage(
                new ChatComponentText(
                    "[Twitchy] Storage target set to (" + message.x
                        + ", "
                        + message.y
                        + ", "
                        + message.z
                        + ") in dimension "
                        + message.dimension
                        + "."));
        }
        return null;
    }
}

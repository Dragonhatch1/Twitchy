package com.twitchy.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/** Sent server -> client so in-game events (commands, deaths, etc.) can post to Twitch chat. */
public class MessageChatRelay implements IMessage {

    public String message;

    public MessageChatRelay() {}

    public MessageChatRelay(String message) {
        this.message = message;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, message == null ? "" : message);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        message = ByteBufUtils.readUTF8String(buf);
    }
}

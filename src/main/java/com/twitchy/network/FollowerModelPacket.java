package com.twitchy.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class FollowerModelPacket implements IMessage {

    public String userId;
    public String model;

    public FollowerModelPacket() {}

    public FollowerModelPacket(String userId, String model) {
        this.userId = userId;
        this.model = model;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, userId == null ? "" : userId);
        ByteBufUtils.writeUTF8String(buf, model == null ? "BIPED" : model);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        userId = ByteBufUtils.readUTF8String(buf);
        model = ByteBufUtils.readUTF8String(buf);
    }
}

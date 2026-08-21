package com.twitchy.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class ResizeFollowerPacket implements IMessage {

    public String userId;
    public float newScale;

    public ResizeFollowerPacket() {}

    public ResizeFollowerPacket(String userId, float newScale) {
        this.userId = userId;
        this.newScale = newScale;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = (userId == null ? "" : userId).getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        buf.writeFloat(newScale);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int len = buf.readInt();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        userId = new String(bytes);
        newScale = buf.readFloat();
    }
}

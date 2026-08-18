package com.twitchy.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class KillCreditPacket implements IMessage {

    public String viewerUserId;

    public KillCreditPacket() {}

    public KillCreditPacket(String viewerUserId) {
        this.viewerUserId = viewerUserId;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = (viewerUserId == null ? "" : viewerUserId).getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int len = buf.readInt();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        viewerUserId = new String(bytes);
    }
}

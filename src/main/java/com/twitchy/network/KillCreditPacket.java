package com.twitchy.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class KillCreditPacket implements IMessage {

    public String viewerUserId;
    public String killedEntityName;

    public KillCreditPacket() {}

    public KillCreditPacket(String viewerUserId, String killedEntityName) {
        this.viewerUserId = viewerUserId;
        this.killedEntityName = killedEntityName;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeString(buf, viewerUserId);
        writeString(buf, killedEntityName);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        viewerUserId = readString(buf);
        killedEntityName = readString(buf);
    }

    private void writeString(ByteBuf buf, String s) {
        byte[] bytes = (s == null ? "" : s).getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    private String readString(ByteBuf buf) {
        int len = buf.readInt();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes);
    }
}

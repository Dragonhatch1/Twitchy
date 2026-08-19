package com.twitchy.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class RerollMobRequestPacket implements IMessage {

    public boolean boss;
    public int attemptsRemaining;

    public RerollMobRequestPacket() {}

    public RerollMobRequestPacket(boolean boss, int attemptsRemaining) {
        this.boss = boss;
        this.attemptsRemaining = attemptsRemaining;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(boss);
        buf.writeInt(attemptsRemaining);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boss = buf.readBoolean();
        attemptsRemaining = buf.readInt();
    }
}

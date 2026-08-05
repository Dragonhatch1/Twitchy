package com.twitchy.network;

import java.util.UUID;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageSyncPlayerFlip implements IMessage {

    public UUID playerId;
    public boolean flipped;

    public MessageSyncPlayerFlip() {}

    public MessageSyncPlayerFlip(UUID playerId, boolean flipped) {
        this.playerId = playerId;
        this.flipped = flipped;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(playerId.getMostSignificantBits());
        buf.writeLong(playerId.getLeastSignificantBits());
        buf.writeBoolean(flipped);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        long most = buf.readLong();
        long least = buf.readLong();
        playerId = new UUID(most, least);
        flipped = buf.readBoolean();
    }
}

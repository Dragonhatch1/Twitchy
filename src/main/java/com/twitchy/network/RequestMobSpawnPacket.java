package com.twitchy.network;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class RequestMobSpawnPacket implements IMessage {

    public List<String> entityNames = new ArrayList<>();
    public boolean boss;

    public RequestMobSpawnPacket() {}

    public RequestMobSpawnPacket(List<String> entityNames, boolean boss) {
        this.entityNames = entityNames;
        this.boss = boss;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(boss);
        buf.writeInt(entityNames.size());
        for (String name : entityNames) {
            byte[] bytes = name.getBytes();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boss = buf.readBoolean();
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            int len = buf.readInt();
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            entityNames.add(new String(bytes));
        }
    }
}

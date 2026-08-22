package com.twitchy.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class HealFollowerPacket implements IMessage {

    public String userId;
    public String redemptionId;
    public String twitchRewardId;
    public float healPercent;

    public HealFollowerPacket() {}

    public HealFollowerPacket(String userId, String redemptionId, String twitchRewardId, float healPercent) {
        this.userId = userId;
        this.redemptionId = redemptionId;
        this.twitchRewardId = twitchRewardId;
        this.healPercent = healPercent;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, userId == null ? "" : userId);
        ByteBufUtils.writeUTF8String(buf, redemptionId == null ? "" : redemptionId);
        ByteBufUtils.writeUTF8String(buf, twitchRewardId == null ? "" : twitchRewardId);
        buf.writeFloat(healPercent);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        userId = ByteBufUtils.readUTF8String(buf);
        redemptionId = ByteBufUtils.readUTF8String(buf);
        twitchRewardId = ByteBufUtils.readUTF8String(buf);
        healPercent = buf.readFloat();
    }
}

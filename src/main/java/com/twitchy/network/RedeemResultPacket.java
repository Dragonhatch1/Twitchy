package com.twitchy.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/** Sent server -> client so the owning client can mark the Twitch redemption FULFILLED or CANCELED. */
public class RedeemResultPacket implements IMessage {

    public String redemptionId;
    public String twitchRewardId;
    public boolean success;

    public RedeemResultPacket() {}

    public RedeemResultPacket(String redemptionId, String twitchRewardId, boolean success) {
        this.redemptionId = redemptionId;
        this.twitchRewardId = twitchRewardId;
        this.success = success;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, redemptionId == null ? "" : redemptionId);
        ByteBufUtils.writeUTF8String(buf, twitchRewardId == null ? "" : twitchRewardId);
        buf.writeBoolean(success);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        redemptionId = ByteBufUtils.readUTF8String(buf);
        twitchRewardId = ByteBufUtils.readUTF8String(buf);
        success = buf.readBoolean();
    }
}

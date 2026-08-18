package com.twitchy.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class RedeemActionPacket implements IMessage {

    public String rewardKey;
    public String viewerLogin;
    public String viewerDisplayName;
    public String viewerUserId;
    public String userInput;
    public String redemptionId;
    public String twitchRewards;

    public RedeemActionPacket() {}

    public RedeemActionPacket(String rewardKey, String viewerLogin, String viewerDisplayName, String viewerUserId,
        String userInput, String redemptionId, String twitchRewards) {
        this.rewardKey = rewardKey;
        this.viewerLogin = viewerLogin;
        this.viewerDisplayName = viewerDisplayName;
        this.viewerUserId = viewerUserId;
        this.userInput = userInput;
        this.redemptionId = redemptionId;
        this.twitchRewards = twitchRewards;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, nullToEmpty(rewardKey));
        ByteBufUtils.writeUTF8String(buf, nullToEmpty(viewerLogin));
        ByteBufUtils.writeUTF8String(buf, nullToEmpty(viewerDisplayName));
        ByteBufUtils.writeUTF8String(buf, nullToEmpty(viewerUserId));
        ByteBufUtils.writeUTF8String(buf, nullToEmpty(userInput));
        ByteBufUtils.writeUTF8String(buf, nullToEmpty(redemptionId));
        ByteBufUtils.writeUTF8String(buf, nullToEmpty(twitchRewards));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        rewardKey = ByteBufUtils.readUTF8String(buf);
        viewerLogin = ByteBufUtils.readUTF8String(buf);
        viewerDisplayName = ByteBufUtils.readUTF8String(buf);
        viewerUserId = ByteBufUtils.readUTF8String(buf);
        userInput = ByteBufUtils.readUTF8String(buf);
        redemptionId = ByteBufUtils.readUTF8String(buf);
        twitchRewards = ByteBufUtils.readUTF8String(buf);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

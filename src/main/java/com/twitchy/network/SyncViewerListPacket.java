package com.twitchy.network;

import java.util.ArrayList;
import java.util.List;

import com.twitchy.api.TwitchModels;
import com.twitchy.entity.ViewerFollowerGear;
import com.twitchy.rewards.RewardAction.GearPiece;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SyncViewerListPacket implements IMessage {

    public List<String> userIds = new ArrayList<>();
    public List<String> userLogins = new ArrayList<>();
    public List<List<GearPiece>> gearPerUser = new ArrayList<>();
    public List<String> minecraftUsernames = new ArrayList<>();

    public SyncViewerListPacket() {}

    public SyncViewerListPacket(List<TwitchModels.Chatter> chatters) {
        for (TwitchModels.Chatter c : chatters) {
            userIds.add(c.user_id);
            userLogins.add(c.user_login);
            gearPerUser.add(ViewerFollowerGear.getGear(c.user_id));
            minecraftUsernames.add(ViewerFollowerGear.getMinecraftUsername(c.user_id));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(userIds.size());
        for (int i = 0; i < userIds.size(); i++) {
            writeString(buf, userIds.get(i));
            writeString(buf, userLogins.get(i));
            List<GearPiece> gear = gearPerUser.get(i);
            buf.writeInt(gear.size());
            for (GearPiece piece : gear) {
                writeString(buf, piece.item);
                buf.writeInt(piece.metadata);
                buf.writeInt(piece.slot);
            }
            writeString(buf, minecraftUsernames.get(i));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            userIds.add(readString(buf));
            userLogins.add(readString(buf));
            int gearCount = buf.readInt();
            List<GearPiece> gear = new ArrayList<>();
            for (int g = 0; g < gearCount; g++) {
                GearPiece piece = new GearPiece();
                piece.item = readString(buf);
                piece.metadata = buf.readInt();
                piece.slot = buf.readInt();
                gear.add(piece);
            }
            gearPerUser.add(gear);
            minecraftUsernames.add(readString(buf));
        }
    }

    private void writeString(ByteBuf buf, String s) {
        byte[] bytes = s.getBytes();
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

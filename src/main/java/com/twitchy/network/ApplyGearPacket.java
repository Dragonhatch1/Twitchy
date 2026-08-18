package com.twitchy.network;

import java.util.ArrayList;
import java.util.List;

import com.twitchy.rewards.RewardAction.GearPiece;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class ApplyGearPacket implements IMessage {

    public String viewerUserId;
    public List<GearPiece> pieces = new ArrayList<>();

    public ApplyGearPacket() {}

    public ApplyGearPacket(String viewerUserId, List<GearPiece> pieces) {
        this.viewerUserId = viewerUserId;
        this.pieces = pieces;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeString(buf, viewerUserId == null ? "" : viewerUserId);
        buf.writeInt(pieces.size());
        for (GearPiece p : pieces) {
            writeString(buf, p.item);
            buf.writeInt(p.metadata);
            buf.writeInt(p.slot);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        viewerUserId = readString(buf);
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            GearPiece p = new GearPiece();
            p.item = readString(buf);
            p.metadata = buf.readInt();
            p.slot = buf.readInt();
            pieces.add(p);
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

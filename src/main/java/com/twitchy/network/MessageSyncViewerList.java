package com.twitchy.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.twitchy.api.TwitchModels;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageSyncViewerList implements IMessage {

    public List<String> userIds = new ArrayList<>();
    public List<String> userLogins = new ArrayList<>();

    public MessageSyncViewerList() {}

    public MessageSyncViewerList(List<TwitchModels.Chatter> chatters) {
        for (TwitchModels.Chatter c : chatters) {
            userIds.add(c.user_id);
            userLogins.add(c.user_login);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(userIds.size());
        for (int i = 0; i < userIds.size(); i++) {
            writeString(buf, userIds.get(i));
            writeString(buf, userLogins.get(i));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            userIds.add(readString(buf));
            userLogins.add(readString(buf));
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

package com.twitchy.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.twitchy.Twitchy;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class PacketHandler {

    public static SimpleNetworkWrapper WRAPPER;

    private static int nextId = 0;

    private PacketHandler() {}

    public static void init() {
        WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(Twitchy.MODID);
        WRAPPER.registerMessage(
            MessageRedeemActionHandler.class,
            MessageRedeemAction.class,
            nextId++,
            cpw.mods.fml.relauncher.Side.SERVER);
        WRAPPER.registerMessage(
            MessageChatRelayHandler.class,
            MessageChatRelay.class,
            nextId++,
            cpw.mods.fml.relauncher.Side.CLIENT);
        WRAPPER.registerMessage(
            MessageRedeemResultHandler.class,
            MessageRedeemResult.class,
            nextId++,
            cpw.mods.fml.relauncher.Side.CLIENT);
    }

    public static void sendToServer(IMessage message) {
        WRAPPER.sendToServer(message);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        WRAPPER.sendTo(message, player);
    }
}

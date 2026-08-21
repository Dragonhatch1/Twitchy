package com.twitchy.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.twitchy.Twitchy;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class PacketHandler {

    public static SimpleNetworkWrapper WRAPPER;

    private static int nextId = 0;

    private PacketHandler() {}

    public static void init() {
        WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(Twitchy.MODID);
        WRAPPER.registerMessage(
            RedeemActionHandler.class,
            RedeemActionPacket.class,
            nextId++,
            cpw.mods.fml.relauncher.Side.SERVER);
        WRAPPER.registerMessage(
            ChatRelayHandler.class,
            ChatRelayPacket.class,
            nextId++,
            cpw.mods.fml.relauncher.Side.CLIENT);
        WRAPPER.registerMessage(
            RedeemResultHandler.class,
            RedeemResultPacket.class,
            nextId++,
            cpw.mods.fml.relauncher.Side.CLIENT);
        WRAPPER.registerMessage(SetStorageTargetHandler.class, SetStorageTargetPacket.class, nextId++, Side.SERVER);
        WRAPPER.registerMessage(SyncPlayerFlipHandler.class, SyncPlayerFlipPacket.class, nextId++, Side.CLIENT);
        WRAPPER.registerMessage(SyncViewerListHandler.class, SyncViewerListPacket.class, nextId++, Side.SERVER);
        WRAPPER.registerMessage(
            DespawnAllViewerFollowersHandler.class,
            DespawnAllViewerFollowersPacket.class,
            nextId++,
            Side.SERVER);
        WRAPPER.registerMessage(ApplyGearHandler.class, ApplyGearPacket.class, nextId++, Side.SERVER);
        WRAPPER.registerMessage(KillCreditHandler.class, KillCreditPacket.class, nextId++, Side.CLIENT);
        WRAPPER.registerMessage(RequestMobSpawnHandler.class, RequestMobSpawnPacket.class, nextId++, Side.SERVER);
        WRAPPER.registerMessage(RerollMobRequestHandler.class, RerollMobRequestPacket.class, nextId++, Side.CLIENT);
        WRAPPER.registerMessage(ResizeFollowerHandler.class, ResizeFollowerPacket.class, nextId++, Side.SERVER);
    }

    public static void sendToServer(IMessage message) {
        WRAPPER.sendToServer(message);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        WRAPPER.sendTo(message, player);
    }

    public static void sendToAll(IMessage message) {
        WRAPPER.sendToAll(message);
    }
}

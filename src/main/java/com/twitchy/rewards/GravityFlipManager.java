package com.twitchy.rewards;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

import com.twitchy.network.MessageSyncPlayerFlip;
import com.twitchy.network.PacketHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Server-side. Tracks which players currently have reversed gravity active, applies the upward
 * "fall" every tick, and broadcasts the flipped/unflipped state to ALL clients so everyone's
 * renderer (not just the affected player's) knows to draw that player's model upside-down.
 */
public class GravityFlipManager {

    private static final Map<UUID, Long> expiresAtMillis = new HashMap<>();

    public static void activate(EntityPlayerMP player, int durationSeconds) {
        UUID id = player.getUniqueID();
        boolean wasActive = expiresAtMillis.containsKey(id);
        expiresAtMillis.put(id, System.currentTimeMillis() + Math.max(1, durationSeconds) * 1000L);
        if (!wasActive) {
            PacketHandler.sendToAll(new MessageSyncPlayerFlip(id, true));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof EntityPlayerMP player)) return; // server-side only

        UUID id = player.getUniqueID();
        Long expiry = expiresAtMillis.get(id);
        if (expiry == null) return;

        if (System.currentTimeMillis() >= expiry) {
            expiresAtMillis.remove(id);
            PacketHandler.sendToAll(new MessageSyncPlayerFlip(id, false));
            return;
        }

        // Steady upward "fall" - overrides whatever vanilla gravity did to motionY this tick.
        player.motionY = 0.2;
        player.fallDistance = 0.0F;
        player.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
    }
}

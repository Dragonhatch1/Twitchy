package com.twitchy.entity;

import java.util.List;
import java.util.Random;

import com.twitchy.Twitchy;

import com.twitchy.network.PacketHandler;
import com.twitchy.network.RerollMobRequestPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/** The single, reusable entry point for spawning mobs near a specific streamer - called from
 *  redemptions, bits, raids, or anywhere else that wants this behavior. Every requested mob is
 *  validated against the server's MobSpawning.json before actually spawning - a client's own pool
 *  selection is a request, never a guarantee. */
public final class MobSpawnManager {

    private static final Random RANDOM = new Random();
    private static final double SPAWN_RADIUS = 2.0D;

    private MobSpawnManager() {}

    public static void spawnRequested(EntityPlayerMP target, List<String> requestedNames, boolean boss) {
        spawnRequested(target, requestedNames, boss, 5);
    }

    private static void spawnRequested(EntityPlayerMP target, List<String> requestedNames, boolean boss, int attemptsRemaining) {
        for (String name : requestedNames) {
            if (!MobSpawningConfig.isAllowed(name)) {
                PacketHandler.sendTo(
                    new RerollMobRequestPacket(boss, attemptsRemaining - 1), target);
                continue;
            }
            spawnOne(target, name);
        }
    }

    private static void spawnOne(EntityPlayerMP target, String entityName) {
        World world = target.worldObj;
        Entity entity = EntityList.createEntityByName(entityName, world);
        if (entity == null) {
            Twitchy.LOG.warn("Could not create entity '{}' - not registered on this server.", entityName);
            return;
        }

        double offsetX = (RANDOM.nextDouble() * 2 - 1) * SPAWN_RADIUS;
        double offsetZ = (RANDOM.nextDouble() * 2 - 1) * SPAWN_RADIUS;
        entity.setPosition(target.posX + offsetX, target.posY, target.posZ + offsetZ);

        world.spawnEntityInWorld(entity);
    }
}

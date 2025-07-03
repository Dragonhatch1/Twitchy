package com.xyrth.twitchy.event.spawn.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import com.xyrth.twitchy.event.GenericEvent;

import twilightforest.entity.TFCreatures;

/**
 * Basic Spawn Template for TF/Regular Mobs on server-side.
 */

public class SpawnGolem extends GenericEvent {

    public SpawnGolem(World world, double x, double y, double z, EntityLivingBase targetEntity) {
        super(world, x, y, z, targetEntity);

        String mobName = "VillagerGolem";

        EntityLiving mob = (EntityLiving) EntityList.createEntityByName(mobName, world);
        mob.setLocationAndAngles(x, y, z, mob.rotationYaw, mob.rotationPitch);

        // spawns mob in the world.
        world.spawnEntityInWorld(mob);
    }
}

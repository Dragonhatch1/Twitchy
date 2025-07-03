package com.xyrth.twitchy.event.spawn.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import com.xyrth.twitchy.event.GenericEvent;

import twilightforest.entity.TFCreatures;

public class SpawnTFFireBeetle extends GenericEvent {

    public SpawnTFFireBeetle(World world, double x, double y, double z, EntityLivingBase targetEntity) {
        super(world, x, y, z, targetEntity);

        int id = 206;

        Entity mob = TFCreatures.createEntityByID(id, world);
        ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(100);
        ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(5.0);
        ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .setBaseValue(10);
        ((EntityLiving) mob).setHealth(100);
        mob.setLocationAndAngles(x, y, z, mob.rotationYaw, mob.rotationPitch);

        world.spawnEntityInWorld(mob);
    }
}

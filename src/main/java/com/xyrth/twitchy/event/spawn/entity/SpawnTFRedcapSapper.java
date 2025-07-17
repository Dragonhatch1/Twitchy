package com.xyrth.twitchy.event.spawn.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import com.xyrth.twitchy.event.GenericSpawnEvent;

import twilightforest.entity.TFCreatures;

public class SpawnTFRedcapSapper extends GenericSpawnEvent {

    public SpawnTFRedcapSapper(World world, double x, double y, double z, EntityLivingBase targetEntity, int hp,
        int att, double spd, String username, int amount) {
        super(world, x, y, z, targetEntity, hp, att, spd, username, amount);

        int l = 0;
        while (l < amount) {
            int id = 210;

            Entity mob = TFCreatures.createEntityByID(id, world);
            mob.setLocationAndAngles(x, y, z, mob.rotationYaw, mob.rotationPitch);

            // Changing Hp by getting base Hp, adding new HP Value to base, then setting HP to newHP value (aka Heal)
            double regHp = ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .getBaseValue();
            ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setBaseValue(regHp + hp);
            float newHp = (float) ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .getBaseValue();
            ((EntityLiving) mob).setHealth(newHp);

            // Changing att by getting base att, and adding new att Value to base
            double regAtt = ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.attackDamage)
                .getBaseValue();
            ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.attackDamage)
                .setBaseValue(regAtt + att);

            // Set Speed
            ((EntityLiving) mob).getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .setBaseValue(spd);

            // set Name
            ((EntityLiving) mob).setCustomNameTag(username);

            // spawns mob in the world.
            world.spawnEntityInWorld(mob);
            l++;
        }
    }
}

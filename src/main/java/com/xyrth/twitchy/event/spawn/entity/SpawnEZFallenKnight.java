package com.xyrth.twitchy.event.spawn.entity;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import com.xyrth.twitchy.event.GenericSpawnEvent;

/**
 * Basic Spawn Template for TF/Regular Mobs on server-side.
 */

public class SpawnEZFallenKnight extends GenericSpawnEvent {

    public SpawnEZFallenKnight(World world, double x, double y, double z, EntityLivingBase targetEntity, int hp,
        int att, double spd, String username, int amount) {
        super(world, x, y, z, targetEntity, hp, att, spd, username, amount);

        int l = 0;
        while (l < amount) {
            String mobName = "enderzoo.FallenKnight";

            EntityLiving mob = (EntityLiving) EntityList.createEntityByName(mobName, world);
            mob.setLocationAndAngles(x, y, z, mob.rotationYaw, mob.rotationPitch);

            // Changing Hp by getting base Hp, adding new HP Value to base, then setting HP to newHP value (aka Heal)
            double regHp = mob.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .getBaseValue();
            mob.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setBaseValue(regHp + hp);
            float newHp = (float) mob.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .getBaseValue();
            mob.setHealth(newHp);

            // Changing att by getting base att, and adding new att Value to base
            double regAtt = mob.getEntityAttribute(SharedMonsterAttributes.attackDamage)
                .getBaseValue();
            mob.getEntityAttribute(SharedMonsterAttributes.attackDamage)
                .setBaseValue(regAtt + att);

            // Set Speed
            mob.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .setBaseValue(spd);

            // set Name
            mob.setCustomNameTag(username);

            // spawns mob in the world.
            world.spawnEntityInWorld(mob);
            l++;
        }
    }
}

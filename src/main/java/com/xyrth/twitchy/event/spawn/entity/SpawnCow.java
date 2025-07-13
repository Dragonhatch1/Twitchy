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

public class SpawnCow extends GenericSpawnEvent {

    public SpawnCow(World world, double x, double y, double z, EntityLivingBase targetEntity, int hp, int att,
        double spd, String username, int amount) {
        super(world, x, y, z, targetEntity, hp, att, spd, username, amount);

        int l = 0;
        while (l < amount) {
            String mobName = "Cow";

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

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

public class SpawnEZFallenKnight extends GenericEvent {

    public SpawnEZFallenKnight(World world, double x, double y, double z, EntityLivingBase targetEntity) {
        super(world, x, y, z, targetEntity);
        // TODO SpawnEZFallenKnight
        // id of the TF mob
        int id = 206;

        // used when spawning vanilla mobs
        String mobName = "Skeleton";

        // can give custom names by using setCustomNameTag and giving a string.
        String customName = "Winkler";

        // TfCreatures is needed to call for Tf Entities. Use the damage number on Eggs to get Entity Ids.
        Entity mob = TFCreatures.createEntityByID(id, world);

        // currently used for spawning vanilla mobs by name. Could swap this up with ids or learn NBT.
        EntityLiving vanillaMob = (EntityLiving) EntityList.createEntityByName(mobName, world);

        // needed to give mob custom names.
        EntityLiving entityliving = (EntityLiving) mob;

        // sets location and Angles.
        mob.setLocationAndAngles(x, y, z, mob.rotationYaw, mob.rotationPitch);

        // Sets the custom name for the entity
        entityliving.setCustomNameTag(customName);

        // spawns mob in the world.
        world.spawnEntityInWorld(mob);
    }
}

package com.xyrth.twitchy.event.spawn;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.xyrth.twitchy.event.GenericSpawnEvent;

import twilightforest.entity.TFCreatures;

/**
 * Basic Spawn Template for TF/Regular Mobs on server-side.
 */

public class BasicSpawnTemplate extends GenericSpawnEvent {

    public BasicSpawnTemplate(World world, double x, double y, double z, EntityLivingBase targetEntity, int hp, int att,
        double spd, String username, int amount) {
        super(world, x, y, z, targetEntity, hp, att, spd, username, amount);

        // id of the TF mob
        int id = 206;

        // used when spawning vanilla mobs
        String mobName = "Skeleton";

        // can give custom names by using setCustomNameTag and giving a string. this currently sets it to the
        // requester's name.
        String customName = username;

        // TfCreatures is needed to call for Tf Entities. Use the damage number on Eggs to get Entity Ids.
        Entity mob = TFCreatures.createEntityByID(id, world);

        // currently used for spawning vanilla mobs by name. Could swap this up with ids or learn NBT.
        EntityLiving vanillaMob = (EntityLiving) EntityList.createEntityByName(mobName, world);

        // needed to give mob custom names if spawning via TF methods.
        EntityLiving entityliving = (EntityLiving) mob;

        // sets location and Angles.
        mob.setLocationAndAngles(x, y, z, mob.rotationYaw, mob.rotationPitch);

        // Sets the custom name for the entity
        entityliving.setCustomNameTag(customName);

        /*
         * Sets up basic mob attributes.
         * Passive entities cannot naturally have attack damage (Chicken, Cow, Sheep).
         */
        vanillaMob.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(spd);

        // adds potion effects to the mob. id, duration (in ticks), Amplification (0 = level 1) (level n = n-1)
        vanillaMob.addPotionEffect(new PotionEffect(20, 9600, 0));

        // While loop to spawn specific amount of monsters in world.
        int l = 0;
        while (l < amount) {
            // spawns mob in the world.
            world.spawnEntityInWorld(mob);
            l++;
        }
    }
}

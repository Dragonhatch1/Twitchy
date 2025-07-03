package com.xyrth.twitchy.event;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EventSpawnTest extends GenericEvent {

    public EventSpawnTest(World world, double x, double y, double z, EntityLivingBase targetEntity) {
        super(world, x, y, z, targetEntity);

        int id = 206;
        String customName = "Winkler";
        String temp = "Skeleton";
        EntityLiving mob = (EntityLiving) EntityList.createEntityByName(temp, world);
        mob.setLocationAndAngles(x, y, z, mob.rotationYaw, mob.rotationPitch);
        world.spawnEntityInWorld(mob);

        // world.addWeatherEffect(new EntityLightningBolt(world, x + 0.5, y + 0.5, z + 0.5));
        // world.createExplosion(targetEntity, x + 0.5, y + 0.5, z + 0.5, 4, false);
    }
}

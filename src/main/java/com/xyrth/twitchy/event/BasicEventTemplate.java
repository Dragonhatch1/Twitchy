package com.xyrth.twitchy.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class BasicEventTemplate extends GenericEvent {

    public BasicEventTemplate(World world, double x, double y, double z, EntityLivingBase targetEntity) {
        super(world, x, y, z, targetEntity);
    }
}

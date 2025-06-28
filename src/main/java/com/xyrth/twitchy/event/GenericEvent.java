package com.xyrth.twitchy.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class GenericEvent extends WorldEvent {

    public double x;
    public double y;
    public double z;
    public EntityLivingBase targetEntity;

    public GenericEvent(World world, double x, double y, double z, EntityLivingBase targetEntity) {
        super(world);
        this.targetEntity = targetEntity;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

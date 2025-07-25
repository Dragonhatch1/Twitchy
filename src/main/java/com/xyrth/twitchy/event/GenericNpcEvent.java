package com.xyrth.twitchy.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

/**
 * <p>
 * This class provides a generic event structure based on WorldEvent,
 * that should be extended for each further event type.
 * <p/>
 * When we want to start a new event for a type, we instatiate it with `new` or
 * <p>
 * .getDeclaredConstructor(
 * World.class,
 * double.class,
 * double.class,
 * double.class,
 * EntityLivingBase.class)
 * .newInstance();
 * </p>
 * <p>
 * The actual event content can be anywhere in the class,
 * it just has to be started in the constructor.
 * </p>
 */
public abstract class GenericNpcEvent extends WorldEvent {

    public double x;
    public double y;
    public double z;
    public EntityLivingBase targetEntity;
    public String username;
    public int size;
    public int modelType;
    public int months;

    public GenericNpcEvent(World world, double x, double y, double z, EntityLivingBase targetEntity, String username,
        int size, int modelType, int months) {
        super(world);
        this.targetEntity = targetEntity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.username = username;
        this.size = size;
        this.modelType = modelType;
        this.months = months;
    }

}

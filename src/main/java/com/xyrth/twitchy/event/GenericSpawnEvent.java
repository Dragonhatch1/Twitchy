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
public abstract class GenericSpawnEvent extends WorldEvent {

    public double x;
    public double y;
    public double z;
    public EntityLivingBase targetEntity;
    public int hp;
    public int att;
    public double spd;
    public String username;

    public GenericSpawnEvent(World world, double x, double y, double z, EntityLivingBase targetEntity, int hp, int att,
        double spd, String username) {
        super(world);
        this.targetEntity = targetEntity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.hp = hp;
        this.att = att;
        this.spd = spd;
        this.username = username;
    }
    // !spawn FireBeetle hp:4 att:11 speed:3 <- user requests spawn in chat
    // /twitchy FireBeetle 4 11 3 requester:username <- sent from GTNHBot through Rcon
}

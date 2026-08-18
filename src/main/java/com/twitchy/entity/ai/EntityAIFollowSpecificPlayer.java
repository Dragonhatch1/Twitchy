package com.twitchy.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;

public class EntityAIFollowSpecificPlayer extends EntityAIBase {

    private final EntityCreature entity;
    private final EntityLivingBase target;
    private final double speed;
    private final float startDist; // was "minDist" - the trigger-to-begin distance (larger)
    private final float stopDist;  // was "maxDist" - the close-enough-to-stop distance (smaller)
    private final PathNavigate navigator;
    private int repathDelay;
    private boolean hadAvoidsWater;

    public EntityAIFollowSpecificPlayer(EntityCreature entity, EntityLivingBase target, double speed, float startDist, float stopDist) {
        this.entity = entity;
        this.target = target;
        this.speed = speed;
        this.startDist = startDist;
        this.stopDist = stopDist;
        this.navigator = entity.getNavigator();
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (target == null || target.isDead) return false;
        return entity.getDistanceSqToEntity(target) >= (double) (startDist * startDist);
    }

    @Override
    public boolean continueExecuting() {
        return !navigator.noPath() && entity.getDistanceSqToEntity(target) > (double) (stopDist * stopDist);
    }

    @Override
    public void startExecuting() {
        repathDelay = 0;
        hadAvoidsWater = navigator.getAvoidsWater();
        navigator.setAvoidsWater(false);
    }

    @Override
    public void resetTask() {
        navigator.clearPathEntity();
        navigator.setAvoidsWater(hadAvoidsWater);
    }

    @Override
    public void updateTask() {
        entity.getLookHelper().setLookPositionWithEntity(target, 10.0F, (float) entity.getVerticalFaceSpeed());
        if (--repathDelay <= 0) {
            repathDelay = 10;
            navigator.tryMoveToEntityLiving(target, speed);
            // Note: deliberately not replicating vanilla's teleport-if-stuck fallback here yet -
            // worth adding if you still see them getting permanently stuck behind obstacles,
            // but leaving it out for now keeps this change focused on the actual stutter-step bug.
        }
    }
}

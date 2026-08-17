package com.twitchy.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;

/** Continuously paths toward a specific player, same core mechanic as vanilla's
 *  EntityAIFollowOwner (confirmed via PathNavigate.tryMoveToEntityLiving), but without requiring
 *  an EntityTameable/owner relationship - just a fixed target resolved once at construction. */
public class EntityAIFollowSpecificPlayer extends EntityAIBase {

    private final EntityCreature entity;
    private final EntityLivingBase target;
    private final double speed;
    private final float minDist;
    private final float maxDist;
    private final PathNavigate navigator;
    private int repathDelay;

    public EntityAIFollowSpecificPlayer(EntityCreature entity, EntityLivingBase target, double speed, float minDist, float maxDist) {
        this.entity = entity;
        this.target = target;
        this.speed = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.navigator = entity.getNavigator();
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (target == null || target.isDead) return false;
        return entity.getDistanceSqToEntity(target) >= (double) (minDist * minDist);
    }

    @Override
    public boolean continueExecuting() {
        return !navigator.noPath() && entity.getDistanceSqToEntity(target) > (double) (maxDist * maxDist);
    }

    @Override
    public void startExecuting() {
        repathDelay = 0;
    }

    @Override
    public void resetTask() {
        navigator.clearPathEntity();
    }

    @Override
    public void updateTask() {
        entity.getLookHelper().setLookPositionWithEntity(target, 10.0F, (float) entity.getVerticalFaceSpeed());
        if (--repathDelay <= 0) {
            repathDelay = 10; // same re-path interval EntityAIFollowOwner uses
            navigator.tryMoveToEntityLiving(target, speed);
        }
    }
}

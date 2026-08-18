package com.twitchy.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Keeps the entity's head oriented toward its owner continuously. Deliberately look-only (mutex
 * bit 2, matching vanilla's own EntityAIWatchClosest convention) and lower priority than
 * EntityAIFollowOwner, so FollowOwner's own look-handling takes precedence while actively
 * moving - this only fills the gap while idle/close enough that FollowOwner isn't executing.
 */
public class AiStareAtOwner extends EntityAIBase {

    private final EntityLiving entity;
    private final EntityLivingBase owner;

    public AiStareAtOwner(EntityLiving entity, EntityLivingBase owner) {
        this.entity = entity;
        this.owner = owner;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        return owner != null && owner.isEntityAlive();
    }

    @Override
    public boolean continueExecuting() {
        return shouldExecute();
    }

    @Override
    public void updateTask() {
        entity.getLookHelper()
            .setLookPositionWithEntity(owner, 10.0F, (float) entity.getVerticalFaceSpeed());
    }
}

package com.twitchy.entity;

import com.twitchy.entity.ai.EntityAIFollowSpecificPlayer;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityViewerFollower extends EntityCreature {

    private String twitchUserId; // set right after construction, used for tracking/removal

    public EntityViewerFollower(World world) {
        super(world);
        this.setSize(0.6F, 1.8F); // roughly player-sized bounding box
    }

    /** Call once, right after construction, before spawning into the world. */
    public void initFollow(EntityLivingBase target, String twitchUserId, String viewerName) {
        this.twitchUserId = twitchUserId;
        this.setCustomNameTag(viewerName);
        this.setAlwaysRenderNameTag(true);
        this.tasks.addTask(1, new EntityAIFollowSpecificPlayer(this, target, 1.0D, 3.0F, 24.0F));
    }

    public String getTwitchUserId() {
        return twitchUserId;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}

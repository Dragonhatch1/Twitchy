package com.twitchy.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.World;

import com.twitchy.entity.ai.AiStareAtOwner;

/**
 * A player-shaped "viewer follower" entity - extends EntityWolf entirely for its proven,
 * battle-tested EntityAIFollowOwner behavior, but strips out every other task Wolf's own
 * constructor adds (swimming, leaping, attacking, mating, wandering, begging, watching, and all
 * of Wolf's targetTasks) so it does nothing but follow its owner. Rendering is swapped separately
 * via RenderViewerFollower (ModelBiped + Steve texture) - nothing here affects appearance at all.
 */
public class EntityViewerFollower extends EntityWolf {

    private String twitchUserId;

    public EntityViewerFollower(World world) {
        super(world);
        this.setSize(0.6F, 1.8F); // Wolf's own constructor sets wolf-sized proportions - restore player-like size

        this.tasks.removeTask(this.aiSit);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIMate);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIWander);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIBeg);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAILookIdle);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIWatchClosest);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIFollowOwner);
        this.tasks.addTask(5, new net.minecraft.entity.ai.EntityAIFollowOwner(this, 1.0D, 7.0F, 4.0F));
    }

    /** Call once, right after construction, before spawning into the world. */
    public void initFollow(EntityLivingBase target, String twitchUserId, String viewerName) {
        this.twitchUserId = twitchUserId;
        this.setTamed(true);
        this.func_152115_b(
            target.getUniqueID()
                .toString());
        this.setCustomNameTag(viewerName);
        this.setAlwaysRenderNameTag(true);
        this.tasks.addTask(6, new AiStareAtOwner(this, target));
    }

    public String getTwitchUserId() {
        return twitchUserId;
    }

    public EntityLivingBase getTargetPlayer() {
        return getOwner();
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected String getLivingSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return "damage.hit";
    }

    @Override
    protected String getDeathSound() {
        return "damage.hit";
    }

    @Override
    public boolean isWet() {
        return false;
    }
}

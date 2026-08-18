package com.twitchy.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.World;

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

        this.tasks.removeTask(this.aiSit); // EntityAISit - stored as a real field on EntityTameable, not an anonymous instance
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIMate);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIWander);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIBeg);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAILookIdle);
    }

    /** Call once, right after construction, before spawning into the world. */
    public void initFollow(EntityLivingBase target, String twitchUserId, String viewerName) {
        this.twitchUserId = twitchUserId;
        this.setTamed(true);
        this.func_152115_b(target.getUniqueID().toString());
        this.setCustomNameTag(viewerName);
        this.setAlwaysRenderNameTag(true);
    }

    public String getTwitchUserId() {
        return twitchUserId;
    }

    public EntityLivingBase getTargetPlayer() {
        return getOwner(); // resolved from the owner UUID set in initFollow
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected String getLivingSound() {
        return null; // no idle sound at all - remove this override entirely if you'd rather use a real sound
    }

    @Override
    protected String getHurtSound() {
        return "damage.hit"; // vanilla's generic hurt sound, in place of "mob.wolf.hurt"
    }

    @Override
    protected String getDeathSound() {
        return "damage.hit"; // no dedicated generic death sound exists the same way - reusing hurt as a placeholder
    }

    @Override
    public boolean isWet() {
        return false; // suppresses the entire wolf shake mechanism (sound + visual trigger) at its single source
    }
}

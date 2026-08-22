package com.twitchy.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
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
    private static final int WATCHER_MINECRAFT_USERNAME = 25;
    private static final int WATCHER_SCALE = 26;
    private static final int WATCHER_MODEL_TYPE = 27;
    private static java.lang.reflect.Method methodSetSize;

    private static final float BASE_WIDTH = 0.6F;
    private static final float BASE_HEIGHT = 1.8F;

    private static final double BASE_MAX_HEALTH = 10.0D;

    public EntityViewerFollower(World world) {
        super(world);
        this.setSize(BASE_WIDTH, BASE_HEIGHT); // Wolf's own constructor sets wolf-sized proportions - restore
                                               // player-like size
        this.dataWatcher.addObject(WATCHER_MINECRAFT_USERNAME, "");
        this.dataWatcher.addObject(WATCHER_SCALE, 1.0F);

        this.tasks.removeTask(this.aiSit);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIMate);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIWander);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIBeg);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAILookIdle);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIWatchClosest);
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof net.minecraft.entity.ai.EntityAIFollowOwner);
        this.tasks.addTask(5, new net.minecraft.entity.ai.EntityAIFollowOwner(this, 1.0D, 7.0F, 4.0F));
        this.dataWatcher.addObject(WATCHER_MODEL_TYPE, "BIPED");
    }

    /** Call once, right after construction, before spawning into the world. */
    public void initFollow(EntityLivingBase target, String twitchUserId, String viewerName, String minecraftUsername,
        float scale) {
        this.twitchUserId = twitchUserId;
        this.setTamed(true);
        this.func_152115_b(
            target.getUniqueID()
                .toString());
        this.setCustomNameTag(viewerName);
        this.setAlwaysRenderNameTag(true);
        this.tasks.addTask(6, new AiStareAtOwner(this, target));
        this.dataWatcher.updateObject(WATCHER_MINECRAFT_USERNAME, minecraftUsername == null ? "" : minecraftUsername);
        this.setFollowerScale(scale);
    }

    public String getTwitchUserId() {
        return twitchUserId;
    }

    public EntityLivingBase getTargetPlayer() {
        return getOwner();
    }

    public String getMinecraftUsername() {
        return this.dataWatcher.getWatchableObjectString(WATCHER_MINECRAFT_USERNAME);
    }

    public float getFollowerScale() {
        return this.dataWatcher.getWatchableObjectFloat(WATCHER_SCALE);
    }

    public void setFollowerScale(float scale) {
        this.dataWatcher.updateObject(WATCHER_SCALE, scale);
    }

    private void applyReflectiveResize(float scale) {
        try {
            if (methodSetSize == null) {
                methodSetSize = cpw.mods.fml.relauncher.ReflectionHelper.findMethod(
                    net.minecraft.entity.Entity.class,
                    this,
                    new String[] { "setSize", "func_70105_a", "a" },
                    new Class[] { Float.TYPE, Float.TYPE });
            }
            methodSetSize.invoke(this, BASE_WIDTH * scale, BASE_HEIGHT * scale);
        } catch (Exception e) {
            com.twitchy.Twitchy.LOG.warn("Failed to resize follower hitbox via reflection", e);
        }
    }

    public void healWithEffect(float amount) {
        this.heal(amount);

        if (this.worldObj instanceof net.minecraft.world.WorldServer server) {
            server.func_147487_a(
                "heart",
                this.posX, this.posY + this.height, this.posZ,
                7,                          // particle count, matching vanilla's own loop count
                this.width * 0.5D, this.height * 0.5D, this.width * 0.5D,  // spread
                0.0D);                      // speed
        }
    }

    public String getFollowerModelKey() {
        String key = this.dataWatcher.getWatchableObjectString(WATCHER_MODEL_TYPE);
        return key == null || key.isBlank() ? "BIPED" : key;
    }

    public void setFollowerModelKey(String key) {
        this.dataWatcher.updateObject(WATCHER_MODEL_TYPE, key == null ? "BIPED" : key);
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
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        // Followers never drop anything of their own - no species drop, no gear.
    }

    @Override
    protected void dropEquipment(boolean recentlyHit, int lootingLevel) {
        // Explicitly suppress the vanilla chance-based equipped-item drop too.
    }

    @Override
    public boolean isWet() {
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BASE_MAX_HEALTH);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        float syncedScale = getFollowerScale();
        float expectedHeight = BASE_HEIGHT * syncedScale;
        if (Math.abs(this.height - expectedHeight) > 0.001F) {
            applyReflectiveResize(syncedScale);
        }
    }

}

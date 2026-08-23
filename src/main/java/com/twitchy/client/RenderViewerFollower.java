package com.twitchy.client;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.twitchy.Twitchy;
import com.twitchy.entity.EntityViewerFollower;

public class RenderViewerFollower extends RenderBiped {

    private final ModelBiped bipedModel = new ModelBiped();
    private final ModelSpider spiderModel = new ModelSpider();
    private static final ResourceLocation SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/spider.png");
    private static final ResourceLocation STEVE_TEXTURE = new ResourceLocation("textures/entity/steve.png");

    private static final Map<String, ResourceLocation> resolvedSkins = new ConcurrentHashMap<>();
    private static final Set<String> pendingLookups = ConcurrentHashMap.newKeySet();
    private final Map<String, ModelBase> modelCache = new HashMap<>();

    // Minecraft's own client doesn't expose a profile-repository getter the way it does for
    // SkinManager (func_152342_ad()), so Twitchy builds its own, matching the exact same
    // constructor pattern Minecraft.java itself uses to build its session service.
    private static final GameProfileRepository PROFILE_REPO = new YggdrasilAuthenticationService(
        Proxy.NO_PROXY,
        UUID.randomUUID()
            .toString()).createProfileRepository();

    public RenderViewerFollower() {
        super(new ModelBiped(), 0.5F);
    }

    protected ResourceLocation getEntityTexture(EntityViewerFollower entity) {
        String key = entity.getFollowerModelKey();
        FollowerModelRegistry.Entry regEntry = FollowerModelRegistry.get(key);

        if (regEntry != null && !"BIPED".equals(key)) {
            return regEntry.texture; // non-biped entries always use their own fixed texture
        }

        // BIPED specifically still supports real skin resolution
        String username = entity.getMinecraftUsername();
        if (username == null || username.isBlank()) return STEVE_TEXTURE;
        ResourceLocation cached = resolvedSkins.get(username);
        if (cached != null) return cached;
        if (pendingLookups.add(username)) resolveSkinAsync(username);
        return STEVE_TEXTURE;
    }

    @Override
    protected ResourceLocation getEntityTexture(net.minecraft.entity.Entity entity) {
        return this.getEntityTexture((EntityViewerFollower) entity);
    }

    private static void resolveSkinAsync(String username) {
        PROFILE_REPO.findProfilesByNames(new String[] { username }, Agent.MINECRAFT, new ProfileLookupCallback() {

            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                SkinManager skinManager = Minecraft.getMinecraft()
                    .func_152342_ad();
                GameProfile filled = Minecraft.getMinecraft()
                    .func_152347_ac()
                    .fillProfileProperties(profile, false);

                skinManager.func_152790_a(filled, new SkinManager.SkinAvailableCallback() {

                    @Override
                    public void func_152121_a(MinecraftProfileTexture.Type type, ResourceLocation location) {
                        if (type == MinecraftProfileTexture.Type.SKIN) {
                            resolvedSkins.put(username, location);
                        }
                        pendingLookups.remove(username);
                    }
                }, false);
            }

            @Override
            public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                Twitchy.LOG.warn("Skin lookup failed for username '{}': {}", username, exception.getMessage());
                pendingLookups.remove(username);
            }
        });
    }

    @Override
    protected void preRenderCallback(net.minecraft.entity.EntityLivingBase entity, float partialTicks) {
        if (entity instanceof EntityViewerFollower follower) {
            float scale = follower.getFollowerScale();
            org.lwjgl.opengl.GL11.glScalef(scale, scale, scale);
        }
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity instanceof EntityViewerFollower follower) {
            String key = follower.getFollowerModelKey();
            FollowerModelRegistry.Entry regEntry = FollowerModelRegistry.get(key);
            if (regEntry != null) {
                this.mainModel = modelCache.computeIfAbsent(key, k -> regEntry.modelSupplier.get());
            }
        }
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected int shouldRenderPass(EntityLiving entity, int armorSlot, float partialTicks) {
        if (entity instanceof EntityViewerFollower && !(this.mainModel instanceof ModelBiped)) {
            return -1; // suppress armor unless the active model shares biped proportions
        }
        return super.shouldRenderPass(entity, armorSlot, partialTicks);
    }

    @Override
    protected void renderEquippedItems(EntityLiving entity, float partialTicks) {
        if (entity instanceof EntityViewerFollower && !(this.mainModel instanceof ModelBiped)) {
            return; // same gate for the held item
        }
        super.renderEquippedItems(entity, partialTicks);
    }
}

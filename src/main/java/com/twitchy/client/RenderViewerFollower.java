package com.twitchy.client;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.util.ResourceLocation;

import com.twitchy.entity.EntityViewerFollower;

public class RenderViewerFollower extends RenderBiped {

    private static final ResourceLocation STEVE_TEXTURE = new ResourceLocation("textures/entity/steve.png");

    private static final Map<String, ResourceLocation> resolvedSkins = new ConcurrentHashMap<>();
    private static final Set<String> pendingLookups = ConcurrentHashMap.newKeySet();

    public RenderViewerFollower() {
        super(new ModelBiped(), 0.5F);
    }

    protected ResourceLocation getEntityTexture(EntityViewerFollower entity) {
        String username = entity.getMinecraftUsername();
        if (username == null || username.isBlank()) return STEVE_TEXTURE;

        ResourceLocation cached = resolvedSkins.get(username);
        if (cached != null) return cached;

        if (pendingLookups.add(username)) {
            resolveSkinAsync(username);
        }
        return STEVE_TEXTURE;
    }

    @Override
    protected ResourceLocation getEntityTexture(net.minecraft.entity.Entity entity) {
        return this.getEntityTexture((EntityViewerFollower) entity);
    }

    /** Called once per poll cycle (via ViewerFollowerClientPoller) so stored usernames get
     *  re-checked for skin changes on the same cadence as everything else, rather than resolving
     *  once and caching forever. */
    public static void invalidateSkinCache() {
        resolvedSkins.clear();
        pendingLookups.clear();
    }

    // TODO: real username -> GameProfile -> texture resolution via authlib. Stubbed for now -
    // logs the attempt and leaves the entity on the Steve fallback until this is implemented.
    private static void resolveSkinAsync(String username) {
        com.twitchy.Twitchy.LOG.info("[TODO] Would resolve real skin for username: {}", username);
        pendingLookups.remove(username);
    }
}

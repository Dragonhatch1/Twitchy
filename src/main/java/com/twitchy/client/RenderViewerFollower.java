package com.twitchy.client;

import com.twitchy.entity.EntityViewerFollower;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.util.ResourceLocation;

public class RenderViewerFollower extends RenderBiped {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/steve.png");

    public RenderViewerFollower() {
        super(new ModelBiped(), 0.5F);
    }

    protected ResourceLocation getEntityTexture(EntityViewerFollower entity) {
        return TEXTURE;
    }

    @Override
    protected ResourceLocation getEntityTexture(net.minecraft.entity.Entity entity) {
        return this.getEntityTexture((EntityViewerFollower) entity);
    }
}

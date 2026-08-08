package com.twitchy.client;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Renders a gravity-flipped player upside-down for everyone watching, not just themselves. */
public class PlayerFlipRenderer {

    @SubscribeEvent
    public void onRenderPre(RenderPlayerEvent.Pre event) {
        if (!PlayerFlipRenderState.isFlipped(event.entityPlayer.getUniqueID())) return;

        float partialTicks = event.partialRenderTick;
        float x = (float) (event.entityPlayer.lastTickPosX
            + (event.entityPlayer.posX - event.entityPlayer.lastTickPosX) * partialTicks - RenderManager.renderPosX);
        float y = (float) (event.entityPlayer.lastTickPosY
            + (event.entityPlayer.posY - event.entityPlayer.lastTickPosY) * partialTicks - RenderManager.renderPosY);
        float z = (float) (event.entityPlayer.lastTickPosZ
            + (event.entityPlayer.posZ - event.entityPlayer.lastTickPosZ) * partialTicks - RenderManager.renderPosZ);
        float halfHeight = event.entityPlayer.height / 2.0F;

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);                 // go to the real interpolated world position first
        GL11.glTranslatef(0.0F, halfHeight, 0.0F);   // up to the model's own center
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);    // flip
        GL11.glTranslatef(0.0F, halfHeight, 0.0F);  // back down to the anchor, inside the flipped frame
        GL11.glTranslatef(-x, -y, -z);               // undo our own translate, so the real translate that runs next composes correctly
    }

    @SubscribeEvent
    public void onRenderPost(RenderPlayerEvent.Post event) {
        if (!PlayerFlipRenderState.isFlipped(event.entityPlayer.getUniqueID())) return;
        GL11.glPopMatrix();
    }
}

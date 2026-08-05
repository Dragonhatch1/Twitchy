package com.twitchy.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

/** Renders a gravity-flipped player upside-down for everyone watching, not just themselves. */
public class PlayerFlipRenderer {

    @SubscribeEvent
    public void onRenderPre(RenderPlayerEvent.Pre event) {
        if (!PlayerFlipRenderState.isFlipped(event.entityPlayer.getUniqueID())) return;
        float halfHeight = event.entityPlayer.height / 2.0F;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, halfHeight, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, -halfHeight, 0.0F);
    }

    @SubscribeEvent
    public void onRenderPost(RenderPlayerEvent.Post event) {
        if (!PlayerFlipRenderState.isFlipped(event.entityPlayer.getUniqueID())) return;
        GL11.glPopMatrix();
    }
}

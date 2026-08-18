package com.twitchy.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.List;


public class ViewerFollowerHandler {

    private boolean sweepDone = false;
    private static final long RESPAWN_DELAY_MS = 60 * 1000L;

    private static final List<PendingRespawn> pending = new ArrayList<>();

    private static class PendingRespawn {

        final String userId;
        final String userLogin;
        final EntityPlayerMP target;
        final long respawnAtMillis;

        PendingRespawn(String userId, String userLogin, EntityPlayerMP target, long respawnAtMillis) {
            this.userId = userId;
            this.userLogin = userLogin;
            this.target = target;
            this.respawnAtMillis = respawnAtMillis;
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            ViewerFollowerManager.despawnForPlayer(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (sweepDone) return;
        sweepDone = true;

        if (!(event.player instanceof EntityPlayerMP player)) return;

        for (Object obj : player.worldObj.loadedEntityList) {
            if (obj instanceof EntityViewerFollower follower) {
                follower.setDead();
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityViewerFollower follower)) return;
        if (follower.worldObj.isRemote) return;

        String userId = follower.getTwitchUserId();
        ViewerFollowerManager.removeFromTracking(userId);
    }
}

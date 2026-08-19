package com.twitchy.entity;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.twitchy.network.KillCreditPacket;
import com.twitchy.network.PacketHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ViewerFollowerHandler {

    private boolean sweepDone = false;

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
        // Credit a kill if a viewer follower landed the final blow - tell the owning streamer's
        // client directly, since kill data (and now the boss-pool check itself) lives entirely in
        // THEIR local files, not the server's.
        if (event.source != null && event.source.getEntity() instanceof EntityViewerFollower killer) {
            if (killer.getTargetPlayer() instanceof EntityPlayerMP owner) {
                String killedEntityName = EntityList.getEntityString(event.entityLiving);
                PacketHandler.sendTo(new KillCreditPacket(killer.getTwitchUserId(), killedEntityName), owner);
            }
        }

        // Existing logic: a viewer follower itself dying, for respawn tracking.
        if (!(event.entityLiving instanceof EntityViewerFollower follower)) return;
        if (follower.worldObj.isRemote) return;

        String userId = follower.getTwitchUserId();
        ViewerFollowerManager.removeFromTracking(userId);
    }
}

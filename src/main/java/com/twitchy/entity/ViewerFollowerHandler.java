package com.twitchy.entity;

import com.twitchy.Twitchy;
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
        if (event.source != null && event.source.getEntity() instanceof EntityViewerFollower killer) {
            Twitchy.LOG.info("[DEBUG] Kill detected. killer follower userId={}, followerName={}, killedEntity={}",
                killer.getTwitchUserId(), killer.getCommandSenderName(), EntityList.getEntityString(event.entityLiving));

            if (killer.getTargetPlayer() instanceof EntityPlayerMP owner) {
                String killedEntityName = EntityList.getEntityString(event.entityLiving);
                Twitchy.LOG.info("[DEBUG] Crediting kill to owner={} (userId={})", owner.getCommandSenderName(), killer.getTwitchUserId());
                PacketHandler.sendTo(new KillCreditPacket(killer.getTwitchUserId(), killedEntityName), owner);
            } else {
                Twitchy.LOG.warn("[DEBUG] killer.getTargetPlayer() did not resolve to a valid EntityPlayerMP!");
            }
        }

        if (!(event.entityLiving instanceof EntityViewerFollower follower)) return;
        if (follower.worldObj.isRemote) return;

        String userId = follower.getTwitchUserId();
        ViewerFollowerManager.removeFromTracking(userId);
    }
}

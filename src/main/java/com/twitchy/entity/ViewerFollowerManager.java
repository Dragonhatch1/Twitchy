package com.twitchy.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;

public class ViewerFollowerManager {

    private static final Map<String, EntityViewerFollower> activeFollowers = new HashMap<>();

    public static void reconcile(EntityPlayerMP target, List<String> userIds, List<String> userLogins) {
        Set<String> currentIds = new HashSet<>(userIds);

        for (int i = 0; i < userIds.size(); i++) {
            String id = userIds.get(i);

            if (activeFollowers.containsKey(id)) continue;

            spawnFollower(target, id, userLogins.get(i));
        }

        activeFollowers.entrySet().removeIf(entry -> {
            if (currentIds.contains(entry.getKey())) return false;
            entry.getValue().setDead();
            return true;
        });
    }

    public static void despawnAll() {
        for (EntityViewerFollower follower : activeFollowers.values()) {
            follower.setDead();
        }
        activeFollowers.clear();
    }

    public static void despawnForPlayer(EntityPlayerMP player) {
        activeFollowers.entrySet().removeIf(entry -> {
            if (entry.getValue().getTargetPlayer() != player) return false;
            entry.getValue().setDead();
            return true;
        });
    }

    public static void spawnFollower(EntityPlayerMP target, String userId, String userLogin) {
        EntityViewerFollower follower = new EntityViewerFollower(target.worldObj);
        follower.setPosition(target.posX, target.posY, target.posZ);
        follower.initFollow(target, userId, userLogin);
        target.worldObj.spawnEntityInWorld(follower);
        activeFollowers.put(userId, follower);
    }

    public static void removeFromTracking(String userId) {
        activeFollowers.remove(userId);
    }
}

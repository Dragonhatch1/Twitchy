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

            EntityViewerFollower follower = new EntityViewerFollower(target.worldObj);
            follower.setPosition(target.posX, target.posY, target.posZ);
            follower.initFollow(target, id, userLogins.get(i));
            target.worldObj.spawnEntityInWorld(follower);
            activeFollowers.put(id, follower);
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
}

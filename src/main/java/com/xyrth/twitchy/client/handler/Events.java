package com.xyrth.twitchy.client.handler;

import net.minecraft.client.entity.EntityClientPlayerMP;

public class Events {

    /**
     * Mob Spawning
     */
    public static void skeleton(EntityClientPlayerMP player) {

        player.sendChatMessage("/summon Skeleton");
    }

    /**
     * Potion Section
     */
    public static void instantdamage(EntityClientPlayerMP player) {

        player.sendChatMessage(
            "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:7,Amplifier:1000,Duration:200}]}},Riding:{id:XPOrb}}");
    }

    public static void wither(EntityClientPlayerMP player) {

        player.sendChatMessage(
            "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:20,Amplifier:10,Duration:200}]}},Riding:{id:XPOrb}}");
    }

    public static void paralysis(EntityClientPlayerMP player) {

        player.sendChatMessage(
            "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:25,Amplifier:0,Duration:200}]}},Riding:{id:XPOrb}}");
    }

    public static void possession(EntityClientPlayerMP player) {

        player.sendChatMessage(
            "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:26,Amplifier:0,Duration:100}]}},Riding:{id:XPOrb}}");
    }

    public static void speed(EntityClientPlayerMP player) {

        player.sendChatMessage(
            "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:1,Amplifier:100,Duration:1200}]}},Riding:{id:XPOrb}}");
    }

    public static void resized(EntityClientPlayerMP player) {

        player.sendChatMessage(
            "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:77,Amplifier:100,Duration:3600}]}},Riding:{id:XPOrb}}");
    }

    public static void firefuse(EntityClientPlayerMP player) {

        player.sendChatMessage(
            "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:109,Amplifier:2,Duration:25}]}},Riding:{id:XPOrb}}");
    }

}

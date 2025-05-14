package com.xyrth.twitchy.client.handler;

import java.util.Random;

import net.minecraft.client.entity.EntityClientPlayerMP;

public class Events {

    /**
     * Mob Spawning
     */
    public static void skeleton(EntityClientPlayerMP player) {

        player.sendChatMessage("/summon Skeleton");
    }

    public static void randomspawn(EntityClientPlayerMP player) {
        Random r = new Random();
        int mob = r.nextInt(5);
        switch (mob) {
            case 0:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/summon Skeleton");
                }
                break;
            case 1:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/summon Zombie");
                }
                break;
            case 2:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/summon Chicken");
                }
                break;
            case 3:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/summon Cow");
                }
                break;
            case 4:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/summon Slime");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Potion Section
     */
    // TODO add more potions and fix percentages
    public static void randompotion(EntityClientPlayerMP player) {
        Random r = new Random();
        int x = r.nextInt(100);
        int mob;
        if (x <= 2) {
            mob = 1;
        } else if (x > 2 && x <= 4) {
            mob = 2;
        } else if (x > 4 && x <= 20) {
            mob = 3;
        } else if (x > 20 && x <= 22) {
            mob = 4;
        } else {
            mob = 0;
        }
        switch (mob) {
            case 0:
                // wither
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:20,Amplifier:10,Duration:200}]}},Riding:{id:XPOrb}}");
                break;
            case 1:
                // paralysis
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:25,Amplifier:0,Duration:200}]}},Riding:{id:XPOrb}}");
                break;
            case 2:
                // possession
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:26,Amplifier:0,Duration:100}]}},Riding:{id:XPOrb}}");
                break;
            case 3:
                // speed
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:1,Amplifier:100,Duration:1200}]}},Riding:{id:XPOrb}}");
                break;
            case 4:
                // Fire fuse
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:109,Amplifier:2,Duration:25}]}},Riding:{id:XPOrb}}");
                break;
            default:
                break;
        }
    }

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

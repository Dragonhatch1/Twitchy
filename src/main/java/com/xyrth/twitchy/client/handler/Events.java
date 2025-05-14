package com.xyrth.twitchy.client.handler;

import java.util.Random;

import net.minecraft.client.entity.EntityClientPlayerMP;

public class Events {

    /**
     * Mob Spawning
     */
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
        // Rolls 1-100
        int x = (r.nextInt(100) + 1);
        int mob;
        //Percentages
        double wither = 0.15;
        double paralysis = 0.15;

        if (x <= 15) {
            mob = 0;
        } else if (x > 15 && x <= 31) {
            mob = 1;
        } else if (x > 31 && x <= 47) {
            mob = 2;
        } else if (x > 47 && x <= 63) {
            mob = 3;
        } else if (x > 63 && x <= 79) {
            mob = 4;
        } else if (x > 79 && x <= 95) {
            mob = 5;
        } else if (x > 79 && x <= 95) {
            mob = 6;
        } else if (x > 79 && x <= 95) {
            mob = 7;
        } else if (x > 79 && x <= 95) {
            mob = 8;
        } else if (x > 79 && x <= 95) {
            mob = 9;
        } else {
            mob = 10;
        }
        switch (mob) {
            case 0:
                // wither 16%
                // player.sendChatMessage("wither 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:20,Amplifier:10,Duration:200}]}},Riding:{id:XPOrb}}");
                break;
            case 1:
                // paralysis 16%
                // player.sendChatMessage("paralysis 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:25,Amplifier:0,Duration:200}]}},Riding:{id:XPOrb}}");
                break;
            case 2:
                // possession 16%
                // player.sendChatMessage("possession 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:26,Amplifier:0,Duration:100}]}},Riding:{id:XPOrb}}");
                break;
            case 3:
                // speed 16%
                // player.sendChatMessage("speed 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:1,Amplifier:100,Duration:1200}]}},Riding:{id:XPOrb}}");
                break;
            case 4:
                // Fire fuse 16%
                // player.sendChatMessage("Fire Fuse 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:109,Amplifier:2,Duration:25}]}},Riding:{id:XPOrb}}");
                break;
            case 5:
                // Resized 16%
                // player.sendChatMessage("Resized 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:77,Amplifier:100,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 6:
                // Waking Nightmare 16%
                // player.sendChatMessage("Waking Nightmare 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:92,Amplifier:100,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 7:
                // Boost 16%
                // player.sendChatMessage("Boost 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:101,Amplifier:100,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 8:
                // Heavy Heart 16%
                // player.sendChatMessage("Heavy Heart 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:108,Amplifier:100,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 9:
                // Hallucinations 16%
                // player.sendChatMessage("Hallucinations 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:64,Amplifier:100,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 10:
                // Instant Damage 4% Chance
                player.sendChatMessage("Oop, here comes the boom! :)");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:7,Amplifier:1000,Duration:200}]}},Riding:{id:XPOrb}}");
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

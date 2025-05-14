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
    // TODO delete old potion keybinds
    public static void randompotion(EntityClientPlayerMP player) {
        Random r = new Random();
        // Rolls 1-100
        int x = (r.nextInt(100) + 1);
        int potion;
        // Percentages || Needs to equal 1.00 or 100%
        double wither = 0.09;
        double paralysis = 0.10;
        double possession = 0.09;
        double speed = 0.10;
        double firefuse = 0.09;
        double resized = 0.11;
        double wakingnightmare = 0.10;
        double boost = 0.10;
        double heavyheart = 0.09;
        double hallucinations = 0.10;
        double instantdamage = 0.03;

        double withern = wither * 100;
        double paralysisn = ((paralysis * 100) + withern);
        double possessionn = ((possession * 100) + paralysisn);
        double speedn = ((speed * 100) + possessionn);
        double firefusen = ((firefuse * 100) + speedn);
        double resizedn = ((resized * 100) + firefusen);
        double wakingnightmaren = ((wakingnightmare * 100) + resizedn);
        double boostn = ((boost * 100) + wakingnightmaren);
        double heavyheartn = ((heavyheart * 100) + boostn);
        double hallucinationsn = ((hallucinations * 100) + heavyheartn);

        if (x <= withern) {
            potion = 0;
        } else if (x > withern && x <= paralysisn) {
            potion = 1;
        } else if (x > paralysisn && x <= possessionn) {
            potion = 2;
        } else if (x > possessionn && x <= speedn) {
            potion = 3;
        } else if (x > speedn && x <= firefusen) {
            potion = 4;
        } else if (x > firefusen && x <= resizedn) {
            potion = 5;
        } else if (x > resizedn && x <= wakingnightmaren) {
            potion = 6;
        } else if (x > wakingnightmaren && x <= boostn) {
            potion = 7;
        } else if (x > boostn && x <= heavyheartn) {
            potion = 8;
        } else if (x > heavyheartn && x <= hallucinationsn) {
            potion = 9;
        } else {
            potion = 10;
        }
        switch (potion) {
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
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:92,Amplifier:10,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 7:
                // Boost 16%
                // player.sendChatMessage("Boost 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:101,Amplifier:10,Duration:1200}]}},Riding:{id:XPOrb}}");
                break;
            case 8:
                // Heavy Heart 16%
                // player.sendChatMessage("Heavy Heart 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:108,Amplifier:10,Duration:200}]}},Riding:{id:XPOrb}}");
                break;
            case 9:
                // Hallucinations 16%
                // player.sendChatMessage("Hallucinations 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:64,Amplifier:10,Duration:3600}]}},Riding:{id:XPOrb}}");
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

    /**
     * Mob Raid Section
     */
    // TODO setup Raid keybind and method. Will spawn many mobs when raided. Can just copy original mob spawner and up the loops.

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

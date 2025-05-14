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
    public static void randompotion(EntityClientPlayerMP player) {
        Random r = new Random();
        // Rolls 1-100
        int x = (r.nextInt(100) + 1);
        int potion;
        // Percentages or Weights || Needs to equal 1.00 or 100%
        //Instant Damage goes no where, its there to remind you to add up to 1.00 or 100%. its Percentage is whatever is leftover from the others to reach 100%.
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
                // wither 9%
                // player.sendChatMessage("wither 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:20,Amplifier:10,Duration:200}]}},Riding:{id:XPOrb}}");
                break;
            case 1:
                // paralysis 10%
                // player.sendChatMessage("paralysis 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:25,Amplifier:0,Duration:200}]}},Riding:{id:XPOrb}}");
                break;
            case 2:
                // possession 9%
                // player.sendChatMessage("possession 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:26,Amplifier:0,Duration:100}]}},Riding:{id:XPOrb}}");
                break;
            case 3:
                // speed 10%
                // player.sendChatMessage("speed 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:1,Amplifier:100,Duration:1200}]}},Riding:{id:XPOrb}}");
                break;
            case 4:
                // Fire fuse 9%
                // player.sendChatMessage("Fire Fuse 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:109,Amplifier:2,Duration:25}]}},Riding:{id:XPOrb}}");
                break;
            case 5:
                // Resized 11%
                // player.sendChatMessage("Resized 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:77,Amplifier:100,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 6:
                // Waking Nightmare 10%
                // player.sendChatMessage("Waking Nightmare 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:92,Amplifier:10,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 7:
                // Boost 10%
                // player.sendChatMessage("Boost 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:101,Amplifier:10,Duration:1200}]}},Riding:{id:XPOrb}}");
                break;
            case 8:
                // Heavy Heart 9%
                // player.sendChatMessage("Heavy Heart 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:108,Amplifier:10,Duration:200}]}},Riding:{id:XPOrb}}");
                break;
            case 9:
                // Hallucinations 10%
                // player.sendChatMessage("Hallucinations 16%");
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:64,Amplifier:10,Duration:3600}]}},Riding:{id:XPOrb}}");
                break;
            case 10:
                // Instant Damage 3%
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
    public static void mobraid(EntityClientPlayerMP player) {
        Random r = new Random();
        int mob = r.nextInt(5);
        switch (mob) {
            case 0:
                player.sendChatMessage("A Horde of Skeletons has chased some raiders from the previous channel!");
                for (int x = 0; x < 60; x++) {
                    player.sendChatMessage("/summon Skeleton");
                }
                break;
            case 1:
                player.sendChatMessage("The arrival of Raiders seems to have stirred up a Zombie Horde!");
                for (int x = 0; x < 150; x++) {
                    player.sendChatMessage("/summon Zombie");
                }
                break;
            case 2:
                player.sendChatMessage("WHO DID DAMAGE TO THE CHICKEN?!");
                for (int x = 0; x < 300; x++) {
                    player.sendChatMessage("/summon Chicken");
                }
                break;
            case 3:
                player.sendChatMessage("The Raiders have brought a stampede of Cows with them!");
                for (int x = 0; x < 300; x++) {
                    player.sendChatMessage("/summon Cow");
                }
                break;
            case 4:
                player.sendChatMessage("The Raiders brought some Slimes with em!");
                for (int x = 0; x < 40; x++) {
                    player.sendChatMessage("/summon Slime");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Sub Section
     */
    // TODO Add Subscriber section and have events on Sub.

    /**
     * Follow Section
     */
    // TODO Add Follower section and have events on Follow.
}

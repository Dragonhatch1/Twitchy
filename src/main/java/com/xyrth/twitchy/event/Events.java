package com.xyrth.twitchy.event;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;

public class Events {

    /**
     * Mob Spawning
     */
    // TODO update mobs to have more health and do more damage.
    public static void randomspawn(EntityClientPlayerMP player) {
        Random r = new Random();
        int mob = r.nextInt(11);
        switch (mob) {
            case 0:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Skeleton");
                }
                break;
            case 1:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Zombie");
                }
                break;
            case 2:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Chicken");
                }
                break;
            case 3:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Cow");
                }
                break;
            case 4:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Slime");
                }
                break;
            case 5:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy DireWolf");
                }
                break;
            case 6:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy FallenKnight");
                }
                break;
            case 7:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Golem");
                }
                break;
            case 8:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Sheep");
                }
                break;
            case 9:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Pig");
                }
                break;
            case 10:
                for (int x = 0; x < 10; x++) {
                    player.sendChatMessage("/twitchy Sentry");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Potion Section
     * /twitchy potion (potionid, duration, amplification)
     */
    public static void randompotion(EntityClientPlayerMP player) {
        Random r = new Random();
        // Rolls 1-100
        int x = (r.nextInt(100) + 1);
        int potion;
        // Percentages or Weights || Needs to equal 1.00 or 100%
        // Instant Damage goes nowhere, it's there to remind you to add up to 1.00 or 100%. its Percentage is whatever
        // is leftover from the others to reach 100%.
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
                player.sendChatMessage("/twitchy potion 20 200 10");
                break;
            case 1:
                // paralysis 10%
                // player.sendChatMessage("paralysis 16%");
                player.sendChatMessage("/twitchy potion 25 200 0");
                break;
            case 2:
                // possession 9%
                // player.sendChatMessage("possession 16%");
                player.sendChatMessage("/twitchy potion 26 100 0");
                break;
            case 3:
                // speed 10%
                // player.sendChatMessage("speed 16%");
                player.sendChatMessage("/twitchy potion 1 1200 100");
                break;
            case 4:
                // Fire fuse 9%
                // player.sendChatMessage("Fire Fuse 16%");
                player.sendChatMessage("/twitchy potion 109 25 2");
                break;
            case 5:
                // Resized 11%
                // player.sendChatMessage("Resized 16%");
                player.sendChatMessage("/twitchy potion 77 3600 100");
                break;
            case 6:
                // Waking Nightmare 10%
                // player.sendChatMessage("Waking Nightmare 16%");
                player.sendChatMessage("/twitchy potion 92 3600 10");
                break;
            case 7:
                // Boost 10%
                // player.sendChatMessage("Boost 16%");
                player.sendChatMessage("/twitchy potion 101 1200 10");
                break;
            case 8:
                // Heavy Heart 9%
                // player.sendChatMessage("Heavy Heart 16%");
                player.sendChatMessage("/twitchy potion 108 200 10");
                break;
            case 9:
                // Hallucinations 10%
                // player.sendChatMessage("Hallucinations 16%");
                player.sendChatMessage("/twitchy potion 64 3600 10");
                break;
            case 10:
                // Instant Damage 3%
                player.sendChatMessage("Oop, here comes the boom! :)");
                player.sendChatMessage("/twitchy potion 7 200 1000");
                break;
            default:
                break;
        }
    }

    /**
     * Mob Raid Section
     */
    // TODO Update with new, better monsters. More Health, More Damage, faster.
    public static void mobraid(EntityClientPlayerMP player) {
        Random r = new Random();
        int mob = r.nextInt(11);
        switch (mob) {
            case 0:
                player.sendChatMessage("A Horde of Skeletons has chased some raiders from the previous channel!");
                for (int x = 0; x < 60; x++) {
                    player.sendChatMessage("/twitchy Skeleton");
                }
                break;
            case 1:
                player.sendChatMessage("The arrival of Raiders seems to have stirred up a Zombie Horde!");
                for (int x = 0; x < 150; x++) {
                    player.sendChatMessage("/twitchy Zombie");
                }
                break;
            case 2:
                player.sendChatMessage("WHO DID DAMAGE TO THE CHICKEN?!");
                for (int x = 0; x < 300; x++) {
                    player.sendChatMessage("/twitchy Chicken");
                }
                break;
            case 3:
                player.sendChatMessage("The Raiders have brought a stampede of Cows with them!");
                for (int x = 0; x < 300; x++) {
                    player.sendChatMessage("/twitchy Cow");
                }
                break;
            case 4:
                player.sendChatMessage("The Raiders brought some Slimes with em!");
                for (int x = 0; x < 40; x++) {
                    player.sendChatMessage("/twitchy Slime");
                }
                break;
            case 5:
                player.sendChatMessage("With the arrival of raiders, an old friend has come to visit! :)");
                for (int x = 0; x < 150; x++) {
                    player.sendChatMessage("/twitchy DireWolf");
                }
                break;
            case 6:
                player.sendChatMessage("The sudden rush of Raiders has awoken a horde of Fallen Knights!");
                for (int x = 0; x < 100; x++) {
                    player.sendChatMessage("/twitchy FallenKnight");
                }
                break;
            case 7:
                player.sendChatMessage("It seems the Raiders have brought a few golems with them!");
                for (int x = 0; x < 50; x++) {
                    player.sendChatMessage("/twitchy Golem");
                }
                break;
            case 8:
                player.sendChatMessage("Turns out the Raiders were shepherds in a past life.");
                for (int x = 0; x < 300; x++) {
                    player.sendChatMessage("/twitchy Sheep");
                }
                break;
            case 9:
                player.sendChatMessage("With squealing in the distance, you hear them approach.... Raiders....");
                for (int x = 0; x < 300; x++) {
                    player.sendChatMessage("/twitchy Pig");
                }
                break;
            case 10:
                player.sendChatMessage("Holy smokes! The Sentries spotted the Raiders! Kill 'em quick!");
                for (int x = 0; x < 100; x++) {
                    player.sendChatMessage("/twitchy Sentry");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Sub Section
     */
    // TODO add more Events to Sub Method
    // TODO Update to new Server-sided methods
    public static void substuff(EntityClientPlayerMP player) {
        Random r = new Random();
        int mob = r.nextInt(2);
        switch (mob) {
            case 0:
                player.sendChatMessage("Zoomin' Sentries incoming! Thanks for the Sub!");
                // Speed Potion || They be zoomin.
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~ ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:1,Amplifier:2,Duration:3600}]}},Riding:{id:XPOrb}}");
                for (int x = 0; x < 50; x++) {
                    player.sendChatMessage("/summon sentryRobot");
                }
                break;
            case 1:
                player.sendChatMessage("Fireballs and Hallucinations? What's not to love? Thanks for the Sub!");
                // Potion ||
                player.sendChatMessage(
                    "/summon ThrownPotion ~ ~2 ~ {Potion:{id:373,Damage:16395,Count:1,tag:{CustomPotionEffects:[{Id:64,Amplifier:10,Duration:3600}]}},Riding:{id:XPOrb}}");
                for (int x = 0; x < 100; x++) {
                    player.sendChatMessage("/summon Blaze");
                }
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    /**
     * Follow Section
     */
    // TODO Add Follower section and have events on Follow.

    // TODO Delete this section
    public static void spawningtest(EntityClientPlayerMP player) {
        player.sendChatMessage("/twitchy tffirebeetle");
    }

    public static void guiclose() {
        Minecraft.getMinecraft()
            .displayGuiScreen((GuiScreen) null);
        Minecraft.getMinecraft()
            .setIngameFocus();
    }
}

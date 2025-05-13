package com.xyrth.twitchy.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class Events {

    public static void spawnskeleton() {

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        player.sendChatMessage("/summon Skeleton");
    }

    public static void potion() {

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        player.addChatMessage(new ChatComponentText("Potion"));
    }
}

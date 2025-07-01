package com.xyrth.twitchy.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChatUtil {

    @SideOnly(Side.CLIENT)
    private static void writeChatString(String message) {
        if (Minecraft.getMinecraft().thePlayer != null) sendChatToPlayer(Minecraft.getMinecraft().thePlayer, message);
    }

    public static void sendChatToPlayer(EntityPlayer player, String message) {
        for (String line : message.split("\\n")) {
            player.addChatMessage(new ChatComponentTranslation(line));
        }
    }
}

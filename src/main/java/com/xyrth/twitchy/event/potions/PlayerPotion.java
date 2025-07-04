package com.xyrth.twitchy.event.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.xyrth.twitchy.event.GenericPotionEvent;

public class PlayerPotion extends GenericPotionEvent {

    public PlayerPotion(World world, int potionid, int duration, int amp, EntityLivingBase player) {
        super(world, potionid, duration, amp, player);

        player.addPotionEffect(new PotionEffect(potionid, duration, amp));
    }
}

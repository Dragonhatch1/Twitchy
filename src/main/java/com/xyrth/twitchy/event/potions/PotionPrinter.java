package com.xyrth.twitchy.event.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.xyrth.twitchy.event.GenericPotionEvent;

public class PotionPrinter extends GenericPotionEvent {

    public PotionPrinter(World world, int x, int y, int z, EntityLivingBase targetEntity) {
        super(world, x, y, z, targetEntity);

        int id = 1;

        while (id != 500) {
            targetEntity.addPotionEffect(new PotionEffect(id, 100, 0));
            System.out.println("Potion ID: " + id + " " + targetEntity.getActivePotionEffects());
            targetEntity.clearActivePotions();
            id++;
        }
    }
}

package com.xyrth.twitchy.event.spawn.npc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.world.World;

import com.xyrth.twitchy.event.GenericNpcEvent;

import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCGolem;
import noppes.npcs.entity.EntityNpcCrystal;
import noppes.npcs.entity.EntityNpcDragon;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import twilightforest.entity.EntityTFHelmetCrab;
import twilightforest.entity.EntityTFIceShooter;
import twilightforest.entity.EntityTFSkeletonDruid;
import twilightforest.entity.EntityTFTowerGolem;
import twilightforest.entity.EntityTFTroll;
import twilightforest.entity.EntityTFYeti;
import twilightforest.entity.boss.EntityTFIceCrystal;
import twilightforest.entity.boss.EntityTFLich;
import twilightforest.entity.passive.EntityTFDeer;
import twilightforest.entity.passive.EntityTFPenguin;
import twilightforest.entity.passive.EntityTFQuestRam;
import twilightforest.entity.passive.EntityTFRaven;

public class SpawnSubNpc extends GenericNpcEvent {

    public SpawnSubNpc(World world, double x, double y, double z, EntityLivingBase targetEntity, String username,
        int size, int modelType, int months) {
        super(world, x, y, z, targetEntity, username, size, modelType, months);

        EntityCustomNpc npc = new EntityCustomNpc(world);
        npc.setLocationAndAngles(x, y, z, npc.rotationYaw, npc.rotationPitch);

        String title = months + " Month Sub";

        npc.display.setName(username);
        npc.display.title = title;
        npc.display.modelSize = size;
        switch (modelType) {
            case 1:
                npc.modelData.setEntityClass(EntityNpcCrystal.class);
                npc.display.texture = "customnpcs:textures/entity/crystal/EnderCrystal.png";
                break;
            case 2:
                npc.modelData.setEntityClass(EntityNPCGolem.class);
                npc.display.texture = "customnpcs:textures/entity/golem/Iron Golem.png";
                break;
            case 3:
                npc.modelData.setEntityClass(EntityBlaze.class);
                npc.display.texture = "minecraft:textures/entity/blaze.png";
                break;
            case 4:
                npc.modelData.setEntityClass(EntityBat.class);
                npc.display.texture = "minecraft:textures/entity/bat.png";
                break;
            case 5:
                npc.modelData.setEntityClass(EntityCaveSpider.class);
                npc.display.texture = "minecraft:textures/entity/spider/cave_spider.png";
                break;
            case 6:
                npc.modelData.setEntityClass(EntityChicken.class);
                npc.display.texture = "minecraft:textures/entity/chicken.png";
                break;
            case 7:
                npc.modelData.setEntityClass(EntityCow.class);
                npc.display.texture = "minecraft:textures/entity/cow.png";
                break;
            case 8:
                npc.modelData.setEntityClass(EntityCreeper.class);
                npc.display.texture = "minecraft:textures/entity/creeper/creeper.png";
                break;
            case 9:
                npc.modelData.setEntityClass(EntityNpcDragon.class);
                npc.display.texture = "customnpcs:textures/entity/dragon/BlackDragon.png";
                break;
            case 10:
                npc.modelData.setEntityClass(EntityTFQuestRam.class);
                npc.display.texture = "twilightforest:textures/model/questram.png";
                break;
            case 11:
                npc.modelData.setEntityClass(EntityTFPenguin.class);
                npc.display.texture = "twilightforest:textures/model/penguin.png";
                break;
            case 12:
                npc.modelData.setEntityClass(EntityTravelingTrunk.class);
                npc.display.texture = "thaumcraft:textures/models/trunk.png";
                break;
            case 13:
                npc.modelData.setEntityClass(EntityTFRaven.class);
                npc.display.texture = "twilightforest:textures/model/raven.png";
                break;
            case 14:
                npc.modelData.setEntityClass(EntityTFHelmetCrab.class);
                npc.display.texture = "twilightforest:textures/model/helmetcrab.png";
                break;
            case 15:
                npc.modelData.setEntityClass(EntityTFIceCrystal.class);
                npc.display.texture = "twilightforest:textures/model/icecrystal.png";
                break;
            case 16:
                npc.modelData.setEntityClass(EntityTFSkeletonDruid.class);
                npc.display.texture = "twilightforest:textures/model/skeletondruid.png";
                break;
            case 17:
                npc.modelData.setEntityClass(EntityTFIceShooter.class);
                npc.display.texture = "twilightforest:textures/model/iceshooter.png";
                break;
            case 18:
                npc.modelData.setEntityClass(EntityTFTowerGolem.class);
                npc.display.texture = "twilightforest:textures/model/carminitegolem.png";
                break;
            case 19:
                npc.modelData.setEntityClass(EntityTFTroll.class);
                npc.display.texture = "twilightforest:textures/model/troll.png";
                break;
            case 20:
                npc.modelData.setEntityClass(EntityTFLich.class);
                npc.display.texture = "twilightforest:textures/model/twilightlich64.png";
                break;
            case 21:
                npc.modelData.setEntityClass(EntityTFDeer.class);
                npc.display.texture = "twilightforest:textures/model/wilddeer.png";
                break;
            case 22:
                npc.modelData.setEntityClass(EntityTFYeti.class);
                npc.display.texture = "twilightforest:textures/model/yeti2.png";
                break;
            case 23:
                npc.modelData.setEntityClass(EntityEldritchGolem.class);
                npc.display.texture = "thaumcraft:textures/models/eldritch_golem.png";
                break;
            case 24:
                npc.modelData.setEntityClass(EntityPech.class);
                npc.display.texture = "thaumcraft:textures/models/pech_stalker.png";
                break;
            case 25:
                npc.modelData.setEntityClass(EntityEldritchGuardian.class);
                npc.display.texture = "thaumcraft:textures/models/eldritch_guardian.png";
                break;
        }
        // spawns mob in the world.
        world.spawnEntityInWorld(npc);
    }
}

package com.twitchy;

import com.twitchy.client.*;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import com.twitchy.chat.ChatCommandConfig;
import com.twitchy.command.CommandTwitchy;
import com.twitchy.entity.EntityViewerFollower;
import com.twitchy.entity.MobSpawningConfig;
import com.twitchy.entity.ViewerFollowerGear;
import com.twitchy.rewards.RewardConfig;
import com.twitchy.rewards.ViewerLinkRegistry;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        RewardConfig.load();
        ViewerLinkRegistry.load();
        ChatCommandConfig.load();
        ViewerFollowerGear.load();
        MobSpawningConfig.load();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ClientCommandHandler.instance.registerCommand(new CommandTwitchy());
        FMLCommonHandler.instance()
            .bus()
            .register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerFlipRenderer());
        MinecraftForge.EVENT_BUS.register(new ToastEffect());
        RenderingRegistry.registerEntityRenderingHandler(EntityViewerFollower.class, new RenderViewerFollower());
        FollowerModelRegistry.registerDefaults();
    }

    @Override
    public boolean canOwnTwitchSession() {
        return true;
    }
}

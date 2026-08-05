package com.twitchy;

import com.twitchy.client.PlayerFlipRenderer;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.client.ClientCommandHandler;

import com.twitchy.chat.ChatCommandConfig;
import com.twitchy.client.ClientEventHandler;
import com.twitchy.command.CommandTwitchy;
import com.twitchy.rewards.RewardConfig;
import com.twitchy.rewards.ViewerLinkRegistry;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // The client keeps its own copy too, both to look up default titles/ids for /twitchy testredeem
        // and because a single-player world runs its own integrated server against this same file.
        RewardConfig.load();
        ViewerLinkRegistry.load();
        ChatCommandConfig.load();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ClientCommandHandler.instance.registerCommand(new CommandTwitchy());
        FMLCommonHandler.instance()
            .bus()
            .register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerFlipRenderer());
    }

    @Override
    public boolean canOwnTwitchSession() {
        return true;
    }
}

package com.twitchy.network;

import com.twitchy.Twitchy;
import com.twitchy.api.TwitchApiClient;
import com.twitchy.client.TwitchSessionManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageRedeemResultHandler implements IMessageHandler<MessageRedeemResult, IMessage> {

    @Override
    public IMessage onMessage(MessageRedeemResult message, MessageContext ctx) {
        if (message.redemptionId == null || message.redemptionId.isBlank()) return null;
        if (!TwitchSessionManager.INSTANCE.hasStoredToken()) return null;

        TwitchApiClient
            .updateRedemptionStatus(
                TwitchSessionManager.INSTANCE.credentials(),
                message.twitchRewardId,
                message.redemptionId,
                message.success)
            .exceptionally(ex -> {
                Twitchy.LOG.warn(
                    "Failed to mark redemption {} as fulfilled/canceled: {}",
                    message.redemptionId,
                    ex.getMessage());
                return null;
            });
        return null;
    }
}

package com.twitchy.chat;

public class ChatCommand {

    /** e.g. "!discord" - matched against the first word of a message, case-insensitive. */
    public String trigger;

    /** Supports {viewer} placeholder for the chatter's display name. */
    public String response;

    public boolean enabled = true;

    /** Per-command cooldown, so spamming the trigger doesn't spam the response back. */
    public int cooldownSeconds = 5;
}

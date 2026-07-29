package com.twitchy.rewards;

public class RewardMapping {

    /**
     * Stable local identifier, chosen once by whoever set up the pack/server. Never changes,
     * and is shared identically across every streamer - this is what the server matches on.
     */
    public String key;

    public boolean enabled = true;

    /** Used only when Twitchy creates this reward on a streamer's channel for the first time. */
    public String title;
    public int cost = 100;
    public String prompt = "";

    public RewardAction action;
}

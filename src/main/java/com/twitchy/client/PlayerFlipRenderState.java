package com.twitchy.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerFlipRenderState {

    private static final Set<UUID> flipped = Collections.synchronizedSet(new HashSet<>());

    private PlayerFlipRenderState() {}

    public static void add(UUID id) {
        flipped.add(id);
    }

    public static void remove(UUID id) {
        flipped.remove(id);
    }

    public static boolean isFlipped(UUID id) {
        return flipped.contains(id);
    }
}

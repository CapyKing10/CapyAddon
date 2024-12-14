package com.capy.capyaddon.event;

import net.minecraft.entity.player.PlayerEntity;

public class ClientPlayerDeathEvent {
    private static final ClientPlayerDeathEvent INSTANCE = new ClientPlayerDeathEvent();
    public static ClientPlayerDeathEvent get() {
        return INSTANCE;
    }
}

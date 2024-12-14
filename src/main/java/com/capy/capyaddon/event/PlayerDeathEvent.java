package com.capy.capyaddon.event;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerDeathEvent {
    private static final PlayerDeathEvent INSTANCE = new PlayerDeathEvent();

    private PlayerEntity entity;

    public PlayerEntity getPlayer() {
        return this.entity;
    }

    public static PlayerDeathEvent get(PlayerEntity entity) {
        INSTANCE.entity = entity;
        return INSTANCE;
    }
}

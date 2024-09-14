package com.capy.capyaddon.event;

import net.minecraft.entity.player.PlayerEntity;

public class TotemPopEvent {
    private static final TotemPopEvent INSTANCE = new TotemPopEvent();

    private PlayerEntity entity;

    public PlayerEntity getPlayer() {
        return this.entity;
    }

    public static TotemPopEvent get(PlayerEntity entity) {
        INSTANCE.entity = entity;
        return INSTANCE;
    }
}

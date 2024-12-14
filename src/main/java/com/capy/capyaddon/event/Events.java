package com.capy.capyaddon.event;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import static meteordevelopment.meteorclient.MeteorClient.EVENT_BUS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Events {
    private static Events INSTANCE;

    public static void init() {
        EVENT_BUS.subscribe(INSTANCE);
    }

    public static Events get() {
        if (INSTANCE == null) INSTANCE = new Events();
        return INSTANCE;
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityStatusS2CPacket p)) return;

        if (p.getStatus() != 35) return;

        Entity entity = p.getEntity(mc.world);

        PlayerEntity player = (PlayerEntity) entity;

        EVENT_BUS.post(TotemPopEvent.get(player));
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.deathTime > 0 || player.getHealth() <= 0) {
                EVENT_BUS.post(PlayerDeathEvent.get(player));
            }
        }
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        assert mc.player != null;
        if (event.screen instanceof DeathScreen) {
            EVENT_BUS.post(ClientPlayerDeathEvent.get());
        }
    }
}

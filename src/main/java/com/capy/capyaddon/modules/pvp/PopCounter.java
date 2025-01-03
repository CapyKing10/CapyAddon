package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.event.ClientPlayerDeathEvent;
import com.capy.capyaddon.event.PlayerDeathEvent;
import com.capy.capyaddon.event.TotemPopEvent;
import com.capy.capyaddon.utils.cLogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class PopCounter extends Module {
    public final Object2IntMap<UUID> totemPopMap = new Object2IntOpenHashMap<>();

    public SettingGroup sgLog = settings.createGroup("Log Settings");

    // Log Settings

    private final Setting<Boolean> ignoreSelf = sgLog.add(new BoolSetting.Builder()
        .name("ignore-self")
        .description("Ignore yourself when popping")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> resetPopsOnDeath = sgLog.add(new BoolSetting.Builder()
        .name("reset-pops-on-death")
        .description("Reset the amount of pops when you die")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> stackMessages = sgLog.add(new BoolSetting.Builder()
        .name("stack-messages")
        .description("Stack the pop messages so it doesn't spam your chat")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> stackMessagesPerPlayer = sgLog.add(new BoolSetting.Builder()
        .name("stack-messages-per-player")
        .description("stack the pop messages for each player")
        .visible(stackMessages::get)
        .defaultValue(true)
        .build()
    );

    public PopCounter() {
        super(CapyAddon.PVP, "PopCounter", "Counts the number of times you have popped someone's totem");
    }

    @Override
    public void onActivate() {
        totemPopMap.clear();
    }

    @EventHandler
    private void onTotemPop(TotemPopEvent event) {
        if ((event.getPlayer().equals(mc.player) && ignoreSelf.get())) return;

        synchronized (totemPopMap) {
            int pops = getPops(event.getPlayer().getUuid());
            totemPopMap.put(event.getPlayer().getUuid(), ++pops);

            cLogUtils.sendTotemPopMessage(pops, event.getPlayer(), stackMessages.get(), stackMessagesPerPlayer.get());
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        synchronized (totemPopMap) {
            if (!totemPopMap.containsKey(event.getPlayer().getUuid())) return;

            int pops = totemPopMap.removeInt(event.getPlayer().getUuid());

            cLogUtils.sendMessage(event.getPlayer().getName().getString() + " died after popping " + Formatting.YELLOW + Formatting.BOLD + pops + Formatting.RESET + (pops == 1 ? " totem" : " totems"), stackMessages.get());
        }
    }

    @EventHandler
    private void onClientPlayerDeath(ClientPlayerDeathEvent event) {
        if (resetPopsOnDeath.get()) {
            totemPopMap.clear();
        }
    }

    public int getPops(UUID uuid) {
        return totemPopMap.getOrDefault(uuid, 0);
    }
}


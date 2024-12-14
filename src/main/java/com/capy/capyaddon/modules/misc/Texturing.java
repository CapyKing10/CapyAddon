package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.cLogUtils;
import meteordevelopment.meteorclient.events.entity.player.PlaceBlockEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Texturing extends Module {
    public SettingGroup sgGeneral = settings.getDefaultGroup();

    public Texturing() {
        super(CapyAddon.MISC, "texturing", "a module that switches between all blocks in the hotbar randomly");
    }

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("how the module should behave")
        .defaultValue(Mode.Interval)
        .build()
    );

    public final Setting<Boolean> neverSame = sgGeneral.add(new BoolSetting.Builder()
        .name("never-same")
        .description("never get the same slot twice")
        .defaultValue(false)
        .build()
    );

    public final Setting<Integer> switchInterval = sgGeneral.add(new IntSetting.Builder()
        .name("interval (ms)")
        .description("Amount of time between switching the items.")
        .defaultValue(1000)
        .min(0)
        .sliderMax(5000)
            .visible(() -> mode.get() == Mode.Interval)
        .build()
    );

    private int timer;
    private final List<Integer> slots = new ArrayList<>();
    private int lastSlot = -1;

    @Override
    public void onActivate() {
        slots.clear();
        for (int i = 0; i < 9; i++) {
            if (MinecraftClient.getInstance().player.getInventory().getStack(i).getItem() instanceof BlockItem) {
                slots.add(i);
            }
        }

        if (slots.isEmpty()) {
            cLogUtils.sendMessage("There are " + Formatting.RED + "No " + Formatting.WHITE + "blocks in your hotbar.", true);
            toggle();
        } else {
            lastSlot = slots.get(new Random().nextInt(slots.size()));
            MinecraftClient.getInstance().player.getInventory().selectedSlot = lastSlot;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mode.get() != Mode.Interval) return;

        if (timer <= 0 && !slots.isEmpty()) {
            swap();
            timer = switchInterval.get() / 50;
        } else {
            timer--;
        }
    }

    @EventHandler
    private void onBlockPlace(PlaceBlockEvent event) {
        if (mode.get() == Mode.Place) {
            swap();
        }
    }

    private void swap() {
        int slot;
        if (neverSame.get()) {
            do {
                slot = slots.get(new Random().nextInt(slots.size()));
            } while (slot == lastSlot);
        } else {
            slot = slots.get(new Random().nextInt(slots.size()));
        }

        MinecraftClient.getInstance().player.getInventory().selectedSlot = slot;
        lastSlot = slot;
    }

    @Override
    public void onDeactivate() {
        slots.clear();
    }

    private enum Mode {
        Place,
        Interval
    }
}

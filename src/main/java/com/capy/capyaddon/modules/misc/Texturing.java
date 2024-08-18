package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.LogUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Texturing extends Module {
    public Texturing() {
        super(CapyAddon.MISC, "Texturing", "a module that switches between all blocks in the hotbar randomly");
    }

    public SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> switchInterval = sgGeneral.add(new IntSetting.Builder()
        .name("interval (ms)")
        .description("Amount of time between switching the items.")
        .defaultValue(1000)
        .min(100)
        .sliderMax(5000)
        .build()
    );

    private int timer;
    private final List<Integer> slots = new ArrayList<>();

    @Override
    public void onActivate() {
        slots.clear(); // Clear slots before repopulating

        // Get all blocks in the hotbar
        for (int i = 0; i < 9; i++) { // Only loop through the valid hotbar slots (0-8)
            ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem) {
                slots.add(i); // Add only valid slots with BlockItem
            }
        }

        if (slots.isEmpty()) {
            LogUtils.sendMessage("There are " + Formatting.RED + "No " + Formatting.WHITE + "blocks in your hotbar.", true);
            toggle();
        } else {
            // Select a random block item slot initially
            int randomIndex = new Random().nextInt(slots.size());
            MinecraftClient.getInstance().player.getInventory().selectedSlot = slots.get(randomIndex);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (timer <= 0) {
            if (!slots.isEmpty()) {
                int randomIndex = new Random().nextInt(slots.size());
                int selectedSlot = slots.get(randomIndex);
                MinecraftClient.getInstance().player.getInventory().selectedSlot = selectedSlot;
            }
            timer = switchInterval.get() / 50;
        } else {
            timer--;
        }
    }

    @Override
    public void onDeactivate() {
        slots.clear();
    }
}

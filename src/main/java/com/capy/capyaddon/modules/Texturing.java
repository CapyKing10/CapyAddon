package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.LogUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Texturing extends Module {
    public Texturing() {
        super(CapyAddon.CATEGORY, "Texturing", "a module that switches between all blocks in the hotbar randomly");
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
    ArrayList<Integer> slots = new ArrayList<>();

    @Override
    public void onActivate() {
        LogUtils.sendMessage(Formatting.WHITE + "Turned the module called " + Formatting.GOLD + "Texturing" + Formatting.GREEN + " On");

        // get all blocks in the hotbar
        for (int i = 0; i < 10; i++) {
            ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem) {
                MinecraftClient.getInstance().player.getInventory().selectedSlot = i;
                slots.add(i);
            }
            if (i > 8) {
                if (slots.isEmpty()) {
                    LogUtils.sendMessage("There are " + Formatting.RED + "No " + Formatting.WHITE + "blocks in your hotbar.");
                    toggle();
                }
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (timer <= 0) {
            if (!slots.isEmpty()) {
                int randomIndex = new Random().nextInt(slots.size());
                if (randomIndex > 9) {
                    MinecraftClient.getInstance().player.getInventory().selectedSlot = 9;
                } else {
                    MinecraftClient.getInstance().player.getInventory().selectedSlot = slots.get(randomIndex);
                }
            }
            int delay = switchInterval.get() / 50;
            timer = delay;
        } else {
            timer--;
        }
    }

    public void onDeactivate() {
        slots.clear();
        LogUtils.sendMessage(Formatting.WHITE + "Turned the module called " + Formatting.GOLD + "Texturing" + Formatting.RED + " Off");
    }
}

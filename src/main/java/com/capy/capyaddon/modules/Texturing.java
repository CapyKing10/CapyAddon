package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.LogUtils;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

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

        // interval shit
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!slots.isEmpty()) {
                    int randomIndex = new Random().nextInt(slots.size());
                    if (randomIndex > 9) {
                        MinecraftClient.getInstance().player.getInventory().selectedSlot = 9;
                    } else {
                        MinecraftClient.getInstance().player.getInventory().selectedSlot = slots.get(randomIndex);
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, switchInterval.get());
    }

    public void onDeactivate() {
        slots.clear();
        LogUtils.sendMessage(Formatting.WHITE + "Turned the module called " + Formatting.GOLD + "Texturing" + Formatting.RED + " Off");
    }
}

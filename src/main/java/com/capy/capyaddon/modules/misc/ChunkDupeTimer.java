package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

/*
    Credits to Eonexe on github
*/

public class ChunkDupeTimer extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    public final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("Delay displayed in seconds.")
        .defaultValue(30.0d)
        .range(1.0d, 1000.0d)
        .build()
    );

    public ChunkDupeTimer() {
        super(CapyAddon.MISC, "ChunkDupeTimer", "shows a timer on the multiplayer screen to perform the chunkdupe optionally");
    }
}

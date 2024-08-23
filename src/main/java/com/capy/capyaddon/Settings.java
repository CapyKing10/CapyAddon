package com.capy.capyaddon;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class Settings {
    private static Settings INSTANCE;

    public static final SettingGroup sgCapyAddon = Config.get().settings.createGroup("CapyAddon");
    public Setting<Boolean> windowName = sgCapyAddon.add(new BoolSetting.Builder()
        .name("window-name")
        .description("add the capyaddon name to the name of the mc window")
        .defaultValue(true)
        .build()
    );

    public Setting<SettingColor> themeColor = sgCapyAddon.add(new ColorSetting.Builder()
        .name("theme-color")
        .description("theme color")
        .defaultValue(new SettingColor(120, 117, 255))
        .build()
    );

    public Setting<SettingColor> bracketsColor = sgCapyAddon.add(new ColorSetting.Builder()
        .name("bracket-color")
        .description("color of the []")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );

    public Setting<Boolean> useThemeColoForPrefix = sgCapyAddon.add(new BoolSetting.Builder()
        .name("use-theme-for-prefix")
        .description("read the name")
        .defaultValue(true)
        .build()
    );

    public Setting<Boolean> useBracketsColor = sgCapyAddon.add(new BoolSetting.Builder()
        .name("use-brackets-color")
        .description("read the name")
        .defaultValue(true)
        .visible(useThemeColoForPrefix::get)
        .build()
    );

    public static Settings get() {
        if (INSTANCE == null) INSTANCE = new Settings();
        return INSTANCE;
    }
}

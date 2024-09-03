package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.cLogUtils;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

public class ClientPrefix extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> global = sgGeneral.add(
        new BoolSetting.Builder()
            .name("Global")
            .description("Use the prefix on meteor client. If toggled when module is active, restart module.")
            .defaultValue(true)
            .build()
    );

    public ClientPrefix() {
        super(CapyAddon.MISC, "prefix-modifier", "Modifies the client's prefix.");
    }

    public void onActivate() {
        ChatUtils.registerCustomPrefix("com.capy.capyaddon", cLogUtils::getPrefix);
    }

    public void onDeactivate() {
        ChatUtils.unregisterCustomPrefix("com.capy.capyaddon");
        ChatUtils.registerCustomPrefix("meteordevelopment.meteorclient", cLogUtils::getPrefix);
    }

    @EventHandler
    public void onTick() {
        if (global.get()) {
            ChatUtils.registerCustomPrefix("meteordevelopment.meteorclient", cLogUtils::getPrefix);
        } else {
            ChatUtils.unregisterCustomPrefix("meteordevelopment.meteorclient");
        }
    }
}

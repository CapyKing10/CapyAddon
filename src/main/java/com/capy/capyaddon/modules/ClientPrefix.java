package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
        super(CapyAddon.CATEGORY, "prefix-modifier", "Modifies the client's prefix.");
    }

    public void onActivate() {
        if (global.get()) {
            ChatUtils.registerCustomPrefix("meteordevelopment.meteorclient", this::getPrefix);
            ChatUtils.registerCustomPrefix("com.capy.capyaddon", this::getPrefix);
        } else {
            ChatUtils.registerCustomPrefix("com.capy.capyaddon", this::getPrefix);
        }
    }

    public void onDeactivate() {
        ChatUtils.unregisterCustomPrefix("com.capy.capyaddon");
        ChatUtils.registerCustomPrefix("meteordevelopment.meteorclient", this::getPrefix);
    }

    public Text getPrefix() {
        MutableText name1 = Text.literal("Capy");
        MutableText name2 = Text.literal("Addon");
        MutableText prefix = Text.literal("");
        name1.setStyle(name1.getStyle().withFormatting(Formatting.GOLD));
        name2.setStyle(name2.getStyle().withFormatting(Formatting.YELLOW));
        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY))
            .append(Text.literal("["))
            .append(name1)
            .append(name2)
            .append(Text.literal("] "));
        return prefix;
    }

}

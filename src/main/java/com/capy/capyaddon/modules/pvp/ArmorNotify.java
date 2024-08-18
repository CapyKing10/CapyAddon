package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.LogUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

public class ArmorNotify extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Modes> logMode = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("Log Mode")
        .description("how the module should log")
        .defaultValue(Modes.chat)
        .build()
    );

    private final Setting<Integer> limit = sgGeneral.add(new IntSetting.Builder()
        .name("Durability Limit")
        .description("The Durability it should notify you on.")
        .defaultValue(2)
        .sliderRange(1, 200)
        .build()
    );

    public ArmorNotify() {
        super(CapyAddon.PVP, "ArmorNotify", "notifies you whenever an armor piece is gonna run out of durability");
    }

    public boolean logged = false;
    public int dur;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        assert mc.player != null;
        checkArmorDurability(mc.player);
    }

    private void checkArmorDurability(PlayerEntity player) {
        for (ItemStack armorItem : player.getArmorItems()) {
            if (armorItem.isDamageable()) {
                int currentDurability = armorItem.getMaxDamage() - armorItem.getDamage();

                if (dur == currentDurability && logged) {
                    continue;
                }

                if (currentDurability <= limit.get()) {
                    if (logMode.get() == Modes.chat) LogUtils.sendMessage(Formatting.RED + "[!] " + Formatting.WHITE + "Your " + Formatting.GOLD + armorItem.getName().getString() + Formatting.WHITE + " is about to run out of durability " + Formatting.GRAY + "(" + currentDurability + ")", true);
                    if (logMode.get() == Modes.notification) LogUtils.sendNotification(armorItem.getName().getString() + " low dur " + currentDurability);
                    logged = true;
                } else {
                    logged = false;
                }

                dur = currentDurability;
            }
        }
    }

    public enum Modes {
        chat,
        notification
    }

}

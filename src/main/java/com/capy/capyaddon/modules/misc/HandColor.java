package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class HandColor extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("color for items in hand.")
        .defaultValue(new SettingColor(117, 120, 255, 255))
        .build()
    );

    public HandColor() {
        super(CapyAddon.MISC, "hand-color", "hand color");
    }

    @EventHandler
    public void onRender3d(Render3DEvent event) {
        PlayerEntity player = mc.player;
        MatrixStack matrices = event.matrices;
        if (player != null) {
            render(player, matrices, Hand.MAIN_HAND);
            render(player, matrices, Hand.OFF_HAND);
        }
    }

    private void render(PlayerEntity player, MatrixStack matrices, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.setShaderColor(color.get().r / 255.0F, color.get().g / 255.0F, color.get().b / 255.0F, color.get().a / 255.0F);

            matrices.push();
            matrices.pop();
            RenderSystem.disableBlend();
        }
    }
}

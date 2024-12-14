package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Box extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("box-color")
        .description("color of the box")
        .defaultValue(new SettingColor(117, 120, 255, 255))
        .build()
    );

    public Box() {
        super(CapyAddon.MISC, "box", "roddy rich");
    }

    @EventHandler
    public void onRender3d(Render3DEvent event) {
        ClientPlayerEntity player = mc.player;

        if (player == null) {
            return;
        }

        // Get player's current position
        Vec3d playerPos = player.getPos();
        double playerX = playerPos.x;
        double playerY = playerPos.y;
        double playerZ = playerPos.z;

        // Get render distance in blocks
        int renderDistanceChunks = mc.options.getViewDistance().getValue();
        double renderDistanceBlocks = renderDistanceChunks * 16.0;

        // Calculate box coordinates
        double minX = playerX - renderDistanceBlocks;
        double minY = playerY - renderDistanceBlocks;
        double minZ = playerZ - renderDistanceBlocks;

        double maxX = playerX + renderDistanceBlocks;
        double maxY = playerY + renderDistanceBlocks;
        double maxZ = playerZ + renderDistanceBlocks;

        // Render the box
        event.renderer.box(
            minX, minY, minZ,
            maxX, maxY, maxZ,
            color.get(),
            color.get(),
            ShapeMode.Sides,
            0
        );
    }
}

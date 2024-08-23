package com.capy.capyaddon.hud;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Position extends HudElement {
    public SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("Color for the coordinates.")
        .defaultValue(new SettingColor(120, 117, 255, 255))
        .build()
    );

    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("shadow")
        .description("Render a shadow.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("Scale of the text.")
        .defaultValue(1.0)
        .min(0.5)
        .max(5.0)
        .sliderRange(0.5, 5.0)
        .build()
    );

    public static final HudElementInfo<Position> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "capy-position", "Displays player coordinates.", Position::new);

    public Position() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (mc.player == null) return;

        // Get the player's coordinates
        int x = (int) mc.player.getX();
        int y = (int) mc.player.getY();
        int z = (int) mc.player.getZ();

        // Calculate Nether coordinates (Overworld coordinates divided by 8)
        int netherX = x / 8;
        int netherZ = z / 8;

        // Format the coordinate text
        String prefix = "XYZ ";
        String coords = String.format("%d, %d, %d", x, y, z);
        String netherCoords = String.format("[%d, %d]", netherX, netherZ);

        double scaleValue = scale.get();
        double xPosition = this.getX();
        double yPosition = this.getY();

        // Calculate width for proper positioning
        double prefixWidth = renderer.textWidth(prefix, shadow.get()) * scaleValue;
        double coordsWidth = renderer.textWidth(coords, shadow.get()) * scaleValue;
        double totalWidth = (prefixWidth + coordsWidth + renderer.textWidth(netherCoords, shadow.get()) * scaleValue);
        double totalHeight = renderer.textHeight(shadow.get()) * scaleValue;

        // Render each part of the coordinate text with the correct color
        renderer.text(prefix, xPosition, yPosition, new Color(0xFFFFFFFF), shadow.get(), scaleValue); // XYZ (white)
        renderer.text(coords, xPosition + prefixWidth, yPosition, color.get(), shadow.get(), scaleValue); // Coordinates (color)
        renderer.text(netherCoords, xPosition + prefixWidth + coordsWidth, yPosition, new Color(0xFFFFFFFF), shadow.get(), scaleValue); // Nether coordinates (white)

        // Set the size for the hitbox with scale applied
        this.setSize(totalWidth, totalHeight);
    }
}

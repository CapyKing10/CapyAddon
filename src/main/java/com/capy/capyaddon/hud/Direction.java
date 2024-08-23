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

public class Direction extends HudElement {
    public SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("color")
        .defaultValue(new SettingColor(120, 117, 255, 255))
        .build()
    );

    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("shadow")
        .description("render a shadow")
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

    public static final HudElementInfo<Direction> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "capy-direction", "Capy Addon Direction.", Direction::new);

    public Direction() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (mc.player == null) return;

        String direction = "";
        String direction2 = "";
        switch (mc.player.getHorizontalFacing()) {
            case EAST -> {
                direction = "East";
                direction2 = " [+X]";
            }
            case WEST -> {
                direction = "West";
                direction2 = " [-X]";
            }
            case NORTH -> {
                direction = "North";
                direction2 = " [-Z]";
            }
            case SOUTH -> {
                direction = "South";
                direction2 = " [+Z]";
            }
        }

        double scaleValue = scale.get();

        double x = this.getX();
        double y = this.getY();

        double directionWidth = renderer.textWidth(direction, shadow.get()) * scaleValue;

        renderer.text(direction, x, y, color.get(), shadow.get(), scaleValue);

        int secondaryColor = 0xFFFFFFFF;
        renderer.text(direction2, x + directionWidth, y, new Color(secondaryColor), shadow.get(), scaleValue);

        double totalWidth = directionWidth + (renderer.textWidth(direction2, shadow.get()) * scaleValue);
        double totalHeight = renderer.textHeight(shadow.get()) * scaleValue;

        this.setSize(totalWidth, totalHeight);
    }

}

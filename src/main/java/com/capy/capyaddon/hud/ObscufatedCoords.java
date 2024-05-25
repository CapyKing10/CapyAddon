package com.capy.capyaddon.hud;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ObscufatedCoords extends HudElement {
    public SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> multiplier = sgGeneral.add(new IntSetting.Builder()
        .name("multiplier")
        .description("Amount of times that the Math.random() function gets multiplied before multiplying with the player position")
        .defaultValue(10)
        .min(0)
        .sliderMax(100)
        .build()
    );

    public static final HudElementInfo<ObscufatedCoords> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "obscufated-coords", "Fake Coordinates Display", ObscufatedCoords::new);

    public ObscufatedCoords() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        double spoofedX = Math.round(player.getX() * (Math.random() * multiplier.get()));
        double spoofedY = Math.round(player.getY() * (Math.random() * multiplier.get()));
        double spoofedZ = Math.round(player.getZ() * (Math.random() * multiplier.get()));

        String text = "x: " + spoofedX + " y: " + spoofedY + " z: " + spoofedZ;
        setSize(renderer.textWidth(text, true), renderer.textHeight(true));

        renderer.text(text, x, y, Color.WHITE, true);
    }
}

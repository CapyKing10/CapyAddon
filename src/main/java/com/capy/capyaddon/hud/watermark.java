package com.capy.capyaddon.hud;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class watermark extends HudElement {
    public static final HudElementInfo<watermark> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "watermark", "Capy Addon Watermark.", watermark::new);

    public watermark() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        String text = "CapyAddon 1.0.2";
        setSize(renderer.textWidth(text, true), renderer.textHeight(true));

        renderer.text(text, x, y, Color.WHITE, true);
    }
}

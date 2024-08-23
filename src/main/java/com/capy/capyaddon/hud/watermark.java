package com.capy.capyaddon.hud;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class watermark extends HudElement {
    public SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("shadow")
        .description("render a shadow")
        .defaultValue(true)
        .build()
    );

    public static final HudElementInfo<watermark> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "watermark", "Capy Addon Watermark.", watermark::new);

    public watermark() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        String text = CapyAddon.NAME + " " + CapyAddon.VERSION;
        setSize(renderer.textWidth(text, shadow.get()), renderer.textHeight(shadow.get()));

        renderer.text(text, x, y, Color.WHITE, shadow.get());
    }
}

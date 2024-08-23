package com.capy.capyaddon.hud;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;

public class fps extends HudElement {
    public SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("shadow")
        .description("render a shadow")
        .defaultValue(true)
        .build()
    );

    public static final HudElementInfo<fps> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "fps", "Capy Addon FPS.", fps::new);

    public fps() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        String text = "FPS: " + MinecraftClient.getInstance().getCurrentFps();
        setSize(renderer.textWidth(text, shadow.get()), renderer.textHeight(shadow.get()));

        renderer.text(text, x, y, Color.WHITE, shadow.get());
    }
}

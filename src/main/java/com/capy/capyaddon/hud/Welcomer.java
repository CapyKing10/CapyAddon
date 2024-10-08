package com.capy.capyaddon.hud;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Welcomer extends HudElement {
    public static final HudElementInfo<Welcomer> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "welcome-to-capyaddon", "Welcome senpai", Welcomer::new);

    public SettingGroup sgGeneral = settings.getDefaultGroup();

    public Setting<String> clientName = sgGeneral.add(new StringSetting.Builder()
        .name("Custom Name")
        .description("name")
        .defaultValue("CapyAddon")
        .build()
    );

    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("shadow")
        .description("render a shadow")
        .defaultValue(true)
        .build()
    );

    public Welcomer() {
        super(INFO);
    }

    public String text;

    @Override
    public void render(HudRenderer renderer) {
        if (mc.player == null) text = "player is null, go ingame.";
        if (mc.player != null) text = "Welcome " + mc.player.getName().getString() + " To " + clientName.get() + " :3";
        setSize(renderer.textWidth(text, shadow.get()), renderer.textHeight(shadow.get()));

        renderer.text(text, x, y, Color.WHITE, shadow.get());
    }
}

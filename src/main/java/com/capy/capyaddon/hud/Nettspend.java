package com.capy.capyaddon.hud;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class Nettspend extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> nettScale = sgGeneral.add(new DoubleSetting.Builder()
        .name("nett Scale")
        .description("Modify the size of the net.")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 10)
        .build()
    );

    private final Setting<SideMode> side = sgGeneral.add(new EnumSetting.Builder<SideMode>()
        .name("side")
        .description("inverter")
        .defaultValue(SideMode.Right)
        .build()
    );

    private final Setting<ImageMode> imagemode = sgGeneral.add(new EnumSetting.Builder<ImageMode>()
        .name("Image Mode")
        .description("Picture selection.")
        .defaultValue(ImageMode.drank)
        .build()
    );
    private final Identifier drank = Identifier.of("capyaddon", "drank.png");
    private final Identifier hair = Identifier.of("capyaddon", "hair.png");
    private final Identifier nugget = Identifier.of("capyaddon", "nugget.png");
    private final Identifier smoke = Identifier.of("capyaddon", "smoke.png");

    public static final HudElementInfo<Nettspend> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "nettspend", "Shows cases the rapper", Nettspend::new);

    public Nettspend() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        setSize(450 * nettScale.get(), 755 * nettScale.get());
        Identifier selectedImage;
        switch (imagemode.get()) {
            case drank:
                selectedImage = drank;
                break;
            case Hair:
                selectedImage = hair;
                break;
            case nugget:
                selectedImage = nugget;
                break;
            case smoke:
                selectedImage = smoke;
                break;
            default:
                return;
        }
        GL.bindTexture(selectedImage);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(
            x + (side.get() == SideMode.Left ? nettScale.get() * 450 : 0),
            y,
            nettScale.get() * (side.get() == SideMode.Left ? -450 : 450),
            nettScale.get() * 755,
            new Color(255, 255, 255, 255)
        );

        Renderer2D.TEXTURE.render(new MatrixStack());
    }

    public enum SideMode {
        Right,
        Left
    }
    public enum ImageMode {
        drank,
        Hair,
        nugget,
        smoke
    }
}

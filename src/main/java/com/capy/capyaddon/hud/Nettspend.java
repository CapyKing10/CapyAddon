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
    private final Setting<Double> nettscale = sgGeneral.add(new DoubleSetting.Builder()
        .name("nettscale")
        .description("Modify the size of the nett.")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 10)
        .build()
    );
    private final Setting<SideMode> side = sgGeneral.add(new EnumSetting.Builder<SideMode>()
        .name("Kill Message Mode")
        .description("What kind of messages to send.")
        .defaultValue(SideMode.Right)
        .build()
    );
    private final Setting<ImageMode> imagemode = sgGeneral.add(new EnumSetting.Builder<ImageMode>()
        .name("Image Mode")
        .description("picture")
        .defaultValue(ImageMode.drank)
        .build()
    );

    private final Identifier drank = Identifier.of("capyaddon", "drank.png");
    private final Identifier Hair = Identifier.of("capyaddon", "Hair.png");
    private final Identifier nugget = Identifier.of("capyaddon", "nugget.png");
    private final Identifier smoke = Identifier.of("capyaddon", "smoke.png");

    public static final HudElementInfo<Nettspend> INFO = new HudElementInfo<>(CapyAddon.HUD_GROUP, "nettspend", "Shows cases the raper", Nettspend::new);

    public Nettspend() {super(INFO);}
    @Override
    public void render(HudRenderer renderer) {
        if (imagemode.get() == ImageMode.drank) {
            setSize(450 * nettscale.get(), 755 * nettscale.get());
            MatrixStack matrixStack = new MatrixStack();

            GL.bindTexture(drank);
            Renderer2D.TEXTURE.begin();
            Renderer2D.TEXTURE.texQuad(x + (side.get() == SideMode.Left ? nettscale.get() * 450 : 0), y, nettscale.get() * (side.get() == SideMode.Left ? nettscale.get() * -450 : 450), nettscale.get() * 755, new Color(255, 255, 255, 255));
            Renderer2D.TEXTURE.render(matrixStack);
        } else {
            if (imagemode.get() == ImageMode.Hair) {
                setSize(450 * nettscale.get(), 755 * nettscale.get());
                MatrixStack matrixStack = new MatrixStack();

                GL.bindTexture(Hair);
                Renderer2D.TEXTURE.begin();
                Renderer2D.TEXTURE.texQuad(x + (side.get() == SideMode.Left ? nettscale.get() * 450 : 0), y, nettscale.get() * (side.get() == SideMode.Left ? nettscale.get() * -450 : 450), nettscale.get() * 755, new Color(255, 255, 255, 255));
                Renderer2D.TEXTURE.render(matrixStack);
            } else {
                if (imagemode.get() == ImageMode.nugget) {
                    setSize(450 * nettscale.get(), 755 * nettscale.get());
                    MatrixStack matrixStack = new MatrixStack();

                    GL.bindTexture(nugget);
                    Renderer2D.TEXTURE.begin();
                    Renderer2D.TEXTURE.texQuad(x + (side.get() == SideMode.Left ? nettscale.get() * 450 : 0), y, nettscale.get() * (side.get() == SideMode.Left ? nettscale.get() * -450 : 450), nettscale.get() * 755, new Color(255, 255, 255, 255));
                    Renderer2D.TEXTURE.render(matrixStack);
                } else {
                    if (imagemode.get() == ImageMode.smoke) {
                        setSize(450 * nettscale.get(), 755 * nettscale.get());
                        MatrixStack matrixStack = new MatrixStack();

                        GL.bindTexture(smoke);
                        Renderer2D.TEXTURE.begin();
                        Renderer2D.TEXTURE.texQuad(x + (side.get() == SideMode.Left ? nettscale.get() * 450 : 0), y, nettscale.get() * (side.get() == SideMode.Left ? nettscale.get() * -450 : 450), nettscale.get() * 755, new Color(255, 255, 255, 255));
                        Renderer2D.TEXTURE.render(matrixStack);
                    }
                }
            }
        }
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

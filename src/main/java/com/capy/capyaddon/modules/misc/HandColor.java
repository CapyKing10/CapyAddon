package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.mixin.IGameRenderer;
import com.capy.capyaddon.utils.Shader.ShaderManager;
import com.capy.capyaddon.utils.Shader.Utils;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.*;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

public class HandColor extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("color for items in hand.")
        .defaultValue(new SettingColor(117, 120, 255, 255))
        .build()
    );

    public final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    public final Setting<Integer> fillOpacity = sgGeneral.add(new IntSetting.Builder()
        .name("fill-opacity")
        .description("The opacity of the shape fill.")
        .visible(() -> shapeMode.get() != ShapeMode.Lines)
        .defaultValue(50)
        .range(0, 255)
        .sliderMax(255)
        .build()
    );

    public final Setting<Integer> outlineWidth = sgGeneral.add(new IntSetting.Builder()
        .name("width")
        .description("The width of the shader outline.")
        .defaultValue(1)
        .range(1, 10)
        .sliderRange(1, 5)
        .build()
    );

    public final Setting<Double> glowMultiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("glow-multiplier")
        .description("Multiplier for glow effect")
        .decimalPlaces(3)
        .defaultValue(3.5)
        .min(0)
        .sliderMax(10)
        .build()
    );

    public HandColor() {
        super(CapyAddon.MISC, "hand-color", "hand color");
    }

    @EventHandler
    public void onRender3d(Render3DEvent event) {
        if (mc.player == null || mc.world == null) return;
        ShaderManager.rendering = true;
        ShaderManager.HAND_OUTLINE.beginRender();
        ShaderManager.HAND_OUTLINE.vertexConsumerProvider.setColor(color.get().r, color.get().g, color.get().b, color.get().a);
        RenderSystem.enableBlend();
        Utils.applyShader(() -> {
            MatrixStack matrices = event.matrices;
            matrices.push();
            matrices.multiply(mc.gameRenderer.getCamera().getRotation());
            matrices.translate(0, 0, 0);
            ((IGameRenderer) mc.gameRenderer).irenderHand(
                mc.gameRenderer.getCamera(),
                event.tickDelta,
                matrices.peek().getPositionMatrix()
            );
            matrices.pop();
        });
        ShaderManager.rendering = false;
    }
}

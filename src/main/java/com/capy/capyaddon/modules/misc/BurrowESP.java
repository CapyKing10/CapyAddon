package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BurrowESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private PlayerEntity target;

    public final Setting<Boolean> ignoreSelf = sgGeneral.add(new BoolSetting.Builder().name("ignore-self").description("ignore your own burrows").defaultValue(true).build());

    public final Setting<modes> mode = sgGeneral.add(new EnumSetting.Builder<modes>().name("render-mode").description("How the module should render").defaultValue(modes.shader).build());

    // Text
    private final Setting<Integer> scale = sgGeneral.add(new IntSetting.Builder().name("scale").description("scale").defaultValue(4).visible(() -> mode.get() == modes.text).build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder().name("line-color").description("The line color of the target block rendering.").defaultValue(new SettingColor(0, 0, 255, 190)).visible(() -> mode.get() == modes.text).build());

    // Shader
    public final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).visible(() -> mode.get() == modes.shader).build());
    public final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder().name("side-color").description("The side color.").defaultValue(new SettingColor(255, 255, 255, 25)).visible(() -> mode.get() == modes.shader).build());
    public final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder().name("line-color").description("The line color.").defaultValue(new SettingColor(255, 255, 255, 127)).visible(() -> mode.get() == modes.shader).build());

    public BurrowESP() {
        super(CapyAddon.MISC, "burrow-esp", "straigh outta old oyvey skids");
    }

    private final List<BlockPos> obsidianPos = new ArrayList<>();

    private static final Vector3d pos = new Vector3d();

    @EventHandler
    public void onTick(TickEvent.Post event) {
        target = TargetUtils.getPlayerTarget(100, SortPriority.LowestDistance);
        if (target == mc.player && ignoreSelf.get()) target = null;
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mode.get() != modes.text) return;

        // Check if there's a valid target and if the target is burrowed
        if (target != null && isBurrowed(target)) {
            // Get the target's position
            Vec3d targetPos = target.getPos();

            // Adjust the position to display text at the feet of the burrowed target
            targetPos = targetPos.add(0, 1, 0);

            Vector3d targetPos3D = new Vector3d(targetPos.x, targetPos.y, targetPos.z);

            // Convert the 3D position to 2D for rendering
            if (NametagUtils.to2D(targetPos3D, scale.get())) {
                // Get the target's name to render
                String burrow = "BURROW"; // Target entity related to TNT

                // Begin the nametag rendering at the calculated position
                NametagUtils.begin(targetPos3D);

                // Set up the text renderer (scale, font smoothing, and rendering text)
                TextRenderer.get().begin(1.0, false, true);

                // Render the target's name at the center of the screen
                TextRenderer.get().render(burrow, -TextRenderer.get().getWidth(burrow) / 2.0, 0.0, color.get()); // Red color for emphasis

                // End text rendering
                TextRenderer.get().end();

                // End nametag rendering
                NametagUtils.end();
            }
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (mode.get() != modes.shader) return;
        if (this.target == null || !isBurrowed(target)) return;
        renderBox(event, target.getBlockPos(), sideColor.get(), lineColor.get());
    }

    public void renderBox(Render3DEvent event, BlockPos blockPos, SettingColor sideColor, SettingColor lineColor) {
        double minX = blockPos.getX();
        double minY = blockPos.getY();
        double minZ = blockPos.getZ();

        double maxX = minX + 1.0;
        double maxY = minY + 1.0;
        double maxZ = minZ + 1.0;

        event.renderer.box(
            minX, minY, minZ,
            maxX, maxY, maxZ,
            sideColor,
            lineColor,
            shapeMode.get(),
            0
        );
    }

    private boolean isBurrowed(LivingEntity target) {
        assert mc.world != null;

        if (!mc.world.getBlockState(target.getBlockPos()).isAir()) {
            return true;
        }
        return false;
    }

    public enum modes {
        text,
        shader
    }
}

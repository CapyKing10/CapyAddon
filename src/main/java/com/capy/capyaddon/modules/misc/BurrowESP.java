package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.*;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BurrowESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private PlayerEntity target;


    private final Setting<Integer> scale = sgGeneral.add(new IntSetting.Builder().name("scale").description("scale").defaultValue(4).build());

    public BurrowESP() {
        super(CapyAddon.MISC, "BurrowESP", "straigh outta old oyvey skids");
    }
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder().name("line-color").description("The line color of the target block rendering.").defaultValue(new SettingColor(0, 0, 255, 190)).build());

    private final List<BlockPos> obsidianPos = new ArrayList<>();

    private static final Vector3d pos = new Vector3d();

    @EventHandler
    public void onTick(TickEvent.Post event) {
        // Find the target player
        target = TargetUtils.getPlayerTarget(100, SortPriority.LowestDistance);
    }

    @EventHandler
    public void on2DRender(Render2DEvent event) {
        // Check if there's a valid target and if the target is burrowed
        if (target != null && isBurrowed(target)) {
            // Get the target's position
            Vec3d targetPos = target.getPos();

            // Adjust the position to display text at the feet of the burrowed target
            targetPos = targetPos.add(0, 1, 0);

            // Convert the Vec3d to Vector3d
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

    private boolean isBurrowed(LivingEntity target) {
        assert mc.world != null;

        return !mc.world.getBlockState(target.getBlockPos()).isAir();
    }
}

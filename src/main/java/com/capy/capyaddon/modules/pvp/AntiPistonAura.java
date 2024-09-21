package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import com.capy.capyaddon.utils.cPlaceBreakUtils;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class AntiPistonAura extends Module {
    SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("If you want to rotate for placing.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .description("If you want to swing when placing.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> obsidianRender = sgGeneral.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders an overlay where blocks will be placed.")
        .defaultValue(true)
        .build()
    );
    private final Setting<ShapeMode> obsidianShapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );
    private final Setting<SettingColor> obsidianSideColor = sgGeneral.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the target block rendering.")
        .defaultValue(new SettingColor(0, 255, 0, 60))
        .build()
    );
    private final Setting<SettingColor> obsidianLineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the target block rendering.")
        .defaultValue(new SettingColor(0, 255, 0, 190))
        .build()
    );

    BlockPos placelocation = null;
    private final List<BlockPos> obsidianPos = new ArrayList<>();

    public AntiPistonAura() {
        super(CapyAddon.PVP, "AntiPistonAura", "prevents niggas from being faggots");
    }

    @EventHandler
    public void onPacket(PacketReceivedEvent event) {
        if (mc.player == null || mc.world == null) return;

        placelocation = getCrystalLocation();

        if (placelocation == null) {
            return;
        }

        int obsidianSlot = InvUtils.findInHotbar(Items.OBSIDIAN).slot();
        if (obsidianSlot == -1) {
            return;
        }

        float[] rotation = null;
        if (rotate.get()) {
            rotation = PlayerUtils.calculateAngle(placelocation.toCenterPos());
        }

        if (BlockUtils.canPlace(placelocation)) {
            InvUtils.swap(obsidianSlot, true);
            cPlaceBreakUtils.placeBlock(Hand.MAIN_HAND, placelocation.toCenterPos(), BlockUtils.getDirection(placelocation), placelocation, obsidianSlot, swing.get());
            InvUtils.swapBack();
        }
    }

    private BlockPos getCrystalLocation() {
        List<EndCrystalEntity> crystals = mc.world.getEntitiesByClass(EndCrystalEntity.class, mc.player.getBoundingBox().expand(5), entity -> true);
        for (EndCrystalEntity crystal : crystals) {
            BlockPos crystalPos = crystal.getBlockPos();
            if (crystalPos.getY() == mc.player.getBlockPos().getY() + 1) {
                return crystalPos;
            }
        }
        return null;
    }
    @EventHandler
    private void onRender(Render3DEvent event) {
        if (obsidianRender.get() && placelocation != null) {
            Color sideColor = obsidianSideColor.get();
            Color lineColor = obsidianLineColor.get();

            event.renderer.box(placelocation, sideColor, lineColor, obsidianShapeMode.get(), 0);
        }
    }
}

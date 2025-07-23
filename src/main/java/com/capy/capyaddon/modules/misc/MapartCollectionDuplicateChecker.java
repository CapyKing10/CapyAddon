package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.MapUtils;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class MapartCollectionDuplicateChecker extends Module {
    public final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> overrideColor = sgGeneral.add(new BoolSetting.Builder()
        .name("override-color")
        .description("Override the unique color for each map id.")
        .defaultValue(false)
        .build()
    );

    public final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("Color to use when overriding")
        .defaultValue(new Color(123, 123, 123))
        .visible(overrideColor::get)
        .build()
    );

    public final Setting<Boolean> showTracer = sgGeneral.add(new BoolSetting.Builder()
        .name("render-tracer")
        .description("render a tracer")
        .defaultValue(true)
        .build()
    );

    public final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    public MapartCollectionDuplicateChecker() {
        super(CapyAddon.MISC, "duplicate-checker", "Checks for duplicates in your mapart collection");
    }

    private final Map<Integer, List<ItemFrameEntity>> duplicateMapFrames = new HashMap<>();

    @Override
    public void onDeactivate() {
        duplicateMapFrames.clear();
        super.onDeactivate();
    }

    @Override
    public void onActivate() {
        updateDuplicates();
    }

    private void updateDuplicates() {
        duplicateMapFrames.clear();
        Map<Integer, List<ItemFrameEntity>> mapFrames = new HashMap<>();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof ItemFrameEntity frame)) continue;

            ItemStack itemStack = frame.getHeldItemStack();
            if (itemStack.isEmpty() || itemStack.getItem() != Items.FILLED_MAP) continue;

            int mapId = MapUtils.getMapId(itemStack);
            if (mapId == -1) continue;

            mapFrames.computeIfAbsent(mapId, k -> new ArrayList<>()).add(frame);
        }

        for (Map.Entry<Integer, List<ItemFrameEntity>> entry : mapFrames.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicateMapFrames.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        for (Map.Entry<Integer, List<ItemFrameEntity>> entry : duplicateMapFrames.entrySet()) {
            int mapId = entry.getKey();
            for (ItemFrameEntity entity : entry.getValue()) {
                render(event, entity, mapId);
            }
        }
    }

    @EventHandler
    public void onInteractEntity(InteractEntityEvent event) {
        if (!(event.entity instanceof ItemFrameEntity)) return;

        updateDuplicates();
    }

    @EventHandler
    public void onAttackEntity(AttackEntityEvent event) {
        if (!(event.entity instanceof ItemFrameEntity)) return;

        updateDuplicates();
    }


    private void render(Render3DEvent event, Entity entity, int id) {
        Color colorToUse = overrideColor.get() ? color.get() : MapUtils.getColorFromId(id);

        if (showTracer.get()) event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, entity.getX(), entity.getY(), entity.getZ(), colorToUse.a(255));

        double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
        double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
        double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();

        Box box = entity.getBoundingBox();
        event.renderer.box(
            x + box.minX, y + box.minY, z + box.minZ,
            x + box.maxX, y + box.maxY, z + box.maxZ,
            colorToUse.a(75), colorToUse.a(255),
            shapeMode.get(), 0
        );
    }
}

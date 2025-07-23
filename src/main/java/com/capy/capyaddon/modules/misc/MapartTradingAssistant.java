package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.Base64Utils;
import com.capy.capyaddon.utils.MapUtils;
import com.capy.capyaddon.utils.cLogUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapartTradingAssistant extends Module {
    public final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final SettingGroup sgRender = settings.createGroup("Render");

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Download: get a list of all your maps in base64, Highlight: highlight the maps from base64")
        .defaultValue(Mode.Download)
        .build()
    );

    public final Setting<String> base64string = sgGeneral.add(new StringSetting.Builder()
        .name("base64-string")
        .description("enter the other person's base64 list here")
        .visible(() -> mode.get() == Mode.Highlight)
        .build()
    );

    // Render

    public final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    public final Setting<SettingColor> fillColor = sgRender.add(new ColorSetting.Builder()
        .name("fill-color")
        .description("Color to use")
        .defaultValue(new Color(255, 0, 0, 75))
        .build()
    );

    public final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("Color to use")
        .defaultValue(new Color(255, 0, 0, 255))
        .build()
    );

    public final Setting<Boolean> showTracer = sgRender.add(new BoolSetting.Builder()
        .name("render-tracer")
        .description("render a tracer (uses side color)")
        .defaultValue(true)
        .build()
    );

    public MapartTradingAssistant() {
        super(CapyAddon.MISC, "mapart-trading-assistant", "helps with trading maparts by highlighting maps the other person has already (so you can trade the ones that arent highlighted)");
    }

    Map<Integer, List<ItemFrameEntity>> mapFrames = new HashMap<>();
    List<ItemFrameEntity> toHighlight = new ArrayList<>();


    @Override
    public void onActivate() {
        super.onActivate();

        mapFrames.clear();
        toHighlight.clear();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof ItemFrameEntity frame)) continue;

            ItemStack itemStack = frame.getHeldItemStack();
            if (itemStack.isEmpty() || itemStack.getItem() != Items.FILLED_MAP) continue;

            int mapId = MapUtils.getMapId(itemStack);
            if (mapId == -1) continue;

            mapFrames.computeIfAbsent(mapId, k -> new ArrayList<>()).add(frame);
        }

        if (mode.get() == Mode.Download) {
            String base64EncodedString = Base64Utils.encodeListToBase64(mapFrames.keySet().stream().toList());
            copyToClipboard(base64EncodedString);

            cLogUtils.sendMessage("Copied base64 string to your clipboard.", false);
        }

        if (mode.get() == Mode.Highlight) {

            if (base64string.get().isEmpty()) {
                cLogUtils.sendMessage("Please enter the base64 string you got from the other person", false);
                this.toggle();
                return;
            }

            List<Integer> idList = Base64Utils.decodeBase64ToList(base64string.get());

            for (Integer id : idList) {
                List<ItemFrameEntity> frames = mapFrames.get(id);
                if (frames != null) {
                    toHighlight.addAll(frames);
                }
            }

        }

    }

    @EventHandler
    public void onRender3d(Render3DEvent event) {

        for (ItemFrameEntity entity : toHighlight) {

            if (showTracer.get()) event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, entity.getX(), entity.getY(), entity.getZ(), sideColor.get());

            double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
            double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
            double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();

            Box box = entity.getBoundingBox();
            event.renderer.box(
                x + box.minX, y + box.minY, z + box.minZ,
                x + box.maxX, y + box.maxY, z + box.maxZ,
                fillColor.get(), sideColor.get(),
                shapeMode.get(), 0
            );

        }

    }


    private void copyToClipboard(String string) {
        mc.keyboard.setClipboard(string);
    }

    public enum Mode {
        Download,
        Highlight
    }
}

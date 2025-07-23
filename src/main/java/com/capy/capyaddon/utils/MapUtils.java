package com.capy.capyaddon.utils;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.block.MapColor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MapUtils {

    public static Color getColorFromId(long id) {
        Random random = new Random(id);
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
    }

    public static void saveMapImage(ItemStack itemStack, int mapId) {
        if (mc.world == null || mc.player == null) return;

        MapState mapState = mc.world.getMapState(new MapIdComponent(mapId));
        if (mapState == null) return;

        byte[] colors = mapState.colors;
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                int i = x + y * 128;
                int colorId = colors[i] & 0xFF;
                int color = MapColor.getRenderColor(MapColor.Brightness.values()[colorId >> 6].ordinal());
                int alpha = (color >> 24) & 0xFF;
                int blue = (color >> 16) & 0xFF;
                int green = (color >> 8) & 0xFF;
                int red = color & 0xFF;

                int fixedColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

                image.setRGB(x, y, fixedColor);
            }
        }


        File mapsFolder = new File(mc.runDirectory, "maps");

        if (!mapsFolder.exists()) {
            if (mapsFolder.mkdirs()) {
                CapyAddon.LOG.info("Created maps directory: " + mapsFolder.getAbsolutePath(), false);
            } else {
                CapyAddon.LOG.info("Failed to create maps directory: " + mapsFolder.getAbsolutePath(), false);
            }
        }

        File output = new File(mapsFolder, "map_" + mapId + ".png");

        try {
            ImageIO.write(image, "png", output);
            CapyAddon.LOG.info("Saved map image: " + output.getAbsolutePath(), false);
        } catch (IOException e) {
            CapyAddon.LOG.info("Failed to save map image: " + e.getMessage(), false);
        }
    }

    public static int getMapId(ItemStack itemStack) {
        MapIdComponent mapIdComponent = itemStack.get(DataComponentTypes.MAP_ID);
        return mapIdComponent != null ? mapIdComponent.id() : -1;
    }

}

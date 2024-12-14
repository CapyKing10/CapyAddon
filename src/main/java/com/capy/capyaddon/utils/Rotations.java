package com.capy.capyaddon.utils;

import net.minecraft.util.math.MathHelper;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Rotations {
        public int getYaw4D () {
            return MathHelper.floor((double) (mc.player.getYaw() * 4.0f / 360.0f) + 0.5) & 3;
        }

    public String getDirection4D(boolean northRed) {
        int yaw = getYaw4D();

        if (yaw == 0) {
            return "South (+Z)";
        }
        if (yaw == 1) {
            return "West (-X)";
        }
        if (yaw == 2) {
            return (northRed ? "\u00c2\u00a7c" : "") + "North (-Z)";
        }
        if (yaw == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }
}


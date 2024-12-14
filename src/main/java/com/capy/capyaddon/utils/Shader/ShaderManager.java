package com.capy.capyaddon.utils.Shader;

import meteordevelopment.meteorclient.utils.PreInit;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ShaderManager {
    public static HandOutlineShader HAND_OUTLINE;

    public static boolean rendering;

    @PreInit
    public static void init() {
        HAND_OUTLINE = new HandOutlineShader();
    }

    public static void beginRender() {
        HAND_OUTLINE.beginRender();
    }

    public static void onResized(int width, int height) {
        if (mc == null) return;

        HAND_OUTLINE.onResized(width, height);
    }
}

package com.capy.capyaddon.utils.Shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;

public class ShaderBuffer extends Framebuffer {
    public ShaderBuffer(int width, int height) {
        super(false);
        RenderSystem.assertOnRenderThreadOrInit();
        resize(width, height, true);
        setClearColor(0f, 0f, 0f, 0f);
    }
}

package com.capy.capyaddon.utils.Shader;

import com.capy.capyaddon.modules.misc.HandColor;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShader;
import net.minecraft.entity.Entity;

public class HandOutlineShader extends PostProcessShader {
    private static HandColor handColor;

    public HandOutlineShader() {
        init("outline");
    }

    @Override
    protected boolean shouldDraw() {
        return true;
    }

    @Override
    public boolean shouldDraw(Entity entity) {
        return false;
    }

    @Override
    protected void setUniforms() {
        shader.set("u_Width", handColor.outlineWidth.get());
        shader.set("u_FillOpacity", handColor.fillOpacity.get() / 255.0);
        shader.set("u_ShapeMode", handColor.shapeMode.get().ordinal());
        shader.set("u_GlowMultiplier", handColor.glowMultiplier.get());
    }
}

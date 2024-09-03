package com.capy.capyaddon.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class cRenderUtils {
    BufferAllocator bufferAllocator = new BufferAllocator(2048);

    private final VertexConsumerProvider.Immediate vertex =
        VertexConsumerProvider.immediate(bufferAllocator);


    public void text(String text, MatrixStack stack, float x, float y, int color) {
        mc.textRenderer.draw(text, x, y, color, false, stack.peek().getPositionMatrix(), vertex, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        vertex.draw();
    }
}

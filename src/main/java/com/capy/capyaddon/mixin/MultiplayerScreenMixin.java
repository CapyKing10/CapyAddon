package com.capy.capyaddon.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.capy.capyaddon.modules.ChunkDupeTimer;
import static meteordevelopment.meteorclient.MeteorClient.mc;

/*
    Credits to Eonexe on github
*/

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

    @Unique
    private double time = Modules.get().get(ChunkDupeTimer.class).delay.get() * 20;

    private int unsafeColor;
    private int safeColor;
    private int textColor;

    private String joinTimer;
    private int joinTimerLength;

    public MultiplayerScreenMixin(Text title){super(title);}

    @Inject(method  = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        unsafeColor = Color.fromRGBA(255,0,0,255);
        safeColor = Color.fromRGBA(0, 255, 0, 255);


        joinTimerLength = textRenderer.getWidth(joinTimer);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {

        if (Modules.get().isActive(ChunkDupeTimer.class)) {
            //join timer
            context.drawCenteredTextWithShadow(mc.textRenderer, joinTimer, this.width / 2, 3, textColor);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        if (time <= 0 ) {
            textColor = safeColor;
            joinTimer = "Safe to join!";
        } else {
            textColor = unsafeColor;
            time--;
            joinTimer = "Join in: " + getText();
        }
    }

    @Unique
    private String getText() {
        String timeRemain = String.format("(%.1f)", time/20);
        return timeRemain;
    }
}

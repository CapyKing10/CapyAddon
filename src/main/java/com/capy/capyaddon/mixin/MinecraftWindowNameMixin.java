package com.capy.capyaddon.mixin;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.Settings;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(MinecraftClient.class)
public class MinecraftWindowNameMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        Settings settings = Settings.get();
        if (settings.windowName.get()) mc.getWindow().setTitle("Minecraft*. [" + CapyAddon.NAME + " " + CapyAddon.VERSION + "]");
    }
}

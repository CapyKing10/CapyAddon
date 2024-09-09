package com.capy.capyaddon.mixin;

import com.capy.capyaddon.Settings;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.w3c.dom.Text;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (!Settings.get().cape.get()) return;

        SkinTextures oldTextures = cir.getReturnValue();
        Identifier capeTexture = new Identifier("minecraft", "textures/capes/capy.png");

        SkinTextures Textures = new SkinTextures(
            oldTextures.texture(),
            oldTextures.textureUrl(),
            capeTexture,
            capeTexture,
            oldTextures.model(),
            oldTextures.secure()
        );
        cir.setReturnValue(Textures);
    }
}

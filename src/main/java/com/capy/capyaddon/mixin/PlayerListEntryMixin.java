package com.capy.capyaddon.mixin;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.capy.capyaddon.modules.misc.Cape;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    private static final Identifier CAPY = new Identifier("minecraft", "textures/capes/capy.png");
    private static final Identifier RUSHERKEK = new Identifier("minecraft", "textures/capes/rusherkek.png");
    private static final Identifier METEOR_DONATOR = new Identifier("minecraft", "textures/capes/donator.png");
    private static final Identifier METEOR_MODERATOR = new Identifier("minecraft", "textures/capes/moderator.png");

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (!Cape.getInstance().isActive()) return;

        SkinTextures oldTextures = cir.getReturnValue();
        Identifier capeTexture;

        capeTexture = switch (Cape.getInstance().cape.get()) {
            case CAPY -> CAPY;
            case RUSHERKEK -> RUSHERKEK;
            case METEOR_DONATOR -> METEOR_DONATOR;
            case METEOR_MODERATOR -> METEOR_MODERATOR;
            default -> null;
        };

        if (capeTexture != null) {
            SkinTextures Textures = new SkinTextures (
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
}

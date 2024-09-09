package com.capy.capyaddon.utils.LogoutSpotsPlus;

import com.capy.capyaddon.modules.pvp.LogoutSpotsPlus;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class GhostPlayer extends FakePlayerEntity {
    private final UUID uuid;
    private final LogoutSpotsPlus LSP;

    public GhostPlayer(PlayerEntity player, LogoutSpotsPlus module) {
        super(player, "ghost", 20, false);
        this.uuid = player.getUuid();
        this.LSP = module;
    }

    public boolean render(Render3DEvent event) {
        double scale = 1;
        WireframeEntityRenderer.render(event, this, scale, LSP.sideColor.get(), LSP.lineColor.get(), LSP.shapeMode.get());

        return false;
    }

    public UUID getUuid() {
        return uuid;
    }
}

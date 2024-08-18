package com.capy.capyaddon.utils.LogoutSpotsPlus;

import com.capy.capyaddon.modules.pvp.LogoutSpotsPlus;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class GhostPlayer extends FakePlayerEntity {
    private final UUID uuid;
    private double scale = 1;
    private final LogoutSpotsPlus LSP;

    public GhostPlayer(PlayerEntity player, LogoutSpotsPlus module) {
        super(player, "ghost", 20, false);
        this.uuid = player.getUuid();
        this.LSP = module;  // Pass the correct instance
        System.out.println("ghostplayer");
    }

    public boolean render(Render3DEvent event) {
        System.out.println("ghost render");
        WireframeEntityRenderer.render(event, this, scale, LSP.sideColor.get(), LSP.lineColor.get(), LSP.shapeMode.get());

        // Return true if the ghost should be removed after rendering, false if it should stay
        return false;
    }

    public UUID getUuid() {
        return uuid;
    }
}

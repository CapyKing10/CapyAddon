package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AntiPlayerSwing extends Module {




    @EventHandler
    private void onRenderEntity(Render3DEvent event) {
        float delta = event.tickDelta;
        for(Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity) {
                event.cancel();
            }
        }
    }

    public AntiPlayerSwing() {
        super(CapyAddon.CATEGORY, "anti-player-swing", "Prevents a players limbs from swinging.");
    }
}

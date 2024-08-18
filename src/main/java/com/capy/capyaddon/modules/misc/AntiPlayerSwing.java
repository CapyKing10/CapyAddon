package com.capy.capyaddon.modules.misc;

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
                ((PlayerEntity) entity).limbAnimator.setSpeed(0f);
                ((PlayerEntity) entity).limbAnimator.updateLimbs(0f, delta);
                ((PlayerEntity) entity).limbAnimator.setSpeed(0f);
            }
        }
    }

    public AntiPlayerSwing() {
        super(CapyAddon.MISC, "anti-player-swing", "Prevents a players limbs from swinging.");
    }
}

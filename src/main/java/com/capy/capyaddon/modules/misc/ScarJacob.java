package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.render.HeldItemRendererEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Identifier;

public class ScarJacob extends Module {
    private static final String TARGET_USERNAME = "SpecificUsername";
    private static final Identifier NEW_SKIN = new Identifier("minecraft", "textures/entity/your_custom_skin.png");

    public ScarJacob() {
        super(CapyAddon.MISC, "ScarJacob", "Changes the skin of a specific player.");
    }

    @EventHandler
    public void render3d(Render3DEvent event) {
    }

}

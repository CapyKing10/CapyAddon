package com.capy.capyaddon.utils.LogoutSpotsPlus;

import com.capy.capyaddon.modules.pvp.LogoutSpotsPlus;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3d;

import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Entry {
    private static final Vector3d pos = new Vector3d();

    public final double x, y, z;
    public final double xWidth, zWidth, halfWidth, height;

    public final UUID uuid;
    public final String name;
    public final int health, maxHealth;
    public final String healthText;
    public final PlayerEntity p;

    private final LogoutSpotsPlus LSP;

    public Entry(PlayerEntity entity, LogoutSpotsPlus module) {
        this.p = entity;
        this.LSP = module;  // Pass the correct instance

        halfWidth = entity.getWidth() / 2;
        x = entity.getX() - halfWidth;
        y = entity.getY();
        z = entity.getZ() - halfWidth;

        xWidth = entity.getBoundingBox().getLengthX();
        zWidth = entity.getBoundingBox().getLengthZ();
        height = entity.getBoundingBox().getLengthY();

        uuid = entity.getUuid();
        name = entity.getName().getString();
        health = Math.round(entity.getHealth() + entity.getAbsorptionAmount());
        maxHealth = Math.round(entity.getMaxHealth() + entity.getAbsorptionAmount());

        healthText = " " + health;

        System.out.println("entry");
    }

    public void render3D() {
        if (!LSP.ghosts.stream().anyMatch(g -> g.getUuid().equals(this.uuid))) {
            System.out.println("entry render");
            LSP.ghosts.add(new GhostPlayer(p, LSP));
        }
    }

    @EventHandler
    public void render2D() {
        System.out.println("entry 2d");
        if (!PlayerUtils.isWithinCamera(x, y, z, mc.options.getViewDistance().getValue() * 16)) return;

        TextRenderer text = TextRenderer.get();
        double scale = LSP.scale.get();
        pos.set(x + halfWidth, y + height + 0.5, z + halfWidth);

        if (!NametagUtils.to2D(pos, scale)) return;

        NametagUtils.begin(pos);

        // Compute health things
        double healthPercentage = (double) health / maxHealth;

        // Get health color
        Color healthColor;
        if (healthPercentage <= 0.333) healthColor = Color.RED;
        else if (healthPercentage <= 0.666) healthColor = Color.ORANGE;
        else healthColor = Color.GREEN;

        // Render background
        double i = text.getWidth(name) / 2.0 + text.getWidth(healthText) / 2.0;
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quad(-i, 0, i * 2, text.getHeight(), LSP.nameBackgroundColor.get());
        Renderer2D.COLOR.render(null);

        // Render name and health texts
        text.beginBig();
        double hX = text.render(name, -i, 0, LSP.nameColor.get());
        text.render(healthText, hX, 0, healthColor);
        text.end();

        NametagUtils.end();
    }
}

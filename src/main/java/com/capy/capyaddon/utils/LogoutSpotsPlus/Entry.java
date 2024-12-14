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
import net.minecraft.util.Formatting;
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
    public String distanceText;
    public final PlayerEntity p;

    private final LogoutSpotsPlus LSP;

    public Entry(PlayerEntity entity, LogoutSpotsPlus module) {
        this.p = entity;
        this.LSP = module;

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

        assert mc.player != null;
        healthText = " " + health;
        distanceText = "";
    }

    public void render3D() {
        if (LSP.ghosts.stream().noneMatch(g -> g.getUuid().equals(this.uuid))) {
            LSP.ghosts.add(new GhostPlayer(p, LSP));
        }
    }

    @EventHandler
    public void render2D() {
        if (!PlayerUtils.isWithinCamera(x, y, z, mc.options.getViewDistance().getValue() * 16)) return;

        TextRenderer text = TextRenderer.get();
        double scale = LSP.scale.get();
        pos.set(x + halfWidth, y + height + 0.5, z + halfWidth);
        distanceText = " [" + Math.round(mc.player.distanceTo(p)) + "]";

        if (!NametagUtils.to2D(pos, scale)) return;

        NametagUtils.begin(pos);

        double healthPercentage = (double) health / maxHealth;

        Color healthColor;
        if (healthPercentage <= 0.333) healthColor = Color.RED;
        else if (healthPercentage <= 0.666) healthColor = Color.ORANGE;
        else healthColor = Color.GREEN;

        double i = text.getWidth(name) / 2.0 + text.getWidth(healthText) / 2.0 + (LSP.distance.get() ? text.getWidth(distanceText) / 2.0 : 0);
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quad(-i, 0, i * 2, text.getHeight(), LSP.nameBackgroundColor.get());
        Renderer2D.COLOR.render(null);

        text.beginBig();
        double aX = text.render(name, -i, 0, LSP.nameColor.get());
        double bX = text.render(healthText, aX, 0, healthColor);
        if (LSP.distance.get()) text.render(distanceText, bX, 0, LSP.distanceColor.get());
        text.end();

        NametagUtils.end();
    }
}

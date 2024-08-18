package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.LogoutSpotsPlus.Entry;
import com.capy.capyaddon.utils.LogoutSpotsPlus.GhostPlayer;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class LogoutSpotsPlus extends Module {
    public final List<GhostPlayer> ghosts = new ArrayList<>();
    private final List<Entry> players = new ArrayList<>();
    private final List<PlayerListEntry> lastPlayerList = new ArrayList<>();
    private final List<PlayerEntity> lastPlayers = new ArrayList<>();
    private int timer;
    private Dimension lastDimension;

    private final SettingGroup sgGhost = settings.createGroup("Ghost");
    private final SettingGroup sgRender = settings.createGroup("Render");

    // Ghost

    public final Setting<ShapeMode> shapeMode = sgGhost.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    public final Setting<SettingColor> sideColor = sgGhost.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color.")
        .defaultValue(new SettingColor(255, 255, 255, 25))
        .build()
    );

    public final Setting<SettingColor> lineColor = sgGhost.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color.")
        .defaultValue(new SettingColor(255, 255, 255, 127))
        .build()
    );

    // Render

    public final Setting<SettingColor> nameColor = sgRender.add(new ColorSetting.Builder()
        .name("name-color")
        .description("The name color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );

    public final Setting<SettingColor> nameBackgroundColor = sgRender.add(new ColorSetting.Builder()
        .name("name-background-color")
        .description("The name background color.")
        .defaultValue(new SettingColor(0, 0, 0, 75))
        .build()
    );

    public final Setting<Double> scale = sgRender.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale.")
        .defaultValue(1)
        .min(0)
        .build()
    );

    public LogoutSpotsPlus() {
        super(CapyAddon.PVP, "LogoutSpots+", "a better logout spots module");
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (event.entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.entity;
            System.out.println("player added");

            // Remove the corresponding ghost if the player logs back in
            ghosts.removeIf(ghost -> ghost.getUuid().equals(player.getUuid()));

            int toRemove = -1;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).uuid.equals(player.getUuid())) {
                    toRemove = i;
                    break;
                }
            }

            if (toRemove != -1) {
                players.remove(toRemove);
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Convert the Collection to a List
        List<PlayerListEntry> currentPlayerList = new ArrayList<>(mc.getNetworkHandler().getPlayerList());

        // Detect player logout by comparing last and current player lists
        for (PlayerListEntry lastEntry : lastPlayerList) {
            boolean stillOnline = currentPlayerList.stream()
                .anyMatch(currentEntry -> currentEntry.getProfile().getId().equals(lastEntry.getProfile().getId()));

            if (!stillOnline) {
                // Player logged out
                for (PlayerEntity player : lastPlayers) {
                    if (player.getUuid().equals(lastEntry.getProfile().getId())) {
                        System.out.println("player logged out: " + player.getName().getString());
                        add(new Entry(player, this));
                    }
                }
            }
        }

        // Update the last player list to the current one
        lastPlayerList.clear();
        lastPlayerList.addAll(currentPlayerList);
        updateLastPlayers();

        // Timer logic for periodic updates
        if (timer <= 0) {
            updateLastPlayers();
            timer = 10;
        } else {
            timer--;
        }

        // Handle dimension changes
        Dimension currentDimension = PlayerUtils.getDimension();
        if (currentDimension != lastDimension) {
            players.clear();
        }
        lastDimension = currentDimension;
    }


    @Override
    public void onDeactivate() {
        synchronized (ghosts) {
            ghosts.clear();
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        for (Entry entry : players) {
            entry.render3D();
        }
        synchronized (ghosts) {
            ghosts.removeIf(ghostPlayer -> ghostPlayer.render(event));
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        for (Entry entry : players) {
            entry.render2D();
        }
    }

    private void add(Entry entry) {
        players.removeIf(player -> player.uuid.equals(entry.uuid));
        players.add(entry);
    }

    private void updateLastPlayers() {
        lastPlayers.clear();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity) lastPlayers.add((PlayerEntity) entity);
        }
    }
}

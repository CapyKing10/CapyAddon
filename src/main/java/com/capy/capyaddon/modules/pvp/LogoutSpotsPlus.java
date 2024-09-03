package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.cLogUtils;
import com.capy.capyaddon.utils.LogoutSpotsPlus.Entry;
import com.capy.capyaddon.utils.LogoutSpotsPlus.GhostPlayer;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import com.capy.capyaddon.utils.cPlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class LogoutSpotsPlus extends Module {
    public final cPlayerUtils cPlayerUtils = new cPlayerUtils();
    public final List<GhostPlayer> ghosts = new ArrayList<>();
    private final List<Entry> players = new ArrayList<>();
    private final List<PlayerListEntry> lastPlayerList = new ArrayList<>();
    private final List<PlayerEntity> lastPlayers = new ArrayList<>();
    private int timer;
    private Dimension lastDimension;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgGhost = settings.createGroup("Ghost");
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgMisc = settings.createGroup("Misc");
    private final SettingGroup sgCompatibility = settings.createGroup("Compatibility");

    // General

    public final Setting<Boolean> logToChat = sgGeneral.add(new BoolSetting.Builder()
        .name("log-to-chat")
        .description("log to chat whenever someone leave or re-joins")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> prefix = sgGeneral.add(new BoolSetting.Builder()
        .name("exclamation-mark-prefix")
        .description("ignore yourself if you log out")
        .defaultValue(true)
        .visible(logToChat::get)
        .build()
    );

    public final Setting<Boolean> stackMessages = sgGeneral.add(new BoolSetting.Builder()
        .name("stack-messages")
        .description("Stack the messages so it doesn't spam your chat")
        .defaultValue(true)
        .visible(logToChat::get)
        .build()
    );

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

    public final Setting<Boolean> distance = sgRender.add(new BoolSetting.Builder()
        .name("distance")
        .description("add a distance thingy on the nametag")
        .defaultValue(true)
        .build()
    );

    public final Setting<SettingColor> distanceColor = sgRender.add(new ColorSetting.Builder()
        .name("distance-color")
        .description("color of the distance on the nametag")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(distance::get)
        .build()
    );

    public final Setting<Double> scale = sgRender.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale.")
        .defaultValue(1)
        .min(0)
        .build()
    );

    // Misc

    public final Setting<Boolean> autoEzLog = sgMisc.add(new BoolSetting.Builder()
        .name("auto-ez-log")
        .description("send a message in chat saying ez log when someone logs out")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> autoEzLogIgnoreNakeds = sgMisc.add(new BoolSetting.Builder()
        .name("ignore-nakeds")
        .description("ignore naked people for the auto-ez-log setting")
        .defaultValue(true)
        .visible(autoEzLog::get)
        .build()
    );

    public final Setting<String> autoEzLogString = sgMisc.add(new StringSetting.Builder()
        .name("string")
        .description("message to send")
        .description("EZZZZ LOG")
        .build()
    );

    // Compatibility

    public final Setting<Boolean> popCounter = sgCompatibility.add(new BoolSetting.Builder()
        .name("pop-counter")
        .description("use compatibility with popcounter")
        .defaultValue(false)
        .build()
    );

    public LogoutSpotsPlus() {
        super(CapyAddon.PVP, "LogoutSpots+", "a better logout spots module");
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (event.entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.entity;

            ghosts.removeIf(ghost -> ghost.getUuid().equals(player.getUuid()));

            int toRemove = -1;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).uuid.equals(player.getUuid())) {
                    toRemove = i;
                    break;
                }
            }

            if (toRemove != -1) {
                String suffix = (prefix.get() ? Formatting.DARK_RED + "[" + Formatting.RED + "!" + Formatting.DARK_RED + "]" + Formatting.RESET + " " : "");
                if (logToChat.get()) cLogUtils.sendMessage(suffix + player.getName().getString() + " logged back in. " + Formatting.GRAY + "[" + Formatting.GOLD + mc.player.distanceTo(player) + Formatting.GRAY + "]", stackMessages.get());
                players.remove(toRemove);
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        List<PlayerListEntry> currentPlayerList = new ArrayList<>(mc.getNetworkHandler().getPlayerList());

        for (PlayerListEntry lastEntry : lastPlayerList) {
            boolean stillOnline = currentPlayerList.stream()
                .anyMatch(currentEntry -> currentEntry.getProfile().getId().equals(lastEntry.getProfile().getId()));

            if (!stillOnline) {
                for (PlayerEntity player : lastPlayers) {
                    if (player.getUuid().equals(lastEntry.getProfile().getId())) {
                        String suffix = (prefix.get() ? Formatting.DARK_RED + "[" + Formatting.RED + "!" + Formatting.DARK_RED + "]" + Formatting.RESET + " " : "");
                        if (logToChat.get()) cLogUtils.sendMessage(suffix + player.getName().getString() + " logged out. " + Formatting.GRAY + "[" + Formatting.GOLD + mc.player.distanceTo(player) + Formatting.GRAY + "]", stackMessages.get());

                        if (popCounter.get()) {
                            PopCounter popCounter1 = new PopCounter();
                            synchronized (popCounter1.totemPopMap) {
                                int pops = popCounter1.getPops(player.getUuid());
                                if (logToChat.get()) cLogUtils.sendMessage(suffix + player.getName().getString() + " logged out after popping " + Formatting.GOLD + Formatting.BOLD + pops + Formatting.RESET + (pops == 1 ? " totem" : " totems"), stackMessages.get());
                            }
                        }

                        if (autoEzLog.get()) {
                            if (autoEzLogIgnoreNakeds.get() && cPlayerUtils.isNaked(player)) return;
                            ChatUtils.sendPlayerMsg(autoEzLogString.get());
                        }

                        addEntry(new Entry(player, this));
                    }
                }
            }
        }

        lastPlayerList.clear();
        lastPlayerList.addAll(currentPlayerList);
        updateLastPlayers();

        if (timer <= 0) {
            updateLastPlayers();
            timer = 10;
        } else {
            timer--;
        }

        Dimension currentDimension = PlayerUtils.getDimension();
        if (currentDimension != lastDimension) {
            players.clear();
        }
        lastDimension = currentDimension;
    }

    @EventHandler
    private void onTick2(TickEvent.Post event) {
        List<PlayerEntity> currentPlayers = new ArrayList<>(mc.world.getPlayers());

        for (PlayerEntity player : currentPlayers) {
            GhostPlayer ghostPlayer = new GhostPlayer(player, this);

            if (ghosts.contains(ghostPlayer)) {
                if (player.deathTime > 0 || player.getHealth() <= 0) {
                    ghosts.removeIf(ghost -> ghost.getUuid().equals(player.getUuid()));

                    players.removeIf(p -> p.uuid.equals(player.getUuid()));

                }
            }
        }

        if (timer <= 0) {
            updateLastPlayers();
            timer = 10;
        } else {
            timer--;
        }

        Dimension currentDimension = PlayerUtils.getDimension();
        if (currentDimension != lastDimension) {
            players.clear();
        }
        lastDimension = currentDimension;
    }

    @Override
    public void onDeactivate() {
        synchronized (ghosts) { ghosts.clear(); }
        synchronized (players) { players.clear(); }
        synchronized (lastPlayers) { lastPlayers.clear(); }
        synchronized (lastPlayerList) { lastPlayerList.clear(); }
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

    private void addEntry(Entry entry) {
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

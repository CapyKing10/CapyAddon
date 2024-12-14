package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.Hole.HoleUtils;
import com.capy.capyaddon.utils.cPlayerUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Burrow;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class AutoBurrow extends Module {
    private SettingGroup sgGeneral = settings.getDefaultGroup();
    private SettingGroup sgBurrow = settings.createGroup("Burrow");
    private SettingGroup sgDetection = settings.createGroup("Detection");

    // general
    public final Setting<Modes> mode = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("mode")
        .description("Replace: automatically replaces your burrow block if it gets mined, Detection: detect a player in a specific range to then burrow.")
        .defaultValue(Modes.Replace)
        .build()
    );

    // detection
    private final Setting<Double> range = sgDetection.add(new DoubleSetting.Builder()
        .name("range")
        .description("how far the module should detect players and burrow")
        .defaultValue(5)
        .sliderMin(0)
        .sliderMax(15)
        .visible(() -> mode.get() == Modes.Detection || mode.get() == Modes.Detection)
        .build()
    );

    private final Setting<Boolean> higherYLevel = sgDetection.add(new BoolSetting.Builder()
        .name("higher-y-lever")
        .description("will only burrow if the other player has a higher y level")
        .defaultValue(true)
        .visible(() -> mode.get() == Modes.Detection || mode.get() == Modes.Detection)
        .build()
    );

    // burrow
    public final Setting<BurrowModes> burrowMode = sgBurrow.add(new EnumSetting.Builder<BurrowModes>()
        .name("burrow-mode")
        .description("How the module should burrow.")
        .defaultValue(BurrowModes.CapyAddon)
        .build()
    );

    private final Setting<Boolean> onlyInHole = sgBurrow.add(new BoolSetting.Builder()
        .name("only-in-holes")
        .description("wont burrow if you're not in a hole")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> center = sgBurrow.add(new BoolSetting.Builder()
        .name("center")
        .description("Centers you to the middle of the block before burrowing.")
        .defaultValue(true)
        .visible(() -> burrowMode.get() == BurrowModes.CapyAddon)
        .build()
    );

    private final Setting<Boolean> rotate = sgBurrow.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Faces the block you place server-side.")
        .defaultValue(true)
        .visible(() -> burrowMode.get() == BurrowModes.CapyAddon)
        .build()
    );

    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private List<PlayerEntity> players = new ArrayList<>();

    public AutoBurrow() {
        super(CapyAddon.PVP, "auto-burrow", "automatically burrows");
    }

    @Override
    public void onActivate() {
        players.clear();
    }

    public void onTick(TickEvent.Pre event) {
        if (mode.get() == Modes.Replace || mode.get() == Modes.Both) {
            if (!cPlayerUtils.isBurrowed(mc.player) && (HoleUtils.inHole(mc.player) && onlyInHole.get())) {
                burrow();
            }
        }

        if (mode.get() == Modes.Detection || mode.get() == Modes.Both) {
            players.clear();
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player.distanceTo(mc.player) <= range.get()) {
                    if (player.distanceTo(mc.player) <= range.get() &&
                        (!higherYLevel.get() || player.getY() > mc.player.getY())) {
                        players.add(player);
                    }
                }
            }
            burrow();
        }
    }

    private void burrow() {
        if (players.isEmpty()) return;
        if (onlyInHole.get() && !HoleUtils.inHole(mc.player)) return;
        switch (burrowMode.get()) {
            case Meteor -> MeteorBurrow();
            case Blackout -> BlackoutBurrow();
            case CapyAddon -> CapyAddonBurrow();
        }
        players.clear();
    }

    private void MeteorBurrow() {
        Burrow burrow = Modules.get().get(Burrow.class);
        if (!burrow.isActive()) {
            burrow.toggle();
            burrow.sendToggledMsg();
        }
    }

    private void BlackoutBurrow() {
        Module burrow = Modules.get().get("Burrow+");
        if (burrow == null) {
            error("Blackout isn't present...");
            this.toggle();
            return;
        }
        burrow.toggle();
    }

    private void CapyAddonBurrow() {
        Rotations.rotate(Rotations.getYaw(mc.player.getBlockPos()), Rotations.getPitch(mc.player.getBlockPos()), 50);

        blockPos.set(mc.player.getBlockPos());

        if (center.get()) PlayerUtils.centerPlayer();

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.4, mc.player.getZ(), false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.75, mc.player.getZ(), false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.01, mc.player.getZ(), false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.15, mc.player.getZ(), false));

        FindItemResult block = InvUtils.findInHotbar(Items.OBSIDIAN);
        if (!block.found())  {
            error("no obsidian");
            return;
        }
        InvUtils.swap(block.slot(), true);

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Utils.vec3d(blockPos), Direction.UP, blockPos, false));
        mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

        InvUtils.swapBack();

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 12, mc.player.getZ(), false));
    }

    public enum Modes {
        Replace,
        Detection,
        Both
    }

    public enum BurrowModes {
        Meteor,
        Blackout,
        CapyAddon
    }
}

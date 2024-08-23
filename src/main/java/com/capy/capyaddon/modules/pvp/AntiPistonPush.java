package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.Hole.Hole;
import com.capy.capyaddon.utils.Hole.HoleUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import com.capy.capyaddon.utils.PlaceBreakUtils;

/*

    Ported from https://github.com/Mint-Dev/Mint-Boze-Addon/
    Full credit to them

 */

public class AntiPistonPush extends Module {
    SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<modes> mode = sgGeneral.add(new EnumSetting.Builder<modes>()
        .name("mode")
        .description("In what way to prevent getting pushed.")
        .defaultValue(modes.Block)
        .build()
    );

    public final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("If you want to rotate for placing.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .description("If you want to swing when playing.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> waitpower = sgGeneral.add(new BoolSetting.Builder()
        .name("wait-piston-activation")
        .description("Wait for the piston to recieve redstone power.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> onlyinhole = sgGeneral.add(new BoolSetting.Builder()
        .name("only-in-hole")
        .description("Only prevent pushes, when in 1x1 hole.")
        .defaultValue(true)
        .build()
    );

    BlockPos placelocation = null;

    public AntiPistonPush() {
        super(CapyAddon.PVP, "AntiPistonPush", "Prevent getting pushed by pistons.");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        BlockPos pos = mc.player.getBlockPos();
        Hole hole = HoleUtils.getHole(pos, true,true, true, 3, true);

        if (!inHole(hole) && onlyinhole.get()) {
            return;
        }

        if (mode.get() == modes.Block) {
            int bestslot;
            bestslot = InvUtils.findInHotbar(Items.OBSIDIAN).slot();
            placelocation = null;
            placelocation = getplacelocation();

            if (placelocation == null || bestslot == -1) {
                return;
            }

            float[] rotation = null;
            if (rotate.get()) {
                rotation = PlayerUtils.calculateAngle(placelocation.toCenterPos());
            }

            if (BlockUtils.canPlace(placelocation) && mc.player.getPose() == EntityPose.STANDING) {
                InvUtils.swap(bestslot, true);
                PlaceBreakUtils.placeBlock(Hand.MAIN_HAND, placelocation.toCenterPos(), BlockUtils.getDirection(placelocation), placelocation, bestslot, swing.get());
                InvUtils.swapBack();
            }
        }
    }

    public BlockPos getplacelocation() {
        BlockPos playerheadpos = mc.player.getBlockPos().up();

        if(isdangerouspiston(playerheadpos.north(), Direction.SOUTH) && !isSolidBlock(playerheadpos.south())) {
            return playerheadpos.south();
        } else if(isdangerouspiston(playerheadpos.east(), Direction.WEST) && !isSolidBlock(playerheadpos.west())) {
            return playerheadpos.west();
        } else if(isdangerouspiston(playerheadpos.south(), Direction.NORTH) && !isSolidBlock(playerheadpos.north())) {
            return playerheadpos.north();
        } else if(isdangerouspiston(playerheadpos.west(), Direction.EAST) && !isSolidBlock(playerheadpos.east())) {
            return playerheadpos.east();
        }
        return null;
    }

    public boolean detectpiston() {

        if(!(mc.player.getPose() == EntityPose.STANDING)) {
            return false;
        }

        BlockPos playerheadpos = mc.player.getBlockPos().up();
        if(isdangerouspiston(playerheadpos.north(), Direction.SOUTH)
            || isdangerouspiston(playerheadpos.east(), Direction.WEST)
            || isdangerouspiston(playerheadpos.south(), Direction.NORTH)
            || isdangerouspiston(playerheadpos.west(), Direction.EAST)) {
            return true;
        }
        return false;
    }

    public boolean isdangerouspiston(BlockPos pos, Direction direction) {
        BlockState state = mc.world.getBlockState(pos);

        if(state.getBlock() == Blocks.PISTON || state.getBlock() == Blocks.STICKY_PISTON || state.getBlock() == Blocks.MOVING_PISTON) {
            if(state.get(Properties.FACING) == direction) {
                if(waitpower.get() && !pistongettingpower(pos)) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public BlockPos getPistonPos() {
        BlockPos playerHeadPos = mc.player.getBlockPos().up();

        if (isPiston(playerHeadPos.north(), Direction.SOUTH)) {
            return playerHeadPos.north();
        } else if (isPiston(playerHeadPos.east(), Direction.WEST)) {
            return playerHeadPos.east();
        } else if (isPiston(playerHeadPos.south(), Direction.NORTH)) {
            return playerHeadPos.south();
        } else if (isPiston(playerHeadPos.west(), Direction.EAST)) {
            return playerHeadPos.west();
        }

        return null;
    }

    private boolean isPiston(BlockPos pos, Direction direction) {
        BlockState state = mc.world.getBlockState(pos);

        if (state.getBlock() == Blocks.PISTON || state.getBlock() == Blocks.STICKY_PISTON || state.getBlock() == Blocks.MOVING_PISTON) {
            return state.get(Properties.FACING) == direction;
        }

        return false;
    }

    private boolean isPowerSource(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.emitsRedstonePower();
    }

    private boolean pistongettingpower(BlockPos pos) {
        return isPowerSource(pos.north()) || isPowerSource(pos.south()) ||
            isPowerSource(pos.east()) || isPowerSource(pos.west()) ||
            isPowerSource(pos.down());
    }

    private boolean inHole(Hole hole) {
        for (BlockPos pos : hole.positions) {
            if (mc.player.getBlockPos().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSolidBlock(BlockPos pos) {
        BlockState blockState = mc.world.getBlockState(pos);
        return blockState.isSolidBlock(mc.world, pos);
    }

    private enum modes {
        Block,
    }
}

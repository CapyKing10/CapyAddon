package com.capy.capyaddon.utils;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.canBreak;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.getDirection;

public class PlaceBreakUtils {
    public static boolean breaking;
    private static boolean breakingThisTick;


    public static void placeBlock(Hand hand, Vec3d blockHitVec, Direction blockDirection, BlockPos pos, Integer slot, Boolean swing) {
        Vec3d eyes = mc.player.getEyePos();
        boolean inside =
            eyes.x > pos.getX() && eyes.x < pos.getX() + 1 &&
                eyes.y > pos.getY() && eyes.y < pos.getY() + 1 &&
                eyes.z > pos.getZ() && eyes.z < pos.getZ() + 1;

        PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(
            hand,
            new BlockHitResult(blockHitVec, blockDirection, pos, inside),
            slot
        );

        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(packet);
            if (swing) mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }
    }
}

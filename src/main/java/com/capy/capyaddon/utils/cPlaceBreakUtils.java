package com.capy.capyaddon.utils;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class cPlaceBreakUtils {

    public static void placeBlock(Hand hand, Vec3d blockHitVec, Direction blockDirection, BlockPos pos, Integer slot, Boolean swing) {
        if (mc.player == null || mc.interactionManager == null || mc.getNetworkHandler() == null) return;

        Vec3d eyes = mc.player.getEyePos();
        boolean inside =
            eyes.x > pos.getX() && eyes.x < pos.getX() + 1 &&
                eyes.y > pos.getY() && eyes.y < pos.getY() + 1 &&
                eyes.z > pos.getZ() && eyes.z < pos.getZ() + 1;

        boolean wasSneaking = mc.player.input.sneaking;
        mc.player.input.sneaking = false;

        BlockHitResult blockHitResult = new BlockHitResult(blockHitVec, blockDirection, pos, inside);
        PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(hand, blockHitResult, slot);

        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(packet);

            if (swing) {
                mc.player.swingHand(Hand.MAIN_HAND);
            } else {
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }
        }

        mc.player.input.sneaking = wasSneaking;
    }

}

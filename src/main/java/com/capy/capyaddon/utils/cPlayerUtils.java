package com.capy.capyaddon.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class cPlayerUtils {
    public static boolean isNaked(PlayerEntity player) {
        return player.getOffHandStack().isEmpty()
            && player.getMainHandStack().isEmpty()
            && player.getInventory().armor.get(0).isEmpty()
            && player.getInventory().armor.get(1).isEmpty()
            && player.getInventory().armor.get(2).isEmpty()
            && player.getInventory().armor.get(3).isEmpty();
    }

    public static boolean isBurrowed(LivingEntity target) {
        assert mc.world != null;

        if (!mc.world.getBlockState(target.getBlockPos()).isAir()) {
            return true;
        }
        return false;
    }
}

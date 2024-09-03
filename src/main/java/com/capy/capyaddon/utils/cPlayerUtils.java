package com.capy.capyaddon.utils;

import net.minecraft.entity.player.PlayerEntity;

public class cPlayerUtils {
    public boolean isNaked(PlayerEntity player) {
        return player.getOffHandStack().isEmpty()
            && player.getMainHandStack().isEmpty()
            && player.getInventory().armor.get(0).isEmpty()
            && player.getInventory().armor.get(1).isEmpty()
            && player.getInventory().armor.get(2).isEmpty()
            && player.getInventory().armor.get(3).isEmpty();
    }
}

package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ShulkerDrop extends Module {
    public ShulkerDrop() {
        super(CapyAddon.MISC, "ShulkerDrop", "a module that drops all shulkers in the inventory on the ground");
    }

    @Override
    public void onActivate() {
        Inventory inventory = MinecraftClient.getInstance().player.getInventory();
        PlayerEntity player = MinecraftClient.getInstance().player;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (isShulker(itemStack)) {
                InvUtils.drop().slot(i);
            }
        }
        this.toggle();
    }

    private boolean isShulker(ItemStack itemStack) {
        String translationKey = itemStack.getTranslationKey();
        return translationKey.contains("shulker_box");
    }
}

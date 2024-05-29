package com.capy.capyaddon.screen;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RouletteScreen extends GenericContainerScreen {
    public RouletteScreen() {
        super(GenericContainerScreenHandler.createGeneric9x6(-69, new PlayerInventory(mc.player)), new PlayerInventory(mc.player), ((MutableText) Text.of("Roulette")).formatted(Formatting.UNDERLINE, Formatting.BLACK));
        updateMenuSlots();
    }

    public void generate() {

    }

    private void updateMenuSlots() {
        for (MenuSlot menuSlot : MenuSlot.values())
            handler.getSlot(menuSlot.slotId).setStack(menuSlot.itemStack);
    }

    private enum MenuSlot {
        BET(Items.LIME_STAINED_GLASS_PANE,45, "BET", Formatting.GOLD);

        public final ItemStack itemStack;
        public final int slotId;
        public final MutableText name;

        MenuSlot(Item item, int slotId, String name, Formatting... formattings) {
            this.slotId = slotId+81;
            this.name = ((MutableText) Text.of(name)).formatted(formattings);
            itemStack = item.getDefaultStack().setCustomName(this.name);
        }
    }
}

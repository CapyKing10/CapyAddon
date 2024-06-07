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
    }
}

package com.capy.capyaddon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LogUtils {
    public static void sendMessage(String message) {
        String prefix = Formatting.GRAY + "[" + Formatting.GOLD + "Capy" + Formatting.YELLOW + "Addon" + Formatting.GRAY + "]";
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(prefix + " " + message));
    }
}

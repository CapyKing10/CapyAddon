package com.capy.capyaddon.utils;

import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static meteordevelopment.meteorclient.MeteorClient.mc;
public class LogUtils {

    public static void sendMessage(String msg, Boolean stack) {
        if (mc.world == null) return;

        MutableText message = Text.empty();
        message.append(getPrefix());
        message.append(" ");
        message.append(msg);

        int id;
        if (stack) {
            id = 93;
        } else {
            id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE); // unique id
        }

        if (!Config.get().deleteChatFeedback.get()) id = 0;

        ((IChatHud) mc.inGameHud.getChatHud()).meteor$add(message, id);
    }

    public static void sendRawMessage(String msg) {
        if (mc.world == null) return;
        mc.inGameHud.getChatHud().addMessage(Text.of(msg));
    }

    public static void sendNotification(String message) {
        mc.getToastManager().add(new MeteorToast(Items.BROWN_STAINED_GLASS_PANE, message, message, 1000));
    }

    public static Text getPrefix() {
        MutableText name1 = Text.literal("Capy");
        MutableText name2 = Text.literal("Addon");
        MutableText prefix = Text.literal("");
        name1.setStyle(name1.getStyle().withFormatting(Formatting.GOLD));
        name2.setStyle(name2.getStyle().withFormatting(Formatting.YELLOW));
        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY))
            .append(Text.literal("["))
            .append(name1)
            .append(name2)
            .append(Text.literal("]"));
        return prefix;
    }

    public static String getStringPrefix() {
        return Formatting.GRAY + "[" + Formatting.GOLD + "Capy" + Formatting.YELLOW + "Addon" + Formatting.GRAY + "]" + Formatting.RESET;
    }
}

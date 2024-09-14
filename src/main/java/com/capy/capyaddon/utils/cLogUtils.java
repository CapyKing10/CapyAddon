package com.capy.capyaddon.utils;

import com.capy.capyaddon.Settings;
import com.capy.capyaddon.modules.pvp.PopCounter;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;
public class cLogUtils {

    public static Settings settings = Settings.get();

    public static void sendMessage(String msg, Boolean stack) {
        if (mc.world == null) return;

        MutableText message = Text.empty();
        message.append(getPrefix());
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

    public static void sendTotemPopMessage(int pops, PlayerEntity player, Boolean stack, Boolean playerStack) {
        if (mc.world == null) return;

        MutableText message = Text.empty();
        message.append(getPrefix());
        message.append(player.getName().getString() + " popped " + Formatting.GOLD + Formatting.BOLD + pops + Formatting.RESET + (pops == 1 ? " totem" : " totems"));

        int id;
        if (stack) {
            if (playerStack) {
                id = player.getId();
            } else {
                id = 103;
            }
        } else {
            id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
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
        MutableText prefix = Text.literal("");
        if (settings.useThemeColoForPrefix.get()) {
            SettingColor cS = settings.themeColor.get();
            SettingColor bC = settings.bracketsColor.get();
            MutableText name = Text.literal("CapyAddon");
            int textColor = (cS.r << 16) | (cS.g << 8) | cS.b;
            int bracketColor = (bC.r << 16) | (bC.g << 8) | bC.b;
            name.setStyle(name.getStyle().withColor(TextColor.fromRgb(textColor)));
            prefix.setStyle((settings.useBracketsColor.get() ? prefix.getStyle().withColor(TextColor.fromRgb(bracketColor)) : prefix.getStyle().withFormatting(Formatting.WHITE)))
                .append(Text.literal("["))
                .append(name)
                .append(Text.literal("] "));
        } else {
            MutableText name1 = Text.literal("Capy");
            MutableText name2 = Text.literal("Addon");
            name1.setStyle(name1.getStyle().withFormatting(Formatting.GOLD));
            name2.setStyle(name2.getStyle().withFormatting(Formatting.YELLOW));
            prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY))
                .append(Text.literal("["))
                .append(name1)
                .append(name2)
                .append(Text.literal("] "));
        }
        return prefix;
    }

    static String rgbaToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public static String getStringPrefix() {
        return Formatting.GRAY + "[" + Formatting.GOLD + "Capy" + Formatting.YELLOW + "Addon" + Formatting.GRAY + "]" + Formatting.RESET;
    }
}

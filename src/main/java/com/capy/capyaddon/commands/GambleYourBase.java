package com.capy.capyaddon.commands;

import com.capy.capyaddon.utils.LogUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class GambleYourBase extends Command {
    public GambleYourBase() {
        super("gamble-your-base", "50/50 chance that your base coordinates get leaked!");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (Math.random() > 0.5) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                String message = "my coordinates are X: " + Math.round(player.getX()) + " Y: " + Math.round(player.getY()) + " Z: " + Math.round(player.getZ()) + " in dimension: " + player.getWorld().getRegistryKey().getValue();
                ChatUtils.sendPlayerMsg(message);
                LogUtils.sendMessage(Formatting.WHITE + "your coordinates got leaked :(", true);
            } else {
                LogUtils.sendMessage(Formatting.WHITE + "hooray, your coordinates are safe.", true);
            }
            return SINGLE_SUCCESS;
        });
    }
}

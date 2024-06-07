package com.capy.capyaddon.commands;

import com.capy.capyaddon.utils.LogUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Discord extends Command {
    public Discord() {
        super("Discord", "Join the Discord!");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            LogUtils.sendMessage(Formatting.WHITE + "Join the Discord at " + Formatting.GOLD + "dsc.gg/capyking10");
            return SINGLE_SUCCESS;
        });
    }
}

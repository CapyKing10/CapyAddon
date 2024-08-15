package com.capy.capyaddon.commands;

import com.capy.capyaddon.utils.LogUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.Format;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class stats extends Command {
    public stats() {
        super("stats", "get someone's joindate and playtime");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            LogUtils.sendMessage("Please provide a username to get the stats of.");
            return SINGLE_SUCCESS;
        });

        builder.then(argument("username", StringArgumentType.word()).executes(context -> {
            String argument = StringArgumentType.getString(context, "username");
            String message = argument + "'s stats: joindate = " + getJoinDate(argument) + ", playtime = " + getPlayTime(argument) + "!";
            info(Formatting.BOLD + message);
            return SINGLE_SUCCESS;
        }));
    }

    public static String getJoinDate(String playerName) {
        String url = "https://www.6b6t.org/en/stats/" + playerName;

        try {
            Document doc = Jsoup.connect(url).get();

            Elements table = doc.select("table");

            Elements rows = table.select("tr");

            for (Element row : rows) {
                Elements cells = row.select("td");
                if (cells.size() >= 2) {
                    String header = cells.get(0).text().trim();
                    String value = cells.get(1).text().trim();

                    if (header.equals("First Join")) {
                        return value;
                    }
                }
            }

            return "N/A";
        } catch (Exception e) {
            System.err.println("Error fetching player stats: " + e.getMessage());
            return "Error";
        }
    }


    public static String getPlayTime(String playerName) {
        String url = "https://www.6b6t.org/en/stats/" + playerName;

        try {
            Document doc = Jsoup.connect(url).get();

            Elements table = doc.select("table");

            Elements rows = table.select("tr");

            for (Element row : rows) {
                Elements cells = row.select("td");
                if (cells.size() >= 2) {
                    String header = cells.get(0).text().trim();
                    String value = cells.get(1).text().trim();

                    if (header.equals("Play Time (total)")) {
                        return value;
                    }
                }
            }

            return "N/A";
        } catch (Exception e) {
            System.err.println("Error fetching player stats: " + e.getMessage());
            return "Error";
        }
    }
}

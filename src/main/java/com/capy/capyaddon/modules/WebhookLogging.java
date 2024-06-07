package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.DiscordWebhook;
import com.capy.capyaddon.utils.LogUtils;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.io.IOException;

public class WebhookLogging extends Module {

    private DiscordWebhook hook;

    public SettingGroup sgGeneral = settings.getDefaultGroup();
    public SettingGroup sgOptions = settings.createGroup("Options");

    public WebhookLogging() {
        super(CapyAddon.CATEGORY, "WebhookLogger", "Log certaint things to a Discord webhook");
    }

    private final Setting<String> url = sgGeneral.add(new StringSetting.Builder()
        .name("webhookURL")
        .description("URL of the webhook to send messages to")
        .defaultValue("")
        .build()
    );

    private final Setting<Boolean> logChats = sgOptions.add(new BoolSetting.Builder()
        .name("Chat Messages")
        .description("log chat messages")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> logDeaths = sgOptions.add(new BoolSetting.Builder()
        .name("Death")
        .description("log to the webhook when you died")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> logRenderDistance = sgOptions.add(new BoolSetting.Builder()
        .name("Player Enter Render Distance")
        .description("the name")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> shouldIgnoreSelf = sgOptions.add(new BoolSetting.Builder()
        .name("Ignore Self")
        .description("ignore yourself upon rendering a player")
        .visible(logRenderDistance::get)
        .defaultValue(true)
        .build()
    );



    @Override
    public void onActivate() {
        String webhookURL = url.get();
        if (!webhookURL.isEmpty()) {
            hook = new DiscordWebhook(webhookURL);
        } else {
            LogUtils.sendMessage(Formatting.WHITE + "Invalid webhook URL...");
            this.toggle();
        }
    }

    @EventHandler
    private void onMessageRecieved(ReceiveMessageEvent event) throws IOException {
        if (logChats.get()) {
            assert mc.player != null;
            if (hook == null) return;

            String message = event.getMessage().getString();
            hook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(message)
                .setColor(Color.ORANGE)
            );
            hook.execute();
            hook.clearEmbeds();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onOpenScreen(OpenScreenEvent event) throws IOException {
        if (logDeaths.get()) {
            assert mc.player != null;
            if (hook == null) return;

            if (event.screen instanceof DeathScreen) {
                BlockPos deathPos = mc.player.getBlockPos();
                String pos = "x: " + deathPos.getX() + " y: " + deathPos.getY() + " z: " + deathPos.getZ();
                hook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("you died!")
                    .setDescription(pos)
                    .setColor(Color.RED)
                );
                hook.execute();
                hook.clearEmbeds();
            }
        }
    }

    @EventHandler
    private void onRenderPlayer(EntityAddedEvent event) {
        if (logRenderDistance.get()) {
            if (event.entity.isPlayer()) {
                String playerName = event.entity.getName().getLiteralString();

                assert mc.player != null;
                assert playerName != null;

                if (shouldIgnoreSelf.get()) {
                    if (playerName.equals(mc.player.getName().getLiteralString())) return;
                }

                double x = event.entity.getX();
                double y = event.entity.getY();
                double z = event.entity.getZ();
                String pos = "X: " + x + " Y: " + y + " Z: " + z;
                hook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("Player: " + event.entity.getName().getLiteralString() + " Entered RenderDistance")
                    .setDescription(pos)
                    .setColor(Color.RED)
                );
                try {
                    hook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                hook.clearEmbeds();
            }
        }
    }
}

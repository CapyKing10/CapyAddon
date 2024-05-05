package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.DiscordWebhook;
import com.capy.capyaddon.utils.LogUtils;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.io.IOException;

public class WebhookLogging extends Module {

    private DiscordWebhook hook;

    public SettingGroup sgGeneral = settings.getDefaultGroup();

    public WebhookLogging() {
        super(CapyAddon.CATEGORY, "WebhookLogger", "a module that logs all chat messages to a discord webhook");
    }

    private final Setting<String> url = sgGeneral.add(new StringSetting.Builder()
        .name("webhookURL")
        .description("URL of the webhook to send messages to")
        .defaultValue("")
        .build());

    @Override
    public void onActivate() {
        LogUtils.sendMessage(Formatting.WHITE + "Turned the module called " + Formatting.GOLD + "WebhookLogger" + Formatting.GREEN + " On");
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

    public void onDeactivate() {
        LogUtils.sendMessage(Formatting.WHITE + "Turned the module called " + Formatting.GOLD + "WebhookLogger" + Formatting.RED + " Off");
    }
}

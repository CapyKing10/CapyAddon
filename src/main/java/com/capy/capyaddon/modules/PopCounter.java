package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.LogUtils;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Formatting;

/*
*
*   To Do: Make "Include Enemy Ping" setting work
*
 */

public class PopCounter extends Module {
    public int popCounter;

    public int streakPops;
    public int streakKills;

    public SettingGroup sgLog = settings.createGroup("Log Settings");
    public SettingGroup sgStreak = settings.createGroup("Streak Settings");


    // Log Settings

    private final Setting<Boolean> logToChat = sgLog.add(new BoolSetting.Builder()
        .name("Log To Chat")
        .description("send a notification in chat when you popped someone")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> includeUsername = sgLog.add(new BoolSetting.Builder()
        .name("Include Enemy Usernames")
        .description("include the username of the player that you popped")
        .visible(logToChat::get)
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> resetPopsOnDeath = sgLog.add(new BoolSetting.Builder()
        .name("Reset Pops On Death")
        .description("reset the amount of pops when you die")
        .defaultValue(true)
        .build()
    );


    // Streak Settings

    private final Setting<Boolean> trackStreak = sgStreak.add(new BoolSetting.Builder()
        .name("Track Streak")
        .description("track your streak of pops and kills")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> trackKills = sgStreak.add(new BoolSetting.Builder()
        .name("Track Kills")
        .description("track your streak of pops and kills")
        .visible(trackStreak::get)
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> resetStreakOnDeath = sgStreak.add(new BoolSetting.Builder()
        .name("Reset Streak On Death")
        .description("reset your streak when you die")
        .visible(trackStreak::get)
        .defaultValue(false)
        .build()
    );

    public PopCounter() {
        super(CapyAddon.CATEGORY, "PopCounter", "counts the amount of times you have popped someone's totem");
    }

    @Override
    public void onActivate() {
        popCounter = 0;
        streakKills = 0;
        streakPops = 0;
    }

    @EventHandler
    private void onTotemPop(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityStatusS2CPacket packet)) return;
        if (packet.getStatus() != 35) return;
        Entity entity = packet.getEntity(mc.world);
        if (!(entity instanceof PlayerEntity)) return;

        popCounter++;
        if (logToChat.get()) {
            if (includeUsername.get()) {
                LogUtils.sendMessage("You popped " + entity.getName().getString() + ", total pops: " + popCounter);
            } else {
                LogUtils.sendMessage("You popped a player, total pops: " + popCounter);
            }
        }

        if (trackStreak.get()) {
            streakPops++;
            if (includeUsername.get()) {
                LogUtils.sendMessage("You popped " + entity.getName().getString() + ", total pops on your streak: " + popCounter);
            } else {
                LogUtils.sendMessage("You popped a player, total pops on your streak: " + popCounter);
            }
        }
    }

    @EventHandler
    private void onPlayerKill(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityStatusS2CPacket packet)) return;
        Entity entity = packet.getEntity(mc.world);
        if (!(entity instanceof PlayerEntity)) return;

        if (((PlayerEntity) entity).getHealth() <= 0) {
            if (trackKills.get()) streakKills++;
            if (logToChat.get()) {
                if (includeUsername.get()) {
                    LogUtils.sendMessage("You killed " + entity.getName().getString() + ", you now have a streak of " + streakKills + " kills!");
                } else {
                    LogUtils.sendMessage("You killed a player, you now have a streak of " + streakKills + " kills!");
                }
            }
        }
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        assert mc.player != null;

        if (event.screen instanceof DeathScreen) {
            if (resetPopsOnDeath.get()) {
                popCounter = 0;
                LogUtils.sendMessage("You died :( The pop counter got reset!");
            }

            if (resetStreakOnDeath.get()) {
                streakKills = 0;
                streakPops = 0;
                LogUtils.sendMessage("You died :( reset your streak!");
            }
        }
    }

}

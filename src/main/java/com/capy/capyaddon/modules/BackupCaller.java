package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class BackupCaller extends Module {
    public BackupCaller() {
        super(CapyAddon.CATEGORY, "BackupCaller", "call for backup in chat");
    }

    @Override
    public void onActivate() {
        assert mc.player != null;
        ChatUtils.sendPlayerMsg("I Need Backup " + mc.player.getX + mc.player.getY() + " " + mc.player.getZ());
    }
}

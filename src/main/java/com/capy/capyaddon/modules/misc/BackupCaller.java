package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class BackupCaller extends Module {
    public BackupCaller() {
        super(CapyAddon.MISC, "backup-caller", "call for backup in chat");
    }

    @Override
    public void onActivate() {
        assert mc.player != null;
        ChatUtils.sendPlayerMsg("I Need Backup " + mc.player.getX() + " " + mc.player.getZ());
    }
}

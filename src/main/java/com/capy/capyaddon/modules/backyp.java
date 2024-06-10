package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class backyp extends Module {
    public backyp() {
        super(CapyAddon.CATEGORY, "Backup Caller", "I need backup !!!!!!!!!!");
    }

@Override
public void onActivate(){
    assert mc.player != null;
    ChatUtils.sendPlayerMsg("I Need Backup " + mc.player.getX() + " " + mc.player.getY() + " " + mc.player.getZ());
    // same thing with here this.disable() pasted this from my oyvey build lol
}
}


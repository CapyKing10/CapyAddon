package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class AutoMeow extends Module {
    public AutoMeow() {
        super(CapyAddon.CATEGORY, "Auto Vertical", "Fuck you advik (elytra fly's you to the moon)");
    }
    @Override
    public void onActivate(){
        ChatUtils.sendPlayerMsg("Meow");
        // need to figure out what this is called in meteor terms this.disable()
    }
}
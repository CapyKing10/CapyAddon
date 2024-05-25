package com.capy.capyaddon.modules;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.LogUtils;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import net.minecraft.util.Formatting;

public class AutoVertical extends Module {
    public AutoVertical() {
        super(CapyAddon.CATEGORY, "Auto Vertical", "Fuck you advik (elytra fly's you to the moon)");
    }

    public void onActivate() {
        LogUtils.sendMessage(Formatting.WHITE + "Turned the module called " + Formatting.GOLD + "ShulkerDrop" + Formatting.GREEN + " On");
        if (Modules.get().get(ElytraFly.class).isActive()) {
            mc.options.jumpKey.setPressed(true);
        } else {
            LogUtils.sendMessage("ElytraFly needs to be enabled on mode: Vanilla");
            this.toggle();
        }
    }

    public void onDeactivate() {
        mc.options.jumpKey.setPressed(false);
        LogUtils.sendMessage(Formatting.WHITE + "Turned the module called " + Formatting.GOLD + "ShulkerDrop" + Formatting.RED + " Off");
    }
}

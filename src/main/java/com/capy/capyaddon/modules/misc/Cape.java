package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Cape extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<capes> cape = sgGeneral.add(new EnumSetting.Builder<capes>()
        .name("Cape")
        .description("Choose a cape")
        .defaultValue(capes.CAPY)
        .build()
    );

    public Cape() {
        super(CapyAddon.MISC, "Cape", "get a cool cape");
    }

    public enum capes {
        CAPY,
        RUSHERKEK,
        METEOR_DONATOR,
        METEOR_MODERATOR
    }

    public static Cape getInstance() {
        return new Cape();
    }
}

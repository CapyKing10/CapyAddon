package com.capy.capyaddon;

import com.capy.capyaddon.commands.discord;
import com.capy.capyaddon.commands.leakCoordinates;
import com.capy.capyaddon.hud.ObscufatedCoords;
import com.capy.capyaddon.hud.fps;
import com.capy.capyaddon.hud.watermark;
import com.capy.capyaddon.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class CapyAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("CapyAddon");
    public static final HudGroup HUD_GROUP = new HudGroup("CapyAddon");

    @Override
    public void onInitialize() {
        LOG.info("Initializing CapyAddon");

        // Modules
        Modules.get().add(new Texturing());
        Modules.get().add(new WebhookLogging());
        Modules.get().add(new ShulkerDrop());
        Modules.get().add(new EntityFly());
        Modules.get().add(new CatSpam());
        Modules.get().add(new AutoVertical());

        // Commands
        Commands.add(new discord());
        Commands.add(new leakCoordinates());

        // HUD
        Hud.get().register(watermark.INFO);
        Hud.get().register(fps.INFO);
        Hud.get().register(ObscufatedCoords.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.capy.capyaddon";
    }
}

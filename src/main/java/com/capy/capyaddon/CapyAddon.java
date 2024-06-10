package com.capy.capyaddon;

import com.capy.capyaddon.commands.Discord;
import com.capy.capyaddon.commands.GambleYourBase;
import com.capy.capyaddon.hud.ObscufatedCoords;
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

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CapyAddon extends MeteorAddon {
    public static final String NAME = "CapyAddon";
    public static final String VERSION = "1.0.2";

    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("CapyAddon");
    public static final HudGroup HUD_GROUP = new HudGroup("CapyAddon");

    @Override
    public void onInitialize() {
        mc.getWindow().setTitle("CapyAddon Current FPS: " + mc.getCurrentFps());
        LOG.info("Initializing CapyAddon");

        // Modules
        Modules.get().add(new Texturing());
        Modules.get().add(new WebhookLogging());
        Modules.get().add(new ShulkerDrop());
        Modules.get().add(new AutoMeow());
        Modules.get().add(new backyp());
        Modules.get().add(new EntityFly());
        Modules.get().add(new CatSpam());
        Modules.get().add(new AutoVertical());
        Modules.get().add(new ChunkDupeTimer());
        Modules.get().add(new ClientPrefix());
        Modules.get().add(new PopCounter());
        Modules.get().add(new ArmorNotify());

        // Commands
        Commands.add(new Discord());
        Commands.add(new GambleYourBase());

        // HUD
        Hud.get().register(watermark.INFO);
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

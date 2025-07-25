package com.capy.capyaddon;

import com.capy.capyaddon.commands.Discord;
import com.capy.capyaddon.commands.GambleYourBase;
import com.capy.capyaddon.hud.*;
import com.capy.capyaddon.modules.misc.dupes.ItemFrameDupe;
import com.capy.capyaddon.modules.misc.dupes.ItemFrameDupe2;
import com.capy.capyaddon.modules.misc.*;
import com.capy.capyaddon.modules.misc.printer.Printer;
import com.capy.capyaddon.modules.pvp.*;
import com.capy.capyaddon.utils.User;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import org.slf4j.Logger;

import java.io.File;

public class CapyAddon extends MeteorAddon {
    public static final String NAME = "CapyAddon";
    public static final String VERSION = User.isDev() ? "1.0.6-beta-extern-1.0f.0AC+" : "1.0.6";
    public static final String MC_VERSION = "1.21.1";

    public static final Logger LOG = LogUtils.getLogger();
    public static final Category MISC = new Category("CapyAddon - MISC");
    public static final Category PVP = new Category("CapyAddon - PvP");
    public static final HudGroup HUD_GROUP = new HudGroup("CapyAddon");

    @Override
    public void onInitialize() {
        LOG.info("Initializing CapyAddon " + VERSION);

        // Modules
        Modules.get().add(new TNTAura());
        Modules.get().add(new Texturing());
        Modules.get().add(new WebhookLogging());
        Modules.get().add(new ShulkerDrop());
        Modules.get().add(new EntityFly());
        Modules.get().add(new CatSpam());
        Modules.get().add(new AutoVertical());
        Modules.get().add(new ChunkDupeTimer());
        Modules.get().add(new ClientPrefix());
        Modules.get().add(new PopCounter());
        Modules.get().add(new ArmorNotify());
        Modules.get().add(new AntiPlayerSwing());
        Modules.get().add(new LogoutSpotsPlus());
        Modules.get().add(new VisualRange());
        Modules.get().add(new AntiPistonPush());
        Modules.get().add(new Box());
        Modules.get().add(new KillEffect());
        Modules.get().add(new BurrowESP());
        Modules.get().add(new HitboxDesync());
        Modules.get().add(new ItemFrameSearch());
        Modules.get().add(new Printer());
        Modules.get().add(new PacketMine());
        Modules.get().add(new AutoBurrow());
        Modules.get().add(new AutoCityBoss());
        Modules.get().add(new AutoRestock());
        Modules.get().add(new EntityAlert());
        Modules.get().add(new ItemFrameDupe());
        Modules.get().add(new ItemFrameDupe2());
        Modules.get().add(new MapartCollectionDuplicateChecker());
        Modules.get().add(new MapartTradingAssistant());

        // Commands
        Commands.add(new Discord());
        Commands.add(new GambleYourBase());

        // HUD
        Hud.get().register(fps.INFO);
        Hud.get().register(watermark.INFO);
        Hud.get().register(ObscufatedCoords.INFO);
        Hud.get().register(Welcomer.INFO);
        Hud.get().register(Direction.INFO);
        Hud.get().register(Position.INFO);

        Settings.get();
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(PVP);
        Modules.registerCategory(MISC);
    }

    @Override
    public String getWebsite() {
        return "https://github.com/CapyKing10/CapyAddon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("CapyKing10", "CapyAddon");
    }

    @Override
    public String getPackage() {
        return "com.capy.capyaddon";
    }

    public static File GetConfigFile(String key, String filename) {
        return new File(new File(new File(new File(MeteorClient.FOLDER, "omegaware"), key), Utils.getFileWorldName()), filename);
    }
}

package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.cLogUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Formatting;

import java.util.Set;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EntityAlert extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Select specific entities.")
        .defaultValue(EntityType.CREEPER)
        .build()
    );

    private final Setting<Double> distance = sgGeneral.add(new DoubleSetting.Builder()
        .name("distance")
        .description("Distance to be alerted.")
        .defaultValue(10.0)
        .build()
    );

    // Set to keep track of alerted entities
    private final Set<UUID> alertedEntities = new HashSet<>();

    public EntityAlert() {
        super(CapyAddon.MISC, "entity-alert", "Notifies you when an entity is in range.");
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        Set<UUID> currentEntitiesInRange = new HashSet<>();

        for (Entity entity : mc.world.getEntities()) {
            if (!entities.get().contains(entity.getType())) continue;

            double dist = mc.player.distanceTo(entity);
            UUID id = entity.getUuid();

            if (dist <= distance.get()) {
                currentEntitiesInRange.add(id);

                if (!alertedEntities.contains(id)) {
                    alertedEntities.add(id);
                    cLogUtils.sendMessage("Entity " + Formatting.RED + entity.getName().getString() + Formatting.RESET + " has entered range!", false);
                }
            }
        }

        // Remove entities that are no longer in range from the alert set
        alertedEntities.retainAll(currentEntitiesInRange);
    }
}

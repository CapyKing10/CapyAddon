package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.LogoutSpotsPlus.Entry;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KillEffect extends Module {
    public final List<PlayerEntity> players = new ArrayList<>();

    public KillEffect() {
        super(CapyAddon.PVP, "kill-effect", "kill effects for cool and stuff idk");
    }

    @Override
    public void onDeactivate() {
        players.clear();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.deathTime > 0 || player.getHealth() <= 0) {
                if (!players.contains(player)) {
                    summonLightning(player.getBlockPos());
                    players.add(player);
                }
            }
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (!event.entity.getUuid().equals(mc.player.getUuid())) {
            if (event.entity instanceof PlayerEntity) {
                players.remove((PlayerEntity) event.entity);
            }
        }
    }

    private void summonLightning(BlockPos pos) {
        LightningEntity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
        lightningEntity.refreshPositionAfterTeleport(pos.getX(), pos.getY(), pos.getZ());

        mc.world.addEntity(lightningEntity);
    }
}

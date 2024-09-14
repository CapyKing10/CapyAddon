package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.abs;

public class HitboxDesync extends Module {
    private static final double MAGIC_OFFSET = .200009968835369999878673424677777777777761;

    public HitboxDesync() {
        super(CapyAddon.MISC, "hitbox-desync", "thanks mioclient devs!");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null) return;
        if (mc.player == null) return;
        Direction f = mc.player.getHorizontalFacing();
        Box bb = mc.player.getBoundingBox();
        Vec3d center = bb.getCenter();
        Vec3d offset = new Vec3d(f.getUnitVector());

        Vec3d fin = merge(Vec3d.of(BlockPos.ofFloored(center)).add(.5, 0, .5).add(offset.multiply(MAGIC_OFFSET)), f);
        mc.player.setPosition(fin.x == 0 ? mc.player.getX() : fin.x,
            mc.player.getY(),
            fin.z == 0 ? mc.player.getZ() : fin.z);
        this.toggle();
    }

    private Vec3d merge(Vec3d a, Direction facing) {
        return new Vec3d(a.x * abs(facing.getUnitVector().x()), a.y * abs(facing.getUnitVector().y()), a.z * abs(facing.getUnitVector().z()));
    }
}

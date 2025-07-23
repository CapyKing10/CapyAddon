package com.capy.capyaddon.modules.misc;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.TimerUtils;
import com.capy.capyaddon.utils.cLogUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class AutoRestock extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> shulkerSlot = sgGeneral.add(new IntSetting.Builder()
        .name("shulker-slot")
        .description("slot to move the shulker with obsidian to")
        .defaultValue(9)
        .min(1)
        .max(9)
        .sliderMax(9)
        .build()
    );

    private final Setting<Double> itemTimer = sgGeneral.add(new DoubleSetting.Builder()
        .name("item-timer")
        .description("Delay between putting items in the chest.")
        .defaultValue(0.05)
        .min(0)
        .build()
    );

    private final Setting<Boolean> debugging = sgGeneral.add(new BoolSetting.Builder()
        .name("debugging")
        .description("debugging")
        .defaultValue(false)
        .build()
    );

    public TimerUtils timer = new TimerUtils();
    private List<Integer> shulkerSlots = new ArrayList<>();
    private boolean shouldReplenish;
    private boolean busy = false;
    private boolean preBusy = false;
    private boolean done = false;

    public AutoRestock() {
        super(CapyAddon.MISC, "auto-restock", "restocks your inventory automatically during logo building");
    }

    @Override
    public void onActivate() {
        timer.reset();
    }

    @Override
    public void onDeactivate() {
        busy = false;
        preBusy = false;
        shouldReplenish = false;
        done = false;
        shulkerSlots.clear();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        preBusy = busy;
        if (debugging.get()) cLogUtils.sendMessage("status of busy" + busy, false);
        if (debugging.get()) cLogUtils.sendMessage("status of preBust" + preBusy, false);
        shulkerSlots.clear();

        if (!mc.player.getInventory().contains(Items.OBSIDIAN.getDefaultStack())) {
            shouldReplenish = true;
        } else {
            shouldReplenish = false;
        }

        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isShulker(stack)) {
                if (containsObsidian(stack)) {
                    shulkerSlots.add(i);
                }
            }
        }

        if (debugging.get()) cLogUtils.sendMessage("should replenish " + shouldReplenish, false);

        if (shouldReplenish && !busy) {
            if (shulkerSlots.isEmpty()) {
                cLogUtils.sendMessage("Should replenish but no shulkers with obsidian present.", true);
                this.toggle();
            }

            moveShulkerToHotbar();
            mc.player.getInventory().selectedSlot = shulkerSlot.get() - 1;

            busy = true;

            placeAndOpen(mc.player.getBlockPos());
            moveItems(mc.player.currentScreenHandler);
        }
    }

    @EventHandler
    public void onTickPost(TickEvent.Post event) {
        if (done) {
            if (mc.currentScreen != null) {
                mc.currentScreen.close();
            }
            busy = false;
        }
    }

    private void placeAndOpen(BlockPos playerPos) {
        if (debugging.get()) cLogUtils.sendMessage("place and open", false);
        BlockPos pos = playerPos.down().down();
        if (isBlockAt(pos)) return;

        Vec3d eyes = mc.player.getEyePos();
        boolean inside =
            eyes.x > pos.getX() && eyes.x < pos.getX() + 1 &&
                eyes.y > pos.getY() && eyes.y < pos.getY() + 1 &&
                eyes.z > pos.getZ() && eyes.z < pos.getZ() + 1;

        sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(pos.toCenterPos(), Direction.DOWN, pos, inside), 0));
        sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(pos.toCenterPos(), Direction.DOWN, pos, inside), 0));
    }

    private void moveShulkerToHotbar() {
        if (!shulkerSlots.isEmpty()) {
            int slot = shulkerSlots.get(0);
            InvUtils.move().from(slot).to(shulkerSlot.get() - 1);
        }
    }

    private void sendPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendPacket(packet);
    }

    private boolean isBlockAt(BlockPos pos) {
        assert mc.world != null;

        BlockState blockState = mc.world.getBlockState(pos);

        return !blockState.isAir();
    }

    private boolean isShulker(ItemStack itemStack) {
        String translationKey = itemStack.toString();
        return translationKey.contains("shulker_box");
    }

    private boolean containsObsidian(ItemStack shulker) {
        ItemStack[] items = new ItemStack[9 * 3];
        Utils.getItemsInContainerItem(shulker, items);
        for (ItemStack stack : items) {
            if (stack.getItem() == Items.OBSIDIAN) {
                return true;
            }
        }
        return false;
    }

    private int getContainerRows(ScreenHandler handler) {
        return (handler instanceof GenericContainerScreenHandler ? ((GenericContainerScreenHandler) handler).getRows() : 3);
    }

    public void moveItems(ScreenHandler handler) {
        int containerInvOffset = getContainerRows(handler) * 9;
        MeteorExecutor.execute(() -> moveSlots(handler, 0, containerInvOffset));
    }

    private void moveSlots(ScreenHandler handler, int start, int end) {
        done = false;
        for (int i = start; i < end; i++) {
            Slot slot = handler.getSlot(i);
            if (!slot.hasStack()) continue;
            if (slot.getStack().getItem() == Items.OBSIDIAN) {
                if (mc.currentScreen == null) break;
                if (timer.hasReached((long) (itemTimer.get() * 1000))) {
                    InvUtils.shiftClick().slotId(i);
                    timer.reset();
                }
            }
        }
        done = true;
    }
}

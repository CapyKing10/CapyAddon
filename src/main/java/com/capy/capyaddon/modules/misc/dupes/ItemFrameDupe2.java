package com.capy.capyaddon.modules.misc.dupes;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;


public class ItemFrameDupe2 extends Module {
    private static final ArrayList<BlockPos> blocks = new ArrayList<>();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPlace = settings.createGroup("Place");
    private final SettingGroup sgBreak = settings.createGroup("Break");
    private final SettingGroup sgAutoScroll = settings.createGroup("Auto Scroll HotBar");
    private final Setting<Integer> distance = sgGeneral.add(new IntSetting.Builder()
        .name("distance")
        .description("The max distance to search for pistons.")
        .min(1)
        .sliderMin(1)
        .defaultValue(5)
        .sliderMax(6)
        .max(6)
        .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Whether or not to rotate when placing.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> rotateItem = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate item")
        .description("Whether or not to keep rotating the item frame")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> swapBack = sgGeneral.add(new BoolSetting.Builder()
        .name("swap-back")
        .description("Whether or not to swap back to the previous held item after placing.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> placeItemFrame = sgPlace.add(new BoolSetting.Builder()
        .name("place-item-frame")
        .description("Whether or not to place an item frame first (needs itemframes in hotbar)")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> placeDelay = sgPlace.add(new IntSetting.Builder()
        .name("place-delay")
        .description("The place delay between placements and breaking")
        .defaultValue(1)
        .sliderMin(1)
        .sliderMax(10)
        .build()
    );

    private final Setting<Boolean> multiBreak = sgBreak.add(new BoolSetting.Builder()
        .name("multi-break")
        .description("Attempts to rotate and break multiple item frames. 90% chance it will kick you out of a server. Multi-Break does not provide multi place, so if the item frames get broken, they wont be replaced. Set your delays accordingly")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> breakDelay = sgBreak.add(new IntSetting.Builder()
        .name("item-break-delay")
        .description("The amount of delay between breaking the item in ticks.")
        .defaultValue(2)
        .min(0)
        .sliderMax(60)
        .build()
    );
    private final Setting<Boolean> autoScroll = sgAutoScroll.add(new BoolSetting.Builder()
        .name("auto-scroll-hotbar")
        .description("Will automatically scroll through the hot bar. Item frames wont be duplicated but any other item in the hot bar will")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> scrollDelay = sgAutoScroll.add(new IntSetting.Builder()
        .name("auto-scroll-delay")
        .description("How many ticks later should we scroll to next hotbar item")
        .defaultValue(3)
        .min(0)
        .sliderMax(10)
        .build()
    );
    private int timer, autoScrollTimer = 0;
    private int breakDelayTimer;

    public ItemFrameDupe2() {
        super(CapyAddon.MISC, "frame-dupe-2", "no saar i didn skidt here is creditz: https://github.com/tanishisherewithhh/AutoDuperAddon");
    }

    private static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
        blocks.clear();

        for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (centerPos.isWithinDistance(pos,radius) && !blocks.contains(pos)) blocks.add(pos);
                }
            }
        }

        return blocks;
    }

    @Override
    public void onActivate() {
        timer = placeDelay.get();
        autoScrollTimer = scrollDelay.get();
        breakDelayTimer = 0;
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        breakDelayTimer = 0;
    }

    @SuppressWarnings("all")
    @EventHandler
    private void onTick(TickEvent.Pre event) {
//        if (!Utils.canUpdate()) return;

        ClientPlayerInteractionManager c = mc.interactionManager;

        if(autoScroll.get()) {
            if (autoScrollTimer > 0) {
                autoScrollTimer--;
            } else {
                int currentSlot = mc.player.getInventory().selectedSlot;
                int nextSlot = (currentSlot + 1) % mc.player.getInventory().getHotbarSize();
                mc.player.getInventory().selectedSlot = nextSlot;
                ((IClientPlayerInteractionManager) mc.interactionManager).meteor$syncSelected();
                autoScrollTimer = scrollDelay.get();
            }
        }

        if(placeItemFrame.get()) {
            FindItemResult itemResult = InvUtils.findInHotbar(Items.ITEM_FRAME, Items.GLOW_ITEM_FRAME);
            if (!itemResult.found()) {
                error("No item frames found in hotbar.");
                toggle();
                return;
            }
            if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                Vec3d vec = mc.crosshairTarget.getPos();
                BlockPos blockPos = new BlockPos((int) vec.getX(), (int) vec.getY(), (int) vec.getZ());
                BlockState blockState = mc.world.getBlockState(blockPos);
                BlockUtils.place(blockPos, itemResult, rotate.get(), 50, true, false, swapBack.get());
            }
        }

//        if (timer > 0) {
//            timer--;
//            return;
//        } else {
//            timer = placeDelay.get();
//        }

        // Main thread gets stuck because I didnt opt in to use the tick based method
        //Also I forgot how to code and i dont know why I was intialising the breakThread again every tick -_-
        //Bad code 2.0 (i learned from my mistakes)
        MeteorExecutor.execute(() -> {
            //ClientPlayerInteractionManager c = mc.interactionManager;
            if (mc.player.getWorld() == null) {
                return;
            }
            Box box = new Box(mc.player.getEyePos().add(-3, -3, -3), mc.player.getEyePos().add(3, 3, 3));
            var itemframes = mc.player.getWorld().getEntitiesByClass(ItemFrameEntity.class, box, itemFrameEntity -> true);
            if (!itemframes.isEmpty()) {
                for(ItemFrameEntity itemFrame: itemframes) {
                    if(!itemFrame.getBlockPos().isWithinDistance(mc.player.getBlockPos(),mc.player.getEntityInteractionRange())) continue;
                    Item mainHanditem = mc.player.getMainHandStack().getItem();
                    // ClientPlayerInteractionManager c = mc.interactionManager;
                    if (mainHanditem == Items.ITEM_FRAME || mainHanditem == Items.GLOW_ITEM_FRAME) {
                        return;
                    }
                    //ItemFrameEntity itemFrame = itemframes.getFirst();

                    c.interactEntity(mc.player, itemFrame, Hand.MAIN_HAND);
                    if (!itemFrame.getHeldItemStack().isEmpty() && itemFrame.getHeldItemStack().isInFrame()) {
                        // Rotate the frame
//                        if (rotateItem.get()) {
//                            c.interactEntity(mc.player, itemFrame, Hand.MAIN_HAND);
//                        }
                        // Delay before attacking the entity
                        // Thread delay because the code runs very fast
//                        try {
//                            TimeUnit.MILLISECONDS.sleep(600);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

                        breakDelayTimer++;
                        if (!itemFrame.getHeldItemStack().isEmpty()) {
                            c.attackEntity(mc.player, itemFrame);
                            //Utils.leftClick();
                            breakDelayTimer = 0;
                        }
//                        try {
//                            TimeUnit.MILLISECONDS.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                    if(!multiBreak.get()){
                        break;
                    }
                }
            }
        });
    }

    private boolean shouldPlace(BlockPos blockPos) {
        return BlockUtils.canPlace(blockPos,true);
    }
}

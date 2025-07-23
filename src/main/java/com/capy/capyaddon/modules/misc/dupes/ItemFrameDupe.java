package com.capy.capyaddon.modules.misc.dupes;

import com.capy.capyaddon.CapyAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFrameDupe extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgVisual = settings.createGroup("Visual");
    private final SettingGroup sgStats = settings.createGroup("Statistics");

    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
        .name("speed")
        .description("Operations per second.")
        .defaultValue(20)
        .min(1)
        .max(100)
        .sliderMin(1)
        .sliderMax(100)
        .build());

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Interaction range in blocks.")
        .defaultValue(4.0)
        .min(1.0)
        .max(10.0)
        .sliderMin(1.0)
        .sliderMax(10.0)
        .build());

    private final Setting<Item> targetItem = sgGeneral.add(new ItemSetting.Builder()
        .name("target-item")
        .description("Item to duplicate. Set to 'air' for any item.")
        .defaultValue(Items.AIR)
        .build());

    private final Setting<Boolean> refillFrames = sgGeneral.add(new BoolSetting.Builder()
        .name("refill-frames")
        .description("Replace broken item frames.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> silentMode = sgGeneral.add(new BoolSetting.Builder()
        .name("silent-mode")
        .description("Use silent packet-based interactions.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> highlightFrames = sgVisual.add(new BoolSetting.Builder()
        .name("highlight-frames")
        .description("Highlight item frames with different colors based on their state.")
        .defaultValue(true)
        .build());

    private final Setting<ShapeMode> shapeMode = sgVisual.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(highlightFrames::get)
        .build());

    private final Setting<SettingColor> emptyColor = sgVisual.add(new ColorSetting.Builder()
        .name("empty-color")
        .description("Color for empty frames.")
        .defaultValue(new SettingColor(0, 255, 0, 100))
        .visible(highlightFrames::get)
        .build());

    private final Setting<SettingColor> activeColor = sgVisual.add(new ColorSetting.Builder()
        .name("active-color")
        .description("Color for frames with items.")
        .defaultValue(new SettingColor(255, 255, 0, 150))
        .visible(highlightFrames::get)
        .build());

    private final Setting<SettingColor> brokenColor = sgVisual.add(new ColorSetting.Builder()
        .name("broken-color")
        .description("Color for broken frames.")
        .defaultValue(new SettingColor(255, 0, 0, 200))
        .visible(highlightFrames::get)
        .build());

    private final Setting<Boolean> showStats = sgStats.add(new BoolSetting.Builder()
        .name("show-stats")
        .description("Display statistics in the module info.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> resetOnActivate = sgStats.add(new BoolSetting.Builder()
        .name("reset-on-activate")
        .description("Reset statistics when module is activated.")
        .defaultValue(true)
        .build());

    private final Map<BlockPos, Long> brokenFrames = new HashMap<>();
    private final List<ItemFrameEntity> activeFrames = new ArrayList<>();
    private double timer = 0;

    private int itemsPopped = 0;
    private int itemsPlaced = 0;
    private int framesBroken = 0;
    private int framesReplaced = 0;
    private int itemsDropped = 0;
    private int startItemCount = 0;

    public ItemFrameDupe() {
        super(CapyAddon.MISC, "frame-dupe", "Automates item frame duplication plugins.");
    }

    @Override
    public void onActivate() {
        brokenFrames.clear();
        activeFrames.clear();
        timer = 0;

        if (resetOnActivate.get()) {
            itemsPopped = 0;
            itemsPlaced = 0;
            framesBroken = 0;
            framesReplaced = 0;
            itemsDropped = 0;

            if (targetItem.get() != Items.AIR) {
                startItemCount = countItemsInInventory();
            }
        }
    }

    @Override
    public void onDeactivate() {
        if (targetItem.get() != Items.AIR) {
            int endItemCount = countItemsInInventory();
            itemsDropped = startItemCount - endItemCount + itemsPopped;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) return;

        timer += 1.0;
        double interval = 20.0 / speed.get();
        if (timer < interval) return;
        timer -= interval;

        scanForFrames();

        processBrokenFrames();

        processActiveFrames();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!highlightFrames.get()) return;

        for (ItemFrameEntity frame : activeFrames) {
            if (!frame.isAlive()) continue;

            Box box = frame.getBoundingBox();
            SettingColor color = frame.getHeldItemStack().isEmpty() ? emptyColor.get() : activeColor.get();

            event.renderer.box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ,
                color, color, shapeMode.get(), 0);
        }

        for (BlockPos pos : brokenFrames.keySet()) {
            Box box = new Box(pos);
            event.renderer.box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ,
                brokenColor.get(), brokenColor.get(), shapeMode.get(), 0);
        }
    }

    @Override
    public String getInfoString() {
        if (!showStats.get()) return null;
        return itemsPopped + "p/" + itemsPlaced + "pl";
    }

    private void scanForFrames() {
        Box box = mc.player.getBoundingBox().expand(range.get());
        List<ItemFrameEntity> frames = mc.world.getEntitiesByClass(
            ItemFrameEntity.class, box, frame -> true
        );

        activeFrames.clear();
        for (ItemFrameEntity frame : frames) {
            if (frame.isAlive() && mc.player.distanceTo(frame) <= range.get()) {
                activeFrames.add(frame);
            }
        }
    }

    private void processBrokenFrames() {
        if (!refillFrames.get() || brokenFrames.isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        List<BlockPos> toRemove = new ArrayList<>();

        for (Map.Entry<BlockPos, Long> entry : brokenFrames.entrySet()) {
            BlockPos pos = entry.getKey();

            // Wait 1 second before replacing
            if (currentTime - entry.getValue() > 1000) {
                if (replaceFrame(pos)) {
                    toRemove.add(pos);
                    framesReplaced++;
                }
            }
        }

        for (BlockPos pos : toRemove) {
            brokenFrames.remove(pos);
        }
    }

    private void processActiveFrames() {
        for (ItemFrameEntity frame : activeFrames) {
            if (!frame.isAlive()) {
                brokenFrames.put(frame.getBlockPos(), System.currentTimeMillis());
                framesBroken++;
                continue;
            }

            ItemStack held = frame.getHeldItemStack();

            if (held.isEmpty()) {
                if (placeItem(frame)) {
                    itemsPlaced++;
                }
            } else if (matchesTarget(held)) {
                popItem(frame);
                itemsPopped++;
            }
        }
    }

    private void popItem(ItemFrameEntity frame) {
        if (frame.getHeldItemStack().isEmpty()) return;

        if (silentMode.get()) {
            mc.getNetworkHandler().sendPacket(
                PlayerInteractEntityC2SPacket.attack(frame, mc.player.isSneaking())
            );
        } else {
            mc.interactionManager.attackEntity(mc.player, frame);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private boolean placeItem(ItemFrameEntity frame) {
        int slot = findMatchingItemSlot();
        if (slot == -1) return false;

        if (silentMode.get()) InvUtils.swap(slot, true);

        if (silentMode.get()) {
            mc.getNetworkHandler().sendPacket(
                PlayerInteractEntityC2SPacket.interact(frame, mc.player.isSneaking(), Hand.MAIN_HAND)
            );
        } else {
            mc.interactionManager.interactEntity(mc.player, frame, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
        }

        if (silentMode.get()) InvUtils.swapBack();
        return true;
    }

    private boolean replaceFrame(BlockPos pos) {
        int frameSlot = InvUtils.find(Items.ITEM_FRAME).slot();
        if (frameSlot == -1) return false;

        if (silentMode.get()) InvUtils.swap(frameSlot, true);

        Vec3d hitPos = new Vec3d(
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5
        );

        BlockHitResult hitResult = new BlockHitResult(
            hitPos,
            Direction.UP,
            pos,
            false
        );

        boolean success = mc.interactionManager.interactBlock(
            mc.player,
            Hand.MAIN_HAND,
            hitResult
        ) != net.minecraft.util.ActionResult.FAIL;

        if (silentMode.get()) InvUtils.swapBack();
        return success;
    }

    private int findMatchingItemSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (matchesTarget(stack)) return i;
        }
        return -1;
    }

    private boolean matchesTarget(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        Item target = targetItem.get();

        if (target == Items.AIR) return true;

        return itemStack.isOf(target);
    }

    private int countItemsInInventory() {
        int count = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (matchesTarget(stack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public int getItemsPopped() {
        return itemsPopped;
    }

    public int getItemsPlaced() {
        return itemsPlaced;
    }

    public int getFramesBroken() {
        return framesBroken;
    }

    public int getFramesReplaced() {
        return framesReplaced;
    }

    public int getItemsDropped() {
        return itemsDropped;
    }

    public int getNetGain() {
        return itemsPlaced - itemsPopped;
    }
}

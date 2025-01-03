package com.capy.capyaddon.modules.pvp;

import com.capy.capyaddon.CapyAddon;
import com.capy.capyaddon.utils.cLogUtils;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

public class VisualRange extends Module {
    SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> visualRangeIgnoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> visualRangeIgnoreFakes = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-fake-players")
        .description("Ignores fake players.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> visualMakeSound = sgGeneral.add(new BoolSetting.Builder()
        .name("sound")
        .description("Emits a sound effect on enter / leave")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> prefix = sgGeneral.add(new BoolSetting.Builder()
        .name("prefix")
        .description("prefix like [-] and [+]")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> bussin = sgGeneral.add(new BoolSetting.Builder()
        .name("bussin")
        .description("bussin way")
        .defaultValue(false)
        .build()
    );

    public VisualRange() {
        super(CapyAddon.PVP, "VisualRange", "notifies you when someone enters you visual range");
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (!event.entity.getUuid().equals(mc.player.getUuid())) {
            if (event.entity instanceof PlayerEntity) {
                if ((!visualRangeIgnoreFriends.get() || !Friends.get().isFriend(((PlayerEntity) event.entity))) && (!visualRangeIgnoreFakes.get() || !(event.entity instanceof FakePlayerEntity))) {
                    String prefixMsg = (prefix.get() ? Formatting.DARK_GREEN + "[" + Formatting.GREEN + "+" + Formatting.DARK_GREEN + "]" + Formatting.RESET + " " : "");
                    cLogUtils.sendMessage(prefixMsg + event.entity.getName().getString() + Formatting.GRAY + " entered your visual range.", true);
                }
                else {
                    if (bussin.get()){
                        cLogUtils.sendMessage(Formatting.GREEN + event.entity.getName().getString(), true);
                    }
                    if (visualMakeSound.get())
                        mc.world.playSoundFromEntity(mc.player, mc.player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 3.0F, 1.0F);
                }
            }
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (!event.entity.getUuid().equals(mc.player.getUuid())) {
            if (event.entity instanceof PlayerEntity) {
                if ((!visualRangeIgnoreFriends.get() || !Friends.get().isFriend(((PlayerEntity) event.entity))) && (!visualRangeIgnoreFakes.get() || !(event.entity instanceof FakePlayerEntity))) {
                    String prefixMsg = (prefix.get() ? Formatting.DARK_RED + "[" + Formatting.RED + "-" + Formatting.DARK_RED + "]" + Formatting.RESET + " " : "");
                    cLogUtils.sendMessage(prefixMsg + event.entity.getName().getString() + Formatting.GRAY + " left your visual range.", true);
                }
                else {
                    if (bussin.get()){
                        cLogUtils.sendMessage(Formatting.RED + event.entity.getName().getString(), true);
                    }
                    if (visualMakeSound.get()) mc.world.playSoundFromEntity(mc.player, mc.player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 3.0F, 1.0F);
                }
            }
        }
    }

}

package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.PebblitEntity;
import com.kruemblegard.registry.ModMobEffects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PebblitShoulderEvents {
    private PebblitShoulderEvents() {}

    private static final int EFFECT_REFRESH_TICKS = 20 * 3;
    private static final int EFFECT_MIN_REAPPLY_REMAINING = 20;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        if (player.level().isClientSide) {
            return;
        }

        boolean hasPebblitOnShoulder = player.getPassengers().stream().anyMatch(e -> e instanceof PebblitEntity);

        if (hasPebblitOnShoulder) {
            MobEffectInstance existing = player.getEffect(ModMobEffects.PEBBLIT_SHOULDER.get());
            if (existing == null || existing.getDuration() < EFFECT_MIN_REAPPLY_REMAINING) {
                // Ambient + no particles + show icon.
                player.addEffect(new MobEffectInstance(ModMobEffects.PEBBLIT_SHOULDER.get(), EFFECT_REFRESH_TICKS, 0, true, false, true));
            }
        } else {
            if (player.hasEffect(ModMobEffects.PEBBLIT_SHOULDER.get())) {
                player.removeEffect(ModMobEffects.PEBBLIT_SHOULDER.get());
            }
        }
    }

}

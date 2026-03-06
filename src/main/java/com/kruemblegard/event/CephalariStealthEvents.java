package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;

import net.minecraft.world.entity.Mob;

import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Make Cephalari undetectable as attack targets to other mobs.
 *
 * This is intentionally narrow (targeting only): Cephalari should still be interactable by players
 * (trading, breeding, etc.) but should not be selected as a mob attack target.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CephalariStealthEvents {
    private CephalariStealthEvents() {}

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (!(event.getNewTarget() instanceof CephalariEntity)) {
            return;
        }

        mob.setTarget(null);
        event.setCanceled(true);
    }
}

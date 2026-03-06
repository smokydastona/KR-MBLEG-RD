package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariZombieEntity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;

import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Links damage/healing between a Zombified Cephalari and its zombie-type mount.
 *
 * The mount side is vanilla, so we mirror via events and avoid bounce-back loops.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CephalariZombieMountLinkEvents {
    private CephalariZombieMountLinkEvents() {}

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (!(mob instanceof Zombie || mob instanceof Husk || mob instanceof Drowned || mob instanceof ZombieVillager || mob instanceof ZombifiedPiglin)) {
            return;
        }

        if (!(mob.getFirstPassenger() instanceof CephalariZombieEntity cephalari) || !cephalari.isAlive()) {
            return;
        }

        if (cephalari.isForwardingLinkedDamage()) {
            return;
        }

        float amount = event.getAmount();
        if (amount <= 0.0F) {
            return;
        }

        cephalari.hurtLinkedFromMount(event.getSource(), amount);
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (!(mob instanceof Zombie || mob instanceof Husk || mob instanceof Drowned || mob instanceof ZombieVillager || mob instanceof ZombifiedPiglin)) {
            return;
        }

        if (!(mob.getFirstPassenger() instanceof CephalariZombieEntity cephalari) || !cephalari.isAlive()) {
            return;
        }

        if (cephalari.isForwardingLinkedDamage()) {
            return;
        }

        float amount = event.getAmount();
        if (amount <= 0.0F) {
            return;
        }

        cephalari.healLinkedFromMount(amount);
    }
}

package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.adultform.CephalariAdultFormEntity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.Zoglin;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ensure Cephalari adult forms are treated like villager targets by zombie-type mobs.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CephalariAdultFormTargetingEvents {
    private CephalariAdultFormTargetingEvents() {}

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (!(mob instanceof Zombie || mob instanceof Husk || mob instanceof Drowned || mob instanceof ZombieVillager || mob instanceof ZombifiedPiglin || mob instanceof Zoglin)) {
            return;
        }

        // Add a villager-like target selector for the custom adult-form entity.
        mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, CephalariAdultFormEntity.class, true));
    }
}

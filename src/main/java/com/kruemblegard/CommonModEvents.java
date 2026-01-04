package com.kruemblegard;

import com.kruemblegard.entity.KruemblegardBossEntity;
import com.kruemblegard.entity.TraprockEntity;
import com.kruemblegard.registry.ModEntities;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonModEvents {
    private CommonModEvents() {}

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.KRUEMBLEGARD.get(), KruemblegardBossEntity.createAttributes().build());
        event.put(ModEntities.TRAPROCK.get(), TraprockEntity.createAttributes().build());
    }
}

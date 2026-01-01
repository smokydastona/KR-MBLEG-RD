package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.client.render.GenericProjectileRenderer;
import com.kruemblegard.registry.ModProjectileEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(ModProjectileEntities.RUNE_BOLT.get(),
                ctx -> new GenericProjectileRenderer<>(ctx, 0.4f));

        event.registerEntityRenderer(ModProjectileEntities.METEOR_ARM.get(),
                ctx -> new GenericProjectileRenderer<>(ctx, 1.0f));

        event.registerEntityRenderer(ModProjectileEntities.ARCANE_STORM.get(),
                ctx -> new GenericProjectileRenderer<>(ctx, 0.5f));
    }
}

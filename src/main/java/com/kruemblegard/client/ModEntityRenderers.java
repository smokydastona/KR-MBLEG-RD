package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.client.render.GenericProjectileRenderer;
import com.kruemblegard.client.render.KruemblegardPhase1BoltRenderer;
import com.kruemblegard.client.render.KruemblegardPhase2BoltRenderer;
import com.kruemblegard.client.render.KruemblegardPhase3MeteorRenderer;
import com.kruemblegard.client.render.KruemblegardPhase4BeamBoltRenderer;
import com.kruemblegard.registry.ModProjectileEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(ModProjectileEntities.PHASE1_BOLT.get(), KruemblegardPhase1BoltRenderer::new);
        event.registerEntityRenderer(ModProjectileEntities.PHASE2_BOLT.get(), KruemblegardPhase2BoltRenderer::new);
        event.registerEntityRenderer(ModProjectileEntities.PHASE3_METEOR.get(), KruemblegardPhase3MeteorRenderer::new);
        event.registerEntityRenderer(ModProjectileEntities.PHASE4_BEAM_BOLT.get(), KruemblegardPhase4BeamBoltRenderer::new);

        event.registerEntityRenderer(ModProjectileEntities.RUNE_BOLT.get(),
                ctx -> new GenericProjectileRenderer<>(ctx, 0.4f));

        event.registerEntityRenderer(ModProjectileEntities.METEOR_ARM.get(),
                ctx -> new GenericProjectileRenderer<>(ctx, 1.0f));

        event.registerEntityRenderer(ModProjectileEntities.ARCANE_STORM.get(),
                ctx -> new GenericProjectileRenderer<>(ctx, 0.5f));
    }
}

package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.client.render.KruemblegardBossRenderer;
import com.kruemblegard.client.render.PebblitRenderer;
import com.kruemblegard.client.render.TraprockRenderer;
import com.kruemblegard.registry.ModEntities;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KruemblegardClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.KRUEMBLEGARD.get(), KruemblegardBossRenderer::new);
        event.registerEntityRenderer(ModEntities.TRAPROCK.get(), TraprockRenderer::new);
        event.registerEntityRenderer(ModEntities.PEBBLIT.get(), PebblitRenderer::new);
    }
}

package com.smoky.krumblegard.client;

import com.smoky.krumblegard.KrumblegardMod;
import com.smoky.krumblegard.client.render.KrumblegardRenderer;
import com.smoky.krumblegard.init.ModEntities;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KrumblegardMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.KRUMBLEGARD.get(), KrumblegardRenderer::new);
    }
}

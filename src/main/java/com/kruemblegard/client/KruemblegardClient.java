package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.client.particle.ArcaneSparkParticle;
import com.kruemblegard.client.render.FaultCrawlerRenderer;
import com.kruemblegard.client.render.GreatHungerRenderer;
import com.kruemblegard.client.render.KruemblegardBoatRenderer;
import com.kruemblegard.client.render.KruemblegardBossRenderer;
import com.kruemblegard.client.render.KruemblegardChestBoatRenderer;
import com.kruemblegard.client.render.MoogloomRenderer;
import com.kruemblegard.client.render.PebblitRenderer;
import com.kruemblegard.client.render.ScaralonBeetleRenderer;
import com.kruemblegard.client.render.ScatteredEndermanRenderer;
import com.kruemblegard.client.render.TraprockRenderer;
import com.kruemblegard.client.render.WyrdwingRenderer;
import com.kruemblegard.client.render.layer.PebblitShoulderLayer;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.registry.ModParticles;

import net.minecraft.world.level.GrassColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KruemblegardClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.KRUEMBLEGARD.get(), KruemblegardBossRenderer::new);
        event.registerEntityRenderer(ModEntities.TRAPROCK.get(), TraprockRenderer::new);
        event.registerEntityRenderer(ModEntities.PEBBLIT.get(), PebblitRenderer::new);
        event.registerEntityRenderer(ModEntities.GREAT_HUNGER.get(), GreatHungerRenderer::new);
        event.registerEntityRenderer(ModEntities.SCATTERED_ENDERMAN.get(), ScatteredEndermanRenderer::new);
        event.registerEntityRenderer(ModEntities.MOOGLOOM.get(), MoogloomRenderer::new);
        event.registerEntityRenderer(ModEntities.FAULT_CRAWLER.get(), FaultCrawlerRenderer::new);
        event.registerEntityRenderer(ModEntities.SCARALON_BEETLE.get(), ScaralonBeetleRenderer::new);
        event.registerEntityRenderer(ModEntities.WYRDWING.get(), WyrdwingRenderer::new);

        event.registerEntityRenderer(ModEntities.KRUEMBLEGARD_BOAT.get(), KruemblegardBoatRenderer::new);
        event.registerEntityRenderer(ModEntities.KRUEMBLEGARD_CHEST_BOAT.get(), KruemblegardChestBoatRenderer::new);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer defaultRenderer = event.getSkin("default");
        if (defaultRenderer != null) {
            defaultRenderer.addLayer(new PebblitShoulderLayer(defaultRenderer));
        }

        PlayerRenderer slimRenderer = event.getSkin("slim");
        if (slimRenderer != null) {
            slimRenderer.addLayer(new PebblitShoulderLayer(slimRenderer));
        }
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.ARCANE_SPARK.get(), ArcaneSparkParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, tintIndex) -> PaleweftTint.stitchedGrassColor(level, pos),
                ModBlocks.PALEWEFT_GRASS.get(),
                ModBlocks.PALEWEFT_TALL_GRASS.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> GrassColor.getDefaultColor(),
                ModBlocks.PALEWEFT_GRASS.get().asItem(),
                ModBlocks.PALEWEFT_TALL_GRASS.get().asItem()
        );
    }
}

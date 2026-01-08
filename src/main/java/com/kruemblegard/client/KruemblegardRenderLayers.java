package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class KruemblegardRenderLayers {
    private KruemblegardRenderLayers() {}

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var entry : ModBlocks.BLOCKS.getEntries()) {
                var block = entry.get();

                if (block instanceof LeavesBlock) {
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped());
                } else if (block instanceof BushBlock) {
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
                }
            }
        });
    }
}

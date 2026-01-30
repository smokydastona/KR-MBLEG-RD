package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.block.AshveilBlock;
import com.kruemblegard.block.KruemblegardLeavesBlock;
import com.kruemblegard.block.WaylilyBlock;
import com.kruemblegard.init.ModBlocks;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;

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

                if (block instanceof LeavesBlock || block instanceof KruemblegardLeavesBlock) {
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped());
                } else if (block instanceof VineBlock) {
                    // Match vanilla vines: cutout (not solid), so alpha pixels don't render black.
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
                } else if (block == ModBlocks.STRING_FRANCH.get()) {
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
                } else if (block == ModBlocks.PYROKELP.get() || block == ModBlocks.PYROKELP_PLANT.get()) {
                    // Pyrokelp is a growing-plant (head/body) like twisting vines; it must be cutout for transparency.
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
                } else if (block instanceof AshveilBlock) {
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
                } else if (block instanceof WaylilyBlock) {
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
                } else if (block instanceof BushBlock) {
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
                }
            }
        });
    }
}

package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.food.FoodProperties;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModComposting {
    private ModComposting() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModComposting::registerCompostables);
    }

    private static void registerCompostables() {
        int added = 0;

        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
            if (key == null || !Kruemblegard.MOD_ID.equals(key.getNamespace())) {
                continue;
            }

            Item item = block.asItem();
            if (item == Items.AIR) {
                continue;
            }

            Float chance = getCompostChance(block);
            if (chance == null) {
                continue;
            }

            if (ComposterBlock.COMPOSTABLES.containsKey(item)) {
                continue;
            }

            ComposterBlock.COMPOSTABLES.put(item, chance);
            added++;
        }

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
            if (key == null || !Kruemblegard.MOD_ID.equals(key.getNamespace())) {
                continue;
            }

            if (!item.isEdible()) {
                continue;
            }

            FoodProperties food = item.getFoodProperties(item.getDefaultInstance(), null);
            if (food != null && food.isMeat()) {
                continue;
            }

            if (ComposterBlock.COMPOSTABLES.containsKey(item)) {
                continue;
            }

            // Vanilla sweet berries compost at 0.3; keep plant foods in that ballpark.
            ComposterBlock.COMPOSTABLES.put(item, 0.3F);
            added++;
        }

        Kruemblegard.LOGGER.info("Registered {} Kruemblegard compostables", added);
    }

    private static Float getCompostChance(Block block) {
        if (block instanceof DoublePlantBlock) {
            return 0.85F;
        }

        if (block instanceof FlowerBlock || block instanceof MushroomBlock) {
            return 0.65F;
        }

        if (block instanceof SweetBerryBushBlock || block instanceof VineBlock) {
            return 0.5F;
        }

        if (block instanceof TallGrassBlock) {
            return 0.3F;
        }

        if (block instanceof SaplingBlock || block instanceof LeavesBlock) {
            return 0.3F;
        }

        if (block instanceof BushBlock) {
            return 0.65F;
        }

        return null;
    }
}

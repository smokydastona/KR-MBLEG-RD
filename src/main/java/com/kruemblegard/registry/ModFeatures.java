package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.world.feature.WayfallSimpleTreeFeature;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    private ModFeatures() {}

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, Kruemblegard.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ASHBLOOM_TREE = FEATURES.register(
            "ashbloom_tree",
            () -> new WayfallSimpleTreeFeature(NoneFeatureConfiguration.CODEC, ModBlocks.ASHBLOOM_LOG::get, ModBlocks.ASHBLOOM_LEAVES::get)
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GLIMMERPINE_TREE = FEATURES.register(
            "glimmerpine_tree",
            () -> new WayfallSimpleTreeFeature(NoneFeatureConfiguration.CODEC, ModBlocks.GLIMMERPINE_LOG::get, ModBlocks.GLIMMERPINE_LEAVES::get)
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> DRIFTWOOD_TREE = FEATURES.register(
            "driftwood_tree",
            () -> new WayfallSimpleTreeFeature(NoneFeatureConfiguration.CODEC, ModBlocks.DRIFTWOOD_LOG::get, ModBlocks.DRIFTWOOD_LEAVES::get)
    );

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}

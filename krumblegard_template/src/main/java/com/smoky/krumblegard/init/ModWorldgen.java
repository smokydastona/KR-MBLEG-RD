package com.smoky.krumblegard.init;

import java.util.List;

import com.smoky.krumblegard.KrumblegardMod;
import com.smoky.krumblegard.config.ModConfig;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModWorldgen {
    private ModWorldgen() {}

    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, KrumblegardMod.MODID);

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registries.PLACED_FEATURE, KrumblegardMod.MODID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> FALSE_WAYSTONE_CONFIGURED = CONFIGURED_FEATURES.register(
            "false_waystone",
            () -> new ConfiguredFeature<>(
                    Feature.SIMPLE_BLOCK,
                    new SimpleBlockConfiguration(SimpleStateProvider.simple(ModBlocks.FALSE_WAYSTONE.get().defaultBlockState()))
            )
    );

    public static final RegistryObject<PlacedFeature> FALSE_WAYSTONE_PLACED = PLACED_FEATURES.register(
            "false_waystone",
            () -> {
                // Note: config values are read when the PlacedFeature is constructed (restart required).
                boolean enabled = ModConfig.WAYSTONE_ENABLED.get();
                int rarity = Math.max(1, ModConfig.WAYSTONE_RARITY.get());

                Holder<ConfiguredFeature<?, ?>> configured = FALSE_WAYSTONE_CONFIGURED.getHolder().orElseThrow();

                List<PlacementModifier> modifiers = List.of(
                        enabled ? RarityFilter.onAverageOnceEvery(rarity) : CountPlacement.of(0),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                );

                return new PlacedFeature(configured, modifiers);
            }
    );

    public static void register(IEventBus bus) {
        CONFIGURED_FEATURES.register(bus);
        PLACED_FEATURES.register(bus);
    }
}

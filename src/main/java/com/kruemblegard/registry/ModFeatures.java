package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.world.feature.WayfallDeepLakeConfiguration;
import com.kruemblegard.world.feature.WayfallDeepLakeFeature;
import com.kruemblegard.world.feature.GlimmerpineMegaSchematicFeature;
import com.kruemblegard.world.feature.GlimmerpineSchematicConfiguration;
import com.kruemblegard.world.feature.GlimmerpineSchematicFeature;
import com.kruemblegard.world.feature.GiantMushroomSchematicConfiguration;
import com.kruemblegard.world.feature.GiantMushroomSchematicFeature;
import com.kruemblegard.world.feature.RockSchematicConfiguration;
import com.kruemblegard.world.feature.RockSchematicFeature;
import com.kruemblegard.world.feature.WayfallSimpleTreeFeature;
import com.kruemblegard.world.feature.EchowoodMegaSchematicFeature;
import com.kruemblegard.world.feature.EchowoodSchematicConfiguration;
import com.kruemblegard.world.feature.EchowoodSchematicFeature;
import com.kruemblegard.world.feature.WayrootMegaSchematicFeature;
import com.kruemblegard.world.feature.WayrootSchematicConfiguration;
import com.kruemblegard.world.feature.WayrootSchematicFeature;

import net.minecraft.resources.ResourceLocation;
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

    public static final RegistryObject<Feature<GlimmerpineSchematicConfiguration>> GLIMMERPINE_SCHEMATIC = FEATURES.register(
            "glimmerpine_schematic",
            () -> new GlimmerpineSchematicFeature(GlimmerpineSchematicConfiguration.CODEC)
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GLIMMERPINE_MEGA_SCHEMATIC_1 = FEATURES.register(
            "glimmerpine_mega_schematic_1",
            () -> new GlimmerpineMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/glimmerpine/mega_glimmerpine_1.schem")
            )
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GLIMMERPINE_MEGA_SCHEMATIC_2 = FEATURES.register(
            "glimmerpine_mega_schematic_2",
            () -> new GlimmerpineMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/glimmerpine/mega_glimmerpine_2.schem")
            )
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GLIMMERPINE_MEGA_SCHEMATIC_3 = FEATURES.register(
            "glimmerpine_mega_schematic_3",
            () -> new GlimmerpineMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/glimmerpine/mega_glimmerpine_3.schem")
            )
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> DRIFTWOOD_TREE = FEATURES.register(
            "driftwood_tree",
            () -> new WayfallSimpleTreeFeature(NoneFeatureConfiguration.CODEC, ModBlocks.DRIFTWOOD_LOG::get, ModBlocks.DRIFTWOOD_LEAVES::get)
    );

    public static final RegistryObject<Feature<WayfallDeepLakeConfiguration>> WAYFALL_DEEP_LAKE = FEATURES.register(
            "wayfall_deep_lake",
            () -> new WayfallDeepLakeFeature(WayfallDeepLakeConfiguration.CODEC)
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> WAYROOT_MEGA_SCHEMATIC_1 = FEATURES.register(
            "wayroot_mega_schematic_1",
            () -> new WayrootMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/wayroot/mega_wayroot_1.schem")
            )
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> WAYROOT_MEGA_SCHEMATIC_2 = FEATURES.register(
            "wayroot_mega_schematic_2",
            () -> new WayrootMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/wayroot/mega_wayroot_2.schem")
            )
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> WAYROOT_MEGA_SCHEMATIC_3 = FEATURES.register(
            "wayroot_mega_schematic_3",
            () -> new WayrootMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/wayroot/mega_wayroot_3.schem")
            )
    );

    public static final RegistryObject<Feature<WayrootSchematicConfiguration>> WAYROOT_SCHEMATIC = FEATURES.register(
            "wayroot_schematic",
            () -> new WayrootSchematicFeature(WayrootSchematicConfiguration.CODEC)
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ECHOWOOD_MEGA_SCHEMATIC_1 = FEATURES.register(
            "echowood_mega_schematic_1",
            () -> new EchowoodMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/echowood/mega_echowood_1.schem")
            )
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ECHOWOOD_MEGA_SCHEMATIC_2 = FEATURES.register(
            "echowood_mega_schematic_2",
            () -> new EchowoodMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/echowood/mega_echowood_2.schem")
            )
    );

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ECHOWOOD_MEGA_SCHEMATIC_3 = FEATURES.register(
            "echowood_mega_schematic_3",
            () -> new EchowoodMegaSchematicFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ResourceLocation(Kruemblegard.MOD_ID, "schematics/echowood/mega_echowood_3.schem")
            )
    );

    public static final RegistryObject<Feature<EchowoodSchematicConfiguration>> ECHOWOOD_SCHEMATIC = FEATURES.register(
            "echowood_schematic",
            () -> new EchowoodSchematicFeature(EchowoodSchematicConfiguration.CODEC)
    );

    public static final RegistryObject<Feature<GiantMushroomSchematicConfiguration>> GIANT_MUSHROOM_SCHEMATIC = FEATURES.register(
            "giant_mushroom_schematic",
            () -> new GiantMushroomSchematicFeature(GiantMushroomSchematicConfiguration.CODEC)
    );

    public static final RegistryObject<Feature<RockSchematicConfiguration>> ROCK_SCHEMATIC = FEATURES.register(
            "rock_schematic",
            () -> new RockSchematicFeature(RockSchematicConfiguration.CODEC)
    );

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}

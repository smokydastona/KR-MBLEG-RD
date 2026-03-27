package com.kruemblegard;

import com.kruemblegard.config.ModConfig;
import com.kruemblegard.config.ClientConfig;
import com.kruemblegard.config.worldgen.WorldgenTuningConfig;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.init.ModCreativeTabs;
import com.kruemblegard.init.ModCriteria;
import com.kruemblegard.init.ModVillagers;
import com.kruemblegard.network.ModNetworking;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.registry.ModEnchantments;
import com.kruemblegard.registry.ModFeatures;
import com.kruemblegard.registry.ModFlammability;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.registry.ModLootModifiers;
import com.kruemblegard.registry.ModMobEffects;
import com.kruemblegard.registry.ModParticles;
import com.kruemblegard.registry.ModPotions;
import com.kruemblegard.registry.ModProjectileEntities;
import com.kruemblegard.registry.ModSounds;
import com.kruemblegard.registry.ModStructures;
import com.kruemblegard.registry.ModWoodTypes;

import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.CreativeModeTabs;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.kruemblegard.worldgen.terrablender.KruemblegardTerraBlender;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import software.bernie.geckolib.GeckoLib;

@Mod(Kruemblegard.MOD_ID)
public final class Kruemblegard {
    public static final String MOD_ID = "kruemblegard";
    public static final String MODID = MOD_ID;

    public static final Logger LOGGER = LogUtils.getLogger();

    public Kruemblegard() {
        GeckoLib.initialize();

        ModNetworking.init();

        ModWoodTypes.register();

        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(Type.CLIENT, ClientConfig.CLIENT_SPEC);

        ModCriteria.register();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.register(modBus);
        ModBlockEntities.register(modBus);
        ModCreativeTabs.register(modBus);
        ModVillagers.register(modBus);

        ModEntities.ENTITIES.register(modBus);
        ModEnchantments.ENCHANTMENTS.register(modBus);
        ModProjectileEntities.PROJECTILES.register(modBus);
        ModFeatures.register(modBus);
        ModStructures.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modBus);
        ModParticles.PARTICLES.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModPotions.POTIONS.register(modBus);
        ModMobEffects.MOB_EFFECTS.register(modBus);

        modBus.addListener(this::addCreative);
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Load the external worldgen tuning file early so TerraBlender region weights
            // and strict validation behavior are available during world init.
            WorldgenTuningConfig.loadAndSync();
            KruemblegardTerraBlender.register();
            ModFlammability.registerFlammables();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ModEnchantments.TELEKINESIS.get(), 1)));
        }
    }
}

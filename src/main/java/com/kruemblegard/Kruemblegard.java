package com.kruemblegard;

import com.kruemblegard.config.ModConfig;
import com.kruemblegard.item.CrumblingCodexItem;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.init.ModCreativeTabs;
import com.kruemblegard.init.ModCriteria;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.registry.ModLootModifiers;
import com.kruemblegard.registry.ModParticles;
import com.kruemblegard.registry.ModProjectileEntities;
import com.kruemblegard.registry.ModSounds;

import net.minecraft.world.item.CreativeModeTabs;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import software.bernie.geckolib.GeckoLib;

@Mod(Kruemblegard.MOD_ID)
public final class Kruemblegard {
    public static final String MOD_ID = "kruemblegard";
    public static final String MODID = MOD_ID;

    public Kruemblegard() {
        GeckoLib.initialize();

        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);

        ModCriteria.register();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.register(modBus);
        ModCreativeTabs.register(modBus);
        ModBlockEntities.register(modBus);

        ModEntities.ENTITIES.register(modBus);
        ModProjectileEntities.PROJECTILES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modBus);
        ModParticles.PARTICLES.register(modBus);
        ModSounds.SOUNDS.register(modBus);

        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.HAUNTED_WAYSTONE_ITEM);
            event.accept(ModItems.ANCIENT_WAYSTONE_ITEM);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.STANDING_STONE_ITEM);
            event.accept(ModItems.ATTUNED_STONE_ITEM);
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ATTUNED_RUNE_SHARD);
            event.accept(ModItems.RUNIC_CORE);
        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(CrumblingCodexItem.createFilledStack(ModItems.CRUMBLING_CODEX.get()));
            event.accept(ModItems.RUNIC_PICKAXE);
            event.accept(ModItems.RUNIC_AXE);
            event.accept(ModItems.RUNIC_SHOVEL);
            event.accept(ModItems.RUNIC_HOE);
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.RUNIC_SWORD);
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            // Added via ModItems event subscriber.
        }
    }
}

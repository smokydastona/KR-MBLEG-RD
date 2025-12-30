package com.smoky.krumblegard;

import com.smoky.krumblegard.config.ModConfig;
import com.smoky.krumblegard.init.ModBlockEntities;
import com.smoky.krumblegard.init.ModBlocks;
import com.smoky.krumblegard.init.ModCriteria;
import com.smoky.krumblegard.init.ModEntities;
import com.smoky.krumblegard.init.ModItems;
import com.smoky.krumblegard.init.ModSounds;

import net.minecraft.world.item.CreativeModeTabs;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import software.bernie.geckolib.GeckoLib;

@Mod(KrumblegardMod.MODID)
public final class KrumblegardMod {
    public static final String MODID = "krumblegard";

    public KrumblegardMod() {
        GeckoLib.initialize();

        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);

        ModCriteria.register();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModEntities.register(modBus);
        ModBlockEntities.register(modBus);
        ModSounds.register(modBus);

        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.HAUNTED_WAYSTONE_ITEM);
            event.accept(ModItems.FALSE_WAYSTONE_ITEM);
            event.accept(ModItems.ANCIENT_WAYSTONE_ITEM);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.STANDING_STONE_ITEM);
            event.accept(ModItems.ATTUNED_STONE_ITEM);
        }
    }
}

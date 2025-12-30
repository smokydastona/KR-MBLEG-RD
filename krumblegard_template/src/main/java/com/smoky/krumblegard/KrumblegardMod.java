package com.smoky.krumblegard;

import com.smoky.krumblegard.config.ModConfig;
import com.smoky.krumblegard.init.ModBlockEntities;
import com.smoky.krumblegard.init.ModBlocks;
import com.smoky.krumblegard.init.ModCriteria;
import com.smoky.krumblegard.init.ModEntities;
import com.smoky.krumblegard.init.ModItems;
import com.smoky.krumblegard.init.ModSounds;
import com.smoky.krumblegard.init.ModWorldgen;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.eventbus.api.IEventBus;
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
        ModWorldgen.register(modBus);
    }
}

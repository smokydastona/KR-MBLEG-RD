package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.effect.PebblitShoulderEffect;

import net.minecraft.world.effect.MobEffect;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMobEffects {
    private ModMobEffects() {}

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Kruemblegard.MOD_ID);

    public static final RegistryObject<MobEffect> PEBBLIT_SHOULDER = MOB_EFFECTS.register(
            "pebblit_shoulder",
            PebblitShoulderEffect::new
    );
}

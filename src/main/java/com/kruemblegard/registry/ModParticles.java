package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.core.particles.SimpleParticleType;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<SimpleParticleType> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Kruemblegard.MOD_ID);

    // Example placeholder for future arcane particles:
    public static final RegistryObject<SimpleParticleType> ARCANE_SPARK =
            PARTICLES.register("arcane_spark", () -> new SimpleParticleType(true));
}

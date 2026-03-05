package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

        public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Kruemblegard.MOD_ID);

    // Example placeholder for future arcane particles:
    public static final RegistryObject<SimpleParticleType> ARCANE_SPARK =
            PARTICLES.register("arcane_spark", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CEPHALARI_SHELL_DUST =
            PARTICLES.register("cephalari_shell_dust", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CEPHALARI_SHELL_FRAGMENT =
            PARTICLES.register("cephalari_shell_fragment", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CEPHALARI_SHELL_SPIRAL =
            PARTICLES.register("cephalari_shell_spiral", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CEPHALARI_ZOMBIFY =
            PARTICLES.register("cephalari_zombify", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CEPHALARI_CURE =
            PARTICLES.register("cephalari_cure", () -> new SimpleParticleType(true));
}

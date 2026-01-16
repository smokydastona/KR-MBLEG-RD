package com.kruemblegard.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ClientConfig {
    private ClientConfig() {}

    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_DISTANCE_CULLED_COSMETICS;
    public static final ForgeConfigSpec.IntValue COSMETIC_CULL_DISTANCE_BLOCKS;
    public static final ForgeConfigSpec.DoubleValue COSMETIC_VERTICAL_STRETCH;

    public static final ForgeConfigSpec.IntValue PROJECTILE_PARTICLE_SPAWN_INTERVAL_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Kruemblegard");
        builder.push("Performance");
        builder.push("Client");

        ENABLE_DISTANCE_CULLED_COSMETICS = builder
            .comment(
                "Enable cheap distance-based culling for purely cosmetic effects (particles/local ambience).",
                "This is inspired by distance-culling mods like betterfpsdistances, but only affects Krümblegård cosmetics.",
                "If you run into visual issues with other render mods, set this to false."
            )
            .define("enableDistanceCulledCosmetics", true);

        COSMETIC_CULL_DISTANCE_BLOCKS = builder
            .comment(
                "Maximum distance (blocks) at which Krümblegård cosmetic effects are spawned.",
                "Lower values reduce client work; higher values increase visible range."
            )
            .defineInRange("cosmeticCullDistanceBlocks", 64, 8, 512);

        COSMETIC_VERTICAL_STRETCH = builder
            .comment(
                "Vertical distance stretch factor for culling decisions.",
                "Effective distance uses: dx^2 + dz^2 + (dy * stretch)^2.",
                "1.0 means normal spherical distance; >1.0 culls more aggressively above/below the player."
            )
            .defineInRange("cosmeticVerticalStretch", 1.0D, 0.1D, 10.0D);

        PROJECTILE_PARTICLE_SPAWN_INTERVAL_TICKS = builder
            .comment(
                "Spawn interval (ticks) for projectile trail particles.",
                "1 = every tick (vanilla-like). Higher values reduce particles and improve FPS."
            )
            .defineInRange("projectileParticleSpawnIntervalTicks", 1, 1, 20);

        builder.pop();
        builder.pop();
        builder.pop();

        CLIENT_SPEC = builder.build();
    }
}

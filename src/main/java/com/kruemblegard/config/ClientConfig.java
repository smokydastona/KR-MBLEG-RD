package com.kruemblegard.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ClientConfig {
    private ClientConfig() {}

    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_DISTANCE_CULLED_COSMETICS;
    public static final ForgeConfigSpec.IntValue COSMETIC_CULL_DISTANCE_BLOCKS;
    public static final ForgeConfigSpec.DoubleValue COSMETIC_VERTICAL_STRETCH;

    public static final ForgeConfigSpec.DoubleValue COSMETIC_CULL_EPSILON_BLOCKS;

    public static final ForgeConfigSpec.BooleanValue ENABLE_VIEW_CONE_CULLED_COSMETICS;
    public static final ForgeConfigSpec.DoubleValue COSMETIC_VIEW_CONE_HALF_ANGLE_DEGREES;
    public static final ForgeConfigSpec.DoubleValue COSMETIC_VIEW_CONE_MARGIN_DEGREES;

    public static final ForgeConfigSpec.BooleanValue ENABLE_COSMETIC_PARTICLE_BUDGET;
    public static final ForgeConfigSpec.IntValue COSMETIC_PARTICLE_BUDGET_PER_TICK;

    public static final ForgeConfigSpec.BooleanValue ENABLE_RUNTIME_CHECKS;

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

        COSMETIC_CULL_EPSILON_BLOCKS = builder
            .comment(
                "Extra slack (blocks) added to cosmetic culling decisions.",
                "This is a conservative buffer to reduce flicker/false culls caused by movement and floating point imprecision.",
                "Typical values are small (e.g., 0.125)."
            )
            .defineInRange("cosmeticCullEpsilonBlocks", 0.125D, 0.0D, 2.0D);

        ENABLE_VIEW_CONE_CULLED_COSMETICS = builder
            .comment(
                "Enable an additional view-cone early-out for purely cosmetic effects.",
                "This is a cheap " +
                    "approximation of frustum culling: if an effect is far outside the player's look direction, we can skip spawning its particles.",
                "Disabled by default because some players prefer to see effects even when looking away."
            )
            .define("enableViewConeCulledCosmetics", false);

        COSMETIC_VIEW_CONE_HALF_ANGLE_DEGREES = builder
            .comment(
                "Half-angle (degrees) for the cosmetic view cone.",
                "Larger values are more permissive (less culling).",
                "This is intentionally not tied to the user's dynamic FOV to avoid client-only dependencies in common code."
            )
            .defineInRange("cosmeticViewConeHalfAngleDegrees", 100.0D, 10.0D, 180.0D);

        COSMETIC_VIEW_CONE_MARGIN_DEGREES = builder
            .comment(
                "Extra margin (degrees) added to the cosmetic view cone for conservative culling.",
                "Higher values reduce false culls at the cost of doing more work."
            )
            .defineInRange("cosmeticViewConeMarginDegrees", 15.0D, 0.0D, 90.0D);

        ENABLE_COSMETIC_PARTICLE_BUDGET = builder
            .comment(
                "Enable a per-tick budget for Krümblegård cosmetic particle spawning.",
                "This caps worst-case particle work when many cosmetic effects are active at once, reducing micro-stutter.",
                "If you prefer maximum visuals over stability, set this to false."
            )
            .define("enableCosmeticParticleBudget", true);

        COSMETIC_PARTICLE_BUDGET_PER_TICK = builder
            .comment(
                "Maximum Krümblegård cosmetic particles to spawn per client tick.",
                "0 = unlimited (budget effectively disabled even if enabled).",
                "This budget is only applied to Krümblegård cosmetics, not vanilla/modded particles globally."
            )
            .defineInRange("cosmeticParticleBudgetPerTick", 512, 0, 10000);

        ENABLE_RUNTIME_CHECKS = builder
            .comment(
                "Enable extra runtime validation for Krümblegård performance helpers.",
                "Primarily useful for mod development/debugging.",
                "You can also force checks on via JVM flag: -Dkruemblegard.checks=true"
            )
            .define("enableRuntimeChecks", false);

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

package com.smoky.krumblegard.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {
    private ModConfig() {}

    public static final ForgeConfigSpec COMMON_SPEC;

    public static final ForgeConfigSpec.BooleanValue WAYSTONE_ENABLED;
    public static final ForgeConfigSpec.IntValue WAYSTONE_RARITY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Krumblegard");

        WAYSTONE_ENABLED = builder
                .comment("Enable False Waystone world generation")
                .define("enableWaystones", true);

        WAYSTONE_RARITY = builder
                .comment("Average chunks per False Waystone (higher = rarer)")
                .defineInRange("waystoneRarity", 800, 10, 5000);

        builder.pop();

        COMMON_SPEC = builder.build();
    }
}

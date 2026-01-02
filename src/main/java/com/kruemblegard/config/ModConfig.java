package com.kruemblegard.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {
    private ModConfig() {}

    public static final ForgeConfigSpec COMMON_SPEC;

    public static final ForgeConfigSpec.BooleanValue WAYSTONE_ENABLED;
    public static final ForgeConfigSpec.IntValue WAYSTONE_RARITY;

    public static final ForgeConfigSpec.DoubleValue BOSS_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue BOSS_ARMOR;
    public static final ForgeConfigSpec.DoubleValue BOSS_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.DoubleValue BOSS_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue BOSS_ATTACK_KNOCKBACK;
    public static final ForgeConfigSpec.DoubleValue BOSS_REGEN_PER_TICK;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Kruemblegard");

        WAYSTONE_ENABLED = builder
                .comment("Enable False Waystone world generation")
                .define("enableWaystones", true);

        WAYSTONE_RARITY = builder
                .comment("Average chunks per False Waystone (higher = rarer)")
                .defineInRange("waystoneRarity", 800, 10, 5000);

        builder.push("Boss");

        BOSS_MAX_HEALTH = builder
            .comment("Krümblegård boss max health")
            .defineInRange("bossMaxHealth", 1200.0D, 1.0D, 1000000.0D);

        BOSS_ARMOR = builder
            .comment("Krümblegård boss armor")
            .defineInRange("bossArmor", 18.0D, 0.0D, 1000.0D);

        BOSS_ARMOR_TOUGHNESS = builder
            .comment("Krümblegård boss armor toughness")
            .defineInRange("bossArmorToughness", 10.0D, 0.0D, 1000.0D);

        BOSS_ATTACK_DAMAGE = builder
            .comment("Krümblegård boss base melee damage")
            .defineInRange("bossAttackDamage", 20.0D, 0.0D, 1000000.0D);

        BOSS_ATTACK_KNOCKBACK = builder
            .comment("Krümblegård boss melee knockback")
            .defineInRange("bossAttackKnockback", 1.5D, 0.0D, 1000.0D);

        BOSS_REGEN_PER_TICK = builder
            .comment("Passive regeneration per tick while engaged (0 disables). 0.1 = 2 HP/sec at 20 TPS")
            .defineInRange("bossRegenPerTick", 0.0D, 0.0D, 1000.0D);

        builder.pop();

        builder.pop();

        COMMON_SPEC = builder.build();
    }
}

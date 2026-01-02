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

    public static final ForgeConfigSpec.DoubleValue BOSS_PHASE_2_HEALTH_RATIO;
    public static final ForgeConfigSpec.DoubleValue BOSS_PHASE_3_HEALTH_RATIO;

    public static final ForgeConfigSpec.IntValue BOSS_PHASE_2_RUNE_BOLT_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_2_GRAVITIC_PULL_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_3_DASH_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_3_METEOR_ARM_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_3_ARCANE_STORM_COOLDOWN_TICKS;

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

        BOSS_PHASE_2_HEALTH_RATIO = builder
            .comment("Health ratio at/below which phase 2 starts")
            .defineInRange("bossPhase2HealthRatio", 0.70D, 0.01D, 0.99D);

        BOSS_PHASE_3_HEALTH_RATIO = builder
            .comment("Health ratio at/below which phase 3 starts")
            .defineInRange("bossPhase3HealthRatio", 0.30D, 0.01D, 0.99D);

        BOSS_PHASE_2_RUNE_BOLT_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between rune bolt attacks in phase 2")
            .defineInRange("bossPhase2RuneBoltCooldownTicks", 40, 1, 20_000);

        BOSS_PHASE_2_GRAVITIC_PULL_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between gravitic pulls in phase 2")
            .defineInRange("bossPhase2GraviticPullCooldownTicks", 60, 1, 20_000);

        BOSS_PHASE_3_DASH_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between dash attacks in phase 3")
            .defineInRange("bossPhase3DashCooldownTicks", 80, 1, 20_000);

        BOSS_PHASE_3_METEOR_ARM_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between meteor arm attacks in phase 3")
            .defineInRange("bossPhase3MeteorArmCooldownTicks", 100, 1, 20_000);

        BOSS_PHASE_3_ARCANE_STORM_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between arcane storm attacks in phase 3")
            .defineInRange("bossPhase3ArcaneStormCooldownTicks", 120, 1, 20_000);

        builder.pop();

        builder.pop();

        COMMON_SPEC = builder.build();
    }
}

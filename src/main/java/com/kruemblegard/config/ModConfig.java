package com.kruemblegard.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {
    private ModConfig() {}

    public static final ForgeConfigSpec COMMON_SPEC;

    public static final ForgeConfigSpec.BooleanValue WAYSTONE_ENABLED;
    public static final ForgeConfigSpec.IntValue WAYSTONE_RARITY;

    public static final ForgeConfigSpec.BooleanValue WAYSTONE_WAYFALL_TELEPORT_ENABLED;
    public static final ForgeConfigSpec.DoubleValue WAYSTONE_WAYFALL_TELEPORT_CHANCE;

    public static final ForgeConfigSpec.IntValue WAYFALL_INIT_TASKS_PER_TICK;
    public static final ForgeConfigSpec.IntValue WAYFALL_INIT_CHUNKS_TICKETED_PER_TICK;
    public static final ForgeConfigSpec.BooleanValue WAYFALL_DEBUG_LOGGING;

    public static final ForgeConfigSpec.DoubleValue BOSS_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue BOSS_ARMOR;
    public static final ForgeConfigSpec.DoubleValue BOSS_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.DoubleValue BOSS_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue BOSS_ATTACK_KNOCKBACK;
    public static final ForgeConfigSpec.DoubleValue BOSS_REGEN_PER_TICK;

    public static final ForgeConfigSpec.DoubleValue BOSS_PHASE_2_HEALTH_RATIO;
    public static final ForgeConfigSpec.DoubleValue BOSS_PHASE_3_HEALTH_RATIO;
    public static final ForgeConfigSpec.DoubleValue BOSS_PHASE_4_HEALTH_RATIO;

    public static final ForgeConfigSpec.IntValue BOSS_PHASE_2_RUNE_BOLT_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_2_GRAVITIC_PULL_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_3_DASH_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_3_METEOR_ARM_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_3_ARCANE_STORM_COOLDOWN_TICKS;

    public static final ForgeConfigSpec.IntValue BOSS_PHASE_1_CLEAVE_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_2_RUNE_VOLLEY_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_3_BLINK_STRIKE_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_3_METEOR_SHOWER_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_4_WHIRLWIND_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_4_ARCANE_BEAM_COOLDOWN_TICKS;

    public static final ForgeConfigSpec.IntValue BOSS_ABILITY_GLOBAL_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BOSS_PHASE_TRANSITION_BUFF_TICKS;
    public static final ForgeConfigSpec.DoubleValue BOSS_PHASE_TRANSITION_RADIUS;
    public static final ForgeConfigSpec.DoubleValue BOSS_PHASE_TRANSITION_KNOCKBACK;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Kruemblegard");

        WAYSTONE_ENABLED = builder
                .comment("Enable False Waystone world generation")
                .define("enableWaystones", true);

        WAYSTONE_RARITY = builder
                .comment("Average chunks per False Waystone (higher = rarer)")
                .defineInRange("waystoneRarity", 800, 10, 5000);

        WAYSTONE_WAYFALL_TELEPORT_ENABLED = builder
            .comment("Allow Waystones to (rarely) pull first-time players into the Wayfall on use. Hold shift to avoid.")
            .define("waystoneWayfallTeleportEnabled", true);

        WAYSTONE_WAYFALL_TELEPORT_CHANCE = builder
            .comment("Chance (0..1) per Waystone use to teleport a player who has never visited the Wayfall.")
            .defineInRange("waystoneWayfallTeleportChance", 0.02D, 0.0D, 1.0D);

        builder.push("Wayfall");

        WAYFALL_INIT_TASKS_PER_TICK = builder
            .comment(
                "Max number of queued Wayfall initialization tasks to process per server tick.",
                "Lower values reduce hitching but can make first-load take longer."
            )
            .defineInRange("wayfallInitTasksPerTick", 2, 1, 20);

        WAYFALL_INIT_CHUNKS_TICKETED_PER_TICK = builder
            .comment(
                "How many chunks to add/remove region tickets for per tick during Wayfall init.",
                "Lower values reduce hitching but can make first-load take longer."
            )
            .defineInRange("wayfallInitChunksTicketedPerTick", 2, 1, 64);

        WAYFALL_DEBUG_LOGGING = builder
            .comment("Enable extra Wayfall debug logging (chunk tickets, init task progress).")
            .define("wayfallDebugLogging", false);

        builder.pop();

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

        BOSS_PHASE_4_HEALTH_RATIO = builder
            .comment("Health ratio at/below which phase 4 starts (should be <= phase 3 threshold)")
            .defineInRange("bossPhase4HealthRatio", 0.10D, 0.01D, 0.99D);

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

        BOSS_PHASE_1_CLEAVE_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between cleave attacks in phase 1")
            .defineInRange("bossPhase1CleaveCooldownTicks", 50, 1, 20_000);

        BOSS_PHASE_2_RUNE_VOLLEY_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between rune volley attacks in phase 2")
            .defineInRange("bossPhase2RuneVolleyCooldownTicks", 70, 1, 20_000);

        BOSS_PHASE_3_BLINK_STRIKE_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between blink strike attacks in phase 3")
            .defineInRange("bossPhase3BlinkStrikeCooldownTicks", 60, 1, 20_000);

        BOSS_PHASE_3_METEOR_SHOWER_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between meteor shower attacks in phase 3")
            .defineInRange("bossPhase3MeteorShowerCooldownTicks", 140, 1, 20_000);

        BOSS_PHASE_4_WHIRLWIND_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between whirlwind attacks in phase 4")
            .defineInRange("bossPhase4WhirlwindCooldownTicks", 80, 1, 20_000);

        BOSS_PHASE_4_ARCANE_BEAM_COOLDOWN_TICKS = builder
            .comment("Base cooldown (ticks) between arcane beam attacks in phase 4")
            .defineInRange("bossPhase4ArcaneBeamCooldownTicks", 120, 1, 20_000);

        BOSS_ABILITY_GLOBAL_COOLDOWN_TICKS = builder
            .comment("Global minimum spacing (ticks) between special abilities. Does not affect melee AI")
            .defineInRange("bossAbilityGlobalCooldownTicks", 20, 0, 20_000);

        BOSS_PHASE_TRANSITION_BUFF_TICKS = builder
            .comment("Duration (ticks) of the boss power spike when entering phase 2/3")
            .defineInRange("bossPhaseTransitionBuffTicks", 80, 0, 20_000);

        BOSS_PHASE_TRANSITION_RADIUS = builder
            .comment("Radius (blocks) around the boss affected by phase transition pulse")
            .defineInRange("bossPhaseTransitionRadius", 10.0D, 0.0D, 128.0D);

        BOSS_PHASE_TRANSITION_KNOCKBACK = builder
            .comment("Strength of the knockback/pull impulse on phase transition")
            .defineInRange("bossPhaseTransitionKnockback", 1.1D, 0.0D, 10.0D);

        builder.pop();

        builder.pop();

        COMMON_SPEC = builder.build();
    }
}

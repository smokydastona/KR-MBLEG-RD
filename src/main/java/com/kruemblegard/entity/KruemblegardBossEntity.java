package com.kruemblegard.entity;

import com.kruemblegard.config.ModConfig;
import com.kruemblegard.entity.projectile.ArcaneStormProjectileEntity;
import com.kruemblegard.entity.projectile.MeteorArmEntity;
import com.kruemblegard.entity.projectile.RuneBoltEntity;
import com.kruemblegard.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class KruemblegardBossEntity extends Monster implements GeoEntity {

    // -----------------------------
    // PHASE DATA
    // -----------------------------
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(KruemblegardBossEntity.class, EntityDataSerializers.INT);

    // Used by existing systems (music + arena controller)
    private static final EntityDataAccessor<Boolean> ENGAGED =
            SynchedEntityData.defineId(KruemblegardBossEntity.class, EntityDataSerializers.BOOLEAN);

        private static final EntityDataAccessor<Integer> ATTACK_ANIM =
            SynchedEntityData.defineId(KruemblegardBossEntity.class, EntityDataSerializers.INT);

        private static final int ATTACK_ANIM_NONE = 0;
        private static final int ATTACK_ANIM_MELEE_SWIPE = 1;
        private static final int ATTACK_ANIM_CLEAVE = 2;
        private static final int ATTACK_ANIM_RUNE_BOLT = 3;
        private static final int ATTACK_ANIM_RUNE_DASH = 4;
        private static final int ATTACK_ANIM_GRAVITIC_PULL = 5;
        private static final int ATTACK_ANIM_RUNE_VOLLEY = 6;
        private static final int ATTACK_ANIM_BLINK_STRIKE = 7;
        private static final int ATTACK_ANIM_METEOR_ARM = 8;
        private static final int ATTACK_ANIM_ARCANE_STORM = 9;
        private static final int ATTACK_ANIM_WHIRLWIND = 10;
        private static final int ATTACK_ANIM_METEOR_SHOWER = 11;
        private static final int ATTACK_ANIM_ARCANE_BEAM = 12;

        private static final int ATTACK_ANIM_HURT = 90;
        private static final int ATTACK_ANIM_DEATH = 91;

    // -----------------------------
    // BOSS BAR
    // -----------------------------
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.translatable("entity.kruemblegard.kruemblegard"),
                    BossEvent.BossBarColor.PURPLE,
                    BossEvent.BossBarOverlay.PROGRESS);

    // -----------------------------
    // GECKOLIB CACHE
    // -----------------------------
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // -----------------------------
    // ANIMATIONS
    // -----------------------------
    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("animation.kruemblegard.idle");

    private static final RawAnimation MOVE =
            RawAnimation.begin().thenLoop("animation.kruemblegard.move");

        private static final RawAnimation IDLE_PHASE4 =
            RawAnimation.begin().thenLoop("animation.kruemblegard.idle_phase4");

        private static final RawAnimation MOVE_PHASE4 =
            RawAnimation.begin().thenLoop("animation.kruemblegard.move_phase4");

    private static final RawAnimation ATTACK_MELEE_SWIPE =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_melee");

    private static final RawAnimation ATTACK_CLEAVE =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_cleave");

    private static final RawAnimation ATTACK_RUNE_BOLT =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_rune_bolt");

    private static final RawAnimation ATTACK_RUNE_DASH =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_dash");

    private static final RawAnimation ATTACK_GRAVITIC_PULL =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_gravitic_pull");

    private static final RawAnimation ATTACK_RUNE_VOLLEY =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_rune_volley");

    private static final RawAnimation ATTACK_BLINK_STRIKE =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_blink_strike");

    private static final RawAnimation ATTACK_METEOR_ARM =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_meteor_arm");

    private static final RawAnimation ATTACK_ARCANE_STORM =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_arcane_storm");

    private static final RawAnimation ATTACK_WHIRLWIND =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_whirlwind");

    private static final RawAnimation ATTACK_METEOR_SHOWER =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_meteor_shower");

    private static final RawAnimation ATTACK_ARCANE_BEAM =
        RawAnimation.begin().thenPlay("animation.kruemblegard.attack_arcane_beam");

        private static final RawAnimation HURT =
            RawAnimation.begin().thenPlay("animation.kruemblegard.hurt");

        private static final RawAnimation DEATH =
            RawAnimation.begin().thenPlay("animation.kruemblegard.death");

    // -----------------------------
    // ARENA / EMERGENCE (integration)
    // -----------------------------
    @Nullable
    private BlockPos arenaCenter;

    private boolean emerging;
    private double emergenceTargetY;

    private int attackAnimTicks;

    // Client-only: used to trigger one-shot phase transition animations
    private int clientLastPhase = -1;

    private int cleaveCooldown;
    private int runeBoltCooldown;
    private int graviticPullCooldown;
    private int runeDashCooldown;
    private int runeVolleyCooldown;
    private int blinkStrikeCooldown;
    private int meteorArmCooldown;
    private int arcaneStormCooldown;
    private int whirlwindCooldown;
    private int meteorShowerCooldown;
    private int arcaneBeamCooldown;

    // -----------------------------
    // CUSTOM MELEE TIMING (windup -> impact)
    // -----------------------------
    private static final int MELEE_TOTAL_TICKS = 8;
    // Impact happens when meleeTicksRemaining == MELEE_IMPACT_AT
    private static final int MELEE_IMPACT_AT = 4;

    // Global “pace” tuning. Multiplies most boss cooldown rolls.
    private static final float ATTACK_COOLDOWN_MULT = 0.75F;

    private int meleeCooldown;
    private int meleeTicksRemaining;
    private boolean meleeImpactDone;

    private int globalAbilityCooldown;

    private int currentAbility;
    private int currentAbilityTicksRemaining;
    private int currentAbilityImpactAt;
    private boolean currentAbilityImpactDone;

    // -----------------------------
    // PHASE ATTACK PATTERN
    // Each phase cycles FAST -> HEAVY -> RANGED.
    // -----------------------------
    private static final int PHASE_ATTACK_FAST = 0;
    private static final int PHASE_ATTACK_HEAVY = 1;
    private static final int PHASE_ATTACK_RANGED = 2;

    private int phaseAttackStep;
    private int phaseAttackStepPhase;

    private static final int ABILITY_NONE = 0;
    private static final int ABILITY_CLEAVE = 1;
    private static final int ABILITY_RUNE_BOLT = 2;
    private static final int ABILITY_GRAVITIC_PULL = 3;
    private static final int ABILITY_RUNE_DASH = 4;
    private static final int ABILITY_RUNE_VOLLEY = 5;
    private static final int ABILITY_BLINK_STRIKE = 6;
    private static final int ABILITY_METEOR_ARM = 7;
    private static final int ABILITY_ARCANE_STORM = 8;
    private static final int ABILITY_WHIRLWIND = 9;
    private static final int ABILITY_METEOR_SHOWER = 10;
    private static final int ABILITY_ARCANE_BEAM = 11;

    // -----------------------------
    // CONSTRUCTOR
    // -----------------------------
    public KruemblegardBossEntity(EntityType<? extends KruemblegardBossEntity> type, Level level) {
        super(type, level);
        this.xpReward = 12000;
        this.noCulling = true;

        this.cleaveCooldown = 0;
        this.runeBoltCooldown = 0;
        this.graviticPullCooldown = 0;
        this.runeDashCooldown = 0;
        this.runeVolleyCooldown = 0;
        this.blinkStrikeCooldown = 0;
        this.meteorArmCooldown = 0;
        this.arcaneStormCooldown = 0;
        this.whirlwindCooldown = 0;
        this.meteorShowerCooldown = 0;
        this.arcaneBeamCooldown = 0;

        this.globalAbilityCooldown = 0;
        this.currentAbility = ABILITY_NONE;
        this.currentAbilityTicksRemaining = 0;
        this.currentAbilityImpactAt = 0;
        this.currentAbilityImpactDone = false;

        this.phaseAttackStep = 0;
        this.phaseAttackStepPhase = 0;

        this.meleeCooldown = 0;
        this.meleeTicksRemaining = 0;
        this.meleeImpactDone = false;
    }

    // -----------------------------
    // ATTRIBUTES
    // -----------------------------
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                // Config-driven so balance can be tuned without recompiling.
                .add(Attributes.MAX_HEALTH, ModConfig.BOSS_MAX_HEALTH.get())
                .add(Attributes.ARMOR, ModConfig.BOSS_ARMOR.get())
                .add(Attributes.ARMOR_TOUGHNESS, ModConfig.BOSS_ARMOR_TOUGHNESS.get())
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, ModConfig.BOSS_ATTACK_DAMAGE.get())
                .add(Attributes.ATTACK_KNOCKBACK, ModConfig.BOSS_ATTACK_KNOCKBACK.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    // -----------------------------
    // SYNCHED DATA
    // -----------------------------
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 1);
        this.entityData.define(ENGAGED, false);
        this.entityData.define(ATTACK_ANIM, ATTACK_ANIM_NONE);
    }

    private int getAttackAnim() {
        return this.entityData.get(ATTACK_ANIM);
    }

    private void setAttackAnim(int animId, int ticks) {
        this.attackAnimTicks = Math.max(this.attackAnimTicks, ticks);
        this.entityData.set(ATTACK_ANIM, animId);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public boolean isEngaged() {
        return this.entityData.get(ENGAGED);
    }

    public void setEngaged(boolean engaged) {
        this.entityData.set(ENGAGED, engaged);
    }

    public void setArenaCenter(@Nullable BlockPos arenaCenter) {
        this.arenaCenter = arenaCenter;
    }

    public void beginEmergence(double targetY) {
        this.emerging = true;
        this.emergenceTargetY = targetY;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (!result) return false;

        if (!this.level().isClientSide
            && amount > 0.0F
            && !this.isDeadOrDying()
            && this.attackAnimTicks <= 0
            && this.currentAbility == ABILITY_NONE
            && !this.isMeleeInProgress()) {
            // Short flinch. Avoid interrupting telegraphed attacks/melee windups.
            this.setAttackAnim(ATTACK_ANIM_HURT, 8);
        }
        return true;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            // Force death animation to take over.
            this.meleeTicksRemaining = 0;
            this.currentAbility = ABILITY_NONE;
            this.setAttackAnim(ATTACK_ANIM_DEATH, 40);
            this.getNavigation().stop();
        }
        super.die(source);
    }

    // -----------------------------
    // AI GOALS
    // -----------------------------
    @Override
    protected void registerGoals() {

        // Movement
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new KruemblegardMeleeGoal(1.0D));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6D));

        // Awareness
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
            this,
            LivingEntity.class,
            10,
            true,
            false,
            target -> target != this
                && !(target instanceof KruemblegardBossEntity)
                && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)
        ));
    }

    // -----------------------------
    // FLOATING MOVEMENT
    // -----------------------------
    @Override
    public boolean isNoGravity() {
        return this.emerging;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            int phase = this.getPhase();
            if (clientLastPhase == -1) {
                clientLastPhase = phase;
            } else if (phase != clientLastPhase) {
                clientLastPhase = phase;

                if (phase == 2) {
                    this.triggerAnim("phase_change", "phase2");
                } else if (phase == 3) {
                    this.triggerAnim("phase_change", "phase3");
                } else if (phase == 4) {
                    this.triggerAnim("phase_change", "phase4");
                }
            }
        }

        if (!this.level().isClientSide) {
            if (this.attackAnimTicks > 0) {
                this.attackAnimTicks--;
                if (this.attackAnimTicks <= 0) {
                    this.entityData.set(ATTACK_ANIM, ATTACK_ANIM_NONE);
                }
            }
        }

        if (!this.level().isClientSide && emerging) {
            double remaining = emergenceTargetY - this.getY();
            if (remaining <= 0.0) {
                emerging = false;
                this.setInvulnerable(false);
                this.setNoAi(false);
                this.setEngaged(true);
            } else {
                double step = Math.min(0.08, remaining);
                this.setPos(this.getX(), this.getY() + step, this.getZ());
            }
        }

        if (!this.level().isClientSide && this.isEngaged()) {
            double regen = ModConfig.BOSS_REGEN_PER_TICK.get();
            if (regen > 0.0D && this.getHealth() < this.getMaxHealth()) {
                this.heal((float) regen);
            }
        }

        if (!this.level().isClientSide) {
            tickCooldowns();
			tickMeleeAttack();
        }

        // Phase logic
        if (!this.level().isClientSide) {
            float ratio = this.getHealth() / this.getMaxHealth();
            float phase2 = ModConfig.BOSS_PHASE_2_HEALTH_RATIO.get().floatValue();
            float phase3 = ModConfig.BOSS_PHASE_3_HEALTH_RATIO.get().floatValue();
            float phase4 = ModConfig.BOSS_PHASE_4_HEALTH_RATIO.get().floatValue();
            int phase = ratio <= phase4 ? 4 : ratio <= phase3 ? 3 : ratio <= phase2 ? 2 : 1;

            if (phase != this.getPhase()) {
                this.setPhase(phase);
                onPhaseChanged(phase);
            }

            this.bossEvent.setProgress(ratio);
        }

        // Phase-based attacks (hooks only — projectiles added in Message 4)
        if (!this.level().isClientSide) {
            switch (this.getPhase()) {
                case 1 -> phaseOneBehavior();
                case 2 -> phaseTwoBehavior();
                case 3 -> phaseThreeBehavior();
                case 4 -> phaseFourBehavior();
            }
        }
    }

    private void tickCooldowns() {
        if (this.globalAbilityCooldown > 0) this.globalAbilityCooldown--;
        if (this.cleaveCooldown > 0) this.cleaveCooldown--;
        if (this.runeBoltCooldown > 0) this.runeBoltCooldown--;
        if (this.graviticPullCooldown > 0) this.graviticPullCooldown--;
        if (this.runeDashCooldown > 0) this.runeDashCooldown--;
        if (this.runeVolleyCooldown > 0) this.runeVolleyCooldown--;
        if (this.blinkStrikeCooldown > 0) this.blinkStrikeCooldown--;
        if (this.meteorArmCooldown > 0) this.meteorArmCooldown--;
        if (this.arcaneStormCooldown > 0) this.arcaneStormCooldown--;
        if (this.whirlwindCooldown > 0) this.whirlwindCooldown--;
        if (this.meteorShowerCooldown > 0) this.meteorShowerCooldown--;
        if (this.arcaneBeamCooldown > 0) this.arcaneBeamCooldown--;
        if (this.meleeCooldown > 0) this.meleeCooldown--;
    }

    private boolean isMeleeInProgress() {
        return this.meleeTicksRemaining > 0;
    }

    private void startMeleeAttack() {
        if (this.level().isClientSide) return;
        if (this.meleeCooldown > 0) return;
        if (this.currentAbility != ABILITY_NONE) return;
        if (isMeleeInProgress()) return;

        this.meleeImpactDone = false;
        this.meleeTicksRemaining = MELEE_TOTAL_TICKS;

        // Impact happens at MELEE_TOTAL_TICKS - MELEE_IMPACT_AT ticks after start.
        // Keep the attack animation active through the impact tick (same pattern as abilities).
        int telegraphAnimTicks = Math.max(1, (MELEE_TOTAL_TICKS - MELEE_IMPACT_AT) + 2);
        this.swing(InteractionHand.MAIN_HAND);
        this.setAttackAnim(ATTACK_ANIM_MELEE_SWIPE, telegraphAnimTicks);
    }

    private void tickMeleeAttack() {
        if (!isMeleeInProgress()) return;

        this.getNavigation().stop();
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            this.meleeTicksRemaining = 0;
            return;
        }

        if (!this.meleeImpactDone && this.meleeTicksRemaining == MELEE_IMPACT_AT) {
            this.meleeImpactDone = true;
            double reachSq = getAttackReachSqr(target);
            if (this.distanceToSqr(target) <= reachSq) {
                this.doHurtTarget(target);
            }
        }

        this.meleeTicksRemaining--;
        if (this.meleeTicksRemaining <= 0) {
            this.meleeCooldown = 12;
        }
    }

    private double getAttackReachSqr(LivingEntity target) {
        float w = this.getBbWidth();
        return (double)(w * 2.0F * w * 2.0F + target.getBbWidth());
    }

    private class KruemblegardMeleeGoal extends Goal {
        private final double speed;
        private int repathTime;

        private KruemblegardMeleeGoal(double speed) {
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = KruemblegardBossEntity.this.getTarget();
            return KruemblegardBossEntity.this.getPhase() == 1 && target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = KruemblegardBossEntity.this.getTarget();
            return KruemblegardBossEntity.this.getPhase() == 1 && target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.repathTime = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = KruemblegardBossEntity.this.getTarget();
            if (target == null) return;

            KruemblegardBossEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (KruemblegardBossEntity.this.currentAbility != ABILITY_NONE || KruemblegardBossEntity.this.isMeleeInProgress()) {
                KruemblegardBossEntity.this.getNavigation().stop();
                return;
            }

            double distSq = KruemblegardBossEntity.this.distanceToSqr(target);
            double reachSq = KruemblegardBossEntity.this.getAttackReachSqr(target);

            if (--this.repathTime <= 0) {
                this.repathTime = 10;
                KruemblegardBossEntity.this.getNavigation().moveTo(target, this.speed);
            }

            if (distSq <= reachSq && KruemblegardBossEntity.this.meleeCooldown <= 0) {
                KruemblegardBossEntity.this.startMeleeAttack();
            }
        }
    }

    // -----------------------------
    // PHASE 1 — BASIC MELEE + SLOW MOVEMENT
    // -----------------------------
    private void phaseOneBehavior() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        if (isMeleeInProgress()) return;

        if (tickCurrentAbility()) {
            return;
        }

        if (this.globalAbilityCooldown > 0) return;

        ensurePhaseAttackPattern(1);
        int category = this.phaseAttackStep % 3;

        boolean started = switch (category) {
            case PHASE_ATTACK_FAST -> tryStartMeleeFast(target);
            case PHASE_ATTACK_HEAVY -> tryStartAbilityIfReady(ABILITY_CLEAVE);
            case PHASE_ATTACK_RANGED -> tryStartAbilityIfReady(ABILITY_RUNE_BOLT);
            default -> false;
        };

        // Fallback if the scheduled move can't start yet.
        if (!started) {
            double distSq = this.distanceToSqr(target);
            int[] preferred = distSq > 81.0
                ? new int[] {ABILITY_RUNE_BOLT, ABILITY_CLEAVE}
                : new int[] {ABILITY_CLEAVE, ABILITY_RUNE_BOLT};

            for (int id : preferred) {
                if (tryStartAbilityIfReady(id)) {
                    started = true;
                    break;
                }
            }
        }

        if (started) {
            this.phaseAttackStep++;
        }
    }

    // -----------------------------
    // PHASE 2 — RUNE BOLTS + GRAVITIC PULL
    // -----------------------------
    private void phaseTwoBehavior() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        if (isMeleeInProgress()) return;

        if (tickCurrentAbility()) {
            return;
        }

        if (this.globalAbilityCooldown > 0) return;

        ensurePhaseAttackPattern(2);
        int category = this.phaseAttackStep % 3;
        boolean started = switch (category) {
            case PHASE_ATTACK_FAST -> tryStartAbilityIfReady(ABILITY_RUNE_DASH);
            case PHASE_ATTACK_HEAVY -> tryStartAbilityIfReady(ABILITY_GRAVITIC_PULL);
            case PHASE_ATTACK_RANGED -> tryStartAbilityIfReady(ABILITY_RUNE_VOLLEY);
            default -> false;
        };

        if (!started) {
            double distSq = this.distanceToSqr(target);
            int[] preferred = distSq > 81.0
                ? new int[] {ABILITY_RUNE_VOLLEY, ABILITY_GRAVITIC_PULL, ABILITY_RUNE_DASH}
                : new int[] {ABILITY_RUNE_DASH, ABILITY_GRAVITIC_PULL, ABILITY_RUNE_VOLLEY};

            for (int id : preferred) {
                if (tryStartAbilityIfReady(id)) {
                    started = true;
                    break;
                }
            }
        }

        if (started) {
            this.phaseAttackStep++;
        }
    }

    // -----------------------------
    // PHASE 3 — DASH + METEOR ARM + ARCANE STORM
    // -----------------------------
    private void phaseThreeBehavior() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        if (isMeleeInProgress()) return;

        if (tickCurrentAbility()) {
            return;
        }

        if (this.globalAbilityCooldown > 0) return;

        ensurePhaseAttackPattern(3);
        int category = this.phaseAttackStep % 3;
        boolean started = switch (category) {
            case PHASE_ATTACK_FAST -> tryStartAbilityIfReady(ABILITY_BLINK_STRIKE);
            case PHASE_ATTACK_HEAVY -> tryStartAbilityIfReady(ABILITY_METEOR_ARM);
            case PHASE_ATTACK_RANGED -> tryStartAbilityIfReady(ABILITY_ARCANE_STORM);
            default -> false;
        };

        if (!started) {
            double distSq = this.distanceToSqr(target);
            int[] preferred = distSq > 81.0
                ? new int[] {ABILITY_ARCANE_STORM, ABILITY_BLINK_STRIKE, ABILITY_METEOR_ARM}
                : new int[] {ABILITY_METEOR_ARM, ABILITY_BLINK_STRIKE, ABILITY_ARCANE_STORM};

            for (int id : preferred) {
                if (tryStartAbilityIfReady(id)) {
                    started = true;
                    break;
                }
            }
        }

        if (started) {
            this.phaseAttackStep++;
        }
    }

    // -----------------------------
    // PHASE 4 — WHIRLWIND + METEOR SHOWER + ARCANE BEAM
    // -----------------------------
    private void phaseFourBehavior() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        if (isMeleeInProgress()) return;

        if (tickCurrentAbility()) {
            return;
        }

        if (this.globalAbilityCooldown > 0) return;

        ensurePhaseAttackPattern(4);
        int category = this.phaseAttackStep % 3;
        boolean started = switch (category) {
            case PHASE_ATTACK_FAST -> tryStartAbilityIfReady(ABILITY_WHIRLWIND);
            case PHASE_ATTACK_HEAVY -> tryStartAbilityIfReady(ABILITY_METEOR_SHOWER);
            case PHASE_ATTACK_RANGED -> tryStartAbilityIfReady(ABILITY_ARCANE_BEAM);
            default -> false;
        };

        if (!started) {
            double distSq = this.distanceToSqr(target);
            int[] preferred = distSq > 100.0
                ? new int[] {ABILITY_ARCANE_BEAM, ABILITY_METEOR_SHOWER, ABILITY_WHIRLWIND}
                : new int[] {ABILITY_WHIRLWIND, ABILITY_METEOR_SHOWER, ABILITY_ARCANE_BEAM};

            for (int id : preferred) {
                if (tryStartAbilityIfReady(id)) {
                    started = true;
                    break;
                }
            }
        }

        if (started) {
            this.phaseAttackStep++;
        }
    }

    private void onPhaseChanged(int phase) {
        doPhaseTransitionEvent(phase);

        // Reset attack pattern so each phase starts with FAST.
        this.phaseAttackStepPhase = phase;
        this.phaseAttackStep = 0;

        // Reset/seed cooldowns so a new phase doesn't instantly fire everything on the same tick.
        this.currentAbility = ABILITY_NONE;

        this.cleaveCooldown = 0;
        this.runeBoltCooldown = 0;
        this.graviticPullCooldown = 0;
        this.runeDashCooldown = 0;
        this.runeVolleyCooldown = 0;
        this.blinkStrikeCooldown = 0;
        this.meteorArmCooldown = 0;
        this.arcaneStormCooldown = 0;
        this.whirlwindCooldown = 0;
        this.meteorShowerCooldown = 0;
        this.arcaneBeamCooldown = 0;

        this.globalAbilityCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_ABILITY_GLOBAL_COOLDOWN_TICKS.get()));

        if (phase == 1) {
            this.cleaveCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_1_CLEAVE_COOLDOWN_TICKS.get()));
            this.runeBoltCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_2_RUNE_BOLT_COOLDOWN_TICKS.get()));
            return;
        }

        if (phase == 2) {
            this.runeDashCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_3_DASH_COOLDOWN_TICKS.get()));
            this.graviticPullCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_2_GRAVITIC_PULL_COOLDOWN_TICKS.get()));
            this.runeVolleyCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_2_RUNE_VOLLEY_COOLDOWN_TICKS.get()));
            return;
        }

        if (phase == 3) {
            this.blinkStrikeCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_3_BLINK_STRIKE_COOLDOWN_TICKS.get()));
            this.meteorArmCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_3_METEOR_ARM_COOLDOWN_TICKS.get()));
            this.arcaneStormCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_3_ARCANE_STORM_COOLDOWN_TICKS.get()));
            return;
        }

        // Phase 4
        this.whirlwindCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_4_WHIRLWIND_COOLDOWN_TICKS.get()));
        this.meteorShowerCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_3_METEOR_SHOWER_COOLDOWN_TICKS.get()));
        this.arcaneBeamCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_4_ARCANE_BEAM_COOLDOWN_TICKS.get()));
    }

    private void ensurePhaseAttackPattern(int phase) {
        if (this.phaseAttackStepPhase != phase) {
            this.phaseAttackStepPhase = phase;
            this.phaseAttackStep = 0;
        }
    }

    private boolean tryStartAbilityIfReady(int ability) {
        if (this.level().isClientSide) return false;
        if (this.currentAbility != ABILITY_NONE) return false;
        if (isMeleeInProgress()) return false;
        if (this.globalAbilityCooldown > 0) return false;
        if (!isAbilityOffCooldown(ability)) return false;

        startAbility(ability);
        return true;
    }

    private boolean isAbilityOffCooldown(int ability) {
        return switch (ability) {
            case ABILITY_CLEAVE -> this.cleaveCooldown <= 0;
            case ABILITY_RUNE_BOLT -> this.runeBoltCooldown <= 0;
            case ABILITY_GRAVITIC_PULL -> this.graviticPullCooldown <= 0;
            case ABILITY_RUNE_DASH -> this.runeDashCooldown <= 0;
            case ABILITY_RUNE_VOLLEY -> this.runeVolleyCooldown <= 0;
            case ABILITY_BLINK_STRIKE -> this.blinkStrikeCooldown <= 0;
            case ABILITY_METEOR_ARM -> this.meteorArmCooldown <= 0;
            case ABILITY_ARCANE_STORM -> this.arcaneStormCooldown <= 0;
            case ABILITY_WHIRLWIND -> this.whirlwindCooldown <= 0;
            case ABILITY_METEOR_SHOWER -> this.meteorShowerCooldown <= 0;
            case ABILITY_ARCANE_BEAM -> this.arcaneBeamCooldown <= 0;
            default -> true;
        };
    }

    private boolean tryStartMeleeFast(LivingEntity target) {
        if (this.level().isClientSide) return false;
        if (this.meleeCooldown > 0) return false;
        if (this.currentAbility != ABILITY_NONE) return false;
        if (isMeleeInProgress()) return false;

        double distSq = this.distanceToSqr(target);
        double reachSq = getAttackReachSqr(target);
        if (distSq > reachSq) return false;

        startMeleeAttack();

        // Prevent immediate ability chaining right after the melee windup.
        this.globalAbilityCooldown = Math.max(this.globalAbilityCooldown, 6);
        return true;
    }

    private void doPhaseTransitionEvent(int phase) {
        if (this.level().isClientSide) return;
        if (!this.isEngaged()) return;

        int buffTicks = ModConfig.BOSS_PHASE_TRANSITION_BUFF_TICKS.get();

        // A short "power spike" that players can feel.
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, buffTicks, phase >= 3 ? 1 : 0, false, true));
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, buffTicks, 0, false, true));

        // Arena pressure: brief debuff + pull/knockback pulse.
        double radius = ModConfig.BOSS_PHASE_TRANSITION_RADIUS.get();
        float kb = ModConfig.BOSS_PHASE_TRANSITION_KNOCKBACK.get().floatValue();

        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(radius))) {
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.min(60, buffTicks), 0, false, true));

            Vec3 away = p.position().subtract(this.position());
            Vec3 dir = away.lengthSqr() < 1.0E-6 ? new Vec3(0, 0, 0) : away.normalize();
            // Phase 2 pulls in slightly; phase 3 blasts out.
            Vec3 impulse = (phase >= 3)
                ? new Vec3(dir.x * kb, 0.35, dir.z * kb)
                : new Vec3(-dir.x * (kb * 0.35f), 0.15, -dir.z * (kb * 0.35f));
            p.setDeltaMovement(p.getDeltaMovement().add(impulse));
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 1.8, this.getZ(),
                40, 0.6, 0.6, 0.6, 0.01);
        }

        this.playSound(ModSounds.KRUEMBLEGARD_ATTACK.get(), 1.2f, phase >= 3 ? 0.75f : 0.85f);
    }

    private boolean tickCurrentAbility() {
        if (this.currentAbility == ABILITY_NONE) return false;
        if (this.currentAbilityTicksRemaining <= 0) {
            this.currentAbility = ABILITY_NONE;
            return false;
        }

        this.getNavigation().stop();

        if (!this.currentAbilityImpactDone && this.currentAbilityTicksRemaining == this.currentAbilityImpactAt) {
            this.currentAbilityImpactDone = true;
            doAbilityImpact(this.currentAbility);
        }

        this.currentAbilityTicksRemaining--;
        if (this.currentAbilityTicksRemaining <= 0) {
            finishAbility(this.currentAbility);
            this.currentAbility = ABILITY_NONE;
        }
        return true;
    }

    private void startAbility(int ability) {
        this.currentAbility = ability;
        this.currentAbilityImpactDone = false;

        // Windup/impact timings tuned for readability.
        int totalTicks;
        int impactAt;
        switch (ability) {
            case ABILITY_CLEAVE -> { totalTicks = 18; impactAt = 10; }
            case ABILITY_RUNE_BOLT -> { totalTicks = 16; impactAt = 8; }
            case ABILITY_GRAVITIC_PULL -> { totalTicks = 18; impactAt = 10; }
            case ABILITY_RUNE_DASH -> { totalTicks = 14; impactAt = 7; }
            case ABILITY_RUNE_VOLLEY -> { totalTicks = 20; impactAt = 10; }
            case ABILITY_BLINK_STRIKE -> { totalTicks = 14; impactAt = 8; }
            case ABILITY_METEOR_ARM -> { totalTicks = 22; impactAt = 12; }
            case ABILITY_ARCANE_STORM -> { totalTicks = 26; impactAt = 14; }
            case ABILITY_WHIRLWIND -> { totalTicks = 18; impactAt = 9; }
            case ABILITY_METEOR_SHOWER -> { totalTicks = 28; impactAt = 14; }
            case ABILITY_ARCANE_BEAM -> { totalTicks = 22; impactAt = 12; }
            default -> { totalTicks = 10; impactAt = 5; }
        }

        this.currentAbilityTicksRemaining = totalTicks;
        this.currentAbilityImpactAt = impactAt;

        // We count down from totalTicks to impactAt; impact happens when ticksRemaining == impactAt.
        // Because the attack animation tick countdown is decremented earlier in aiStep(), add a small
        // buffer so the telegraph animation is still active on the impact tick.
        int telegraphAnimTicks = Math.max(1, (totalTicks - impactAt) + 2);

        // Telegraph: animation + sound.
        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            switch (ability) {
                case ABILITY_CLEAVE -> this.setAttackAnim(ATTACK_ANIM_CLEAVE, telegraphAnimTicks);
                case ABILITY_RUNE_BOLT -> this.setAttackAnim(ATTACK_ANIM_RUNE_BOLT, telegraphAnimTicks);
                case ABILITY_GRAVITIC_PULL -> this.setAttackAnim(ATTACK_ANIM_GRAVITIC_PULL, telegraphAnimTicks);
                case ABILITY_RUNE_DASH -> this.setAttackAnim(ATTACK_ANIM_RUNE_DASH, telegraphAnimTicks);
                case ABILITY_RUNE_VOLLEY -> this.setAttackAnim(ATTACK_ANIM_RUNE_VOLLEY, telegraphAnimTicks);
                case ABILITY_BLINK_STRIKE -> this.setAttackAnim(ATTACK_ANIM_BLINK_STRIKE, telegraphAnimTicks);
                case ABILITY_METEOR_ARM -> this.setAttackAnim(ATTACK_ANIM_METEOR_ARM, telegraphAnimTicks);
                case ABILITY_ARCANE_STORM -> this.setAttackAnim(ATTACK_ANIM_ARCANE_STORM, telegraphAnimTicks);
                case ABILITY_WHIRLWIND -> this.setAttackAnim(ATTACK_ANIM_WHIRLWIND, telegraphAnimTicks);
                case ABILITY_METEOR_SHOWER -> this.setAttackAnim(ATTACK_ANIM_METEOR_SHOWER, telegraphAnimTicks);
                case ABILITY_ARCANE_BEAM -> this.setAttackAnim(ATTACK_ANIM_ARCANE_BEAM, telegraphAnimTicks);
                default -> this.setAttackAnim(ATTACK_ANIM_MELEE_SWIPE, telegraphAnimTicks);
            }

            float pitch = 0.9f + (this.random.nextFloat() * 0.2f);
            if (ability == ABILITY_ARCANE_STORM) {
                this.playSound(ModSounds.KRUEMBLEGARD_STORM.get(), 1.0f, pitch);
            } else if (ability == ABILITY_RUNE_DASH || ability == ABILITY_BLINK_STRIKE) {
                this.playSound(ModSounds.KRUEMBLEGARD_DASH.get(), 1.0f, pitch);
            } else {
                this.playSound(ModSounds.KRUEMBLEGARD_ATTACK.get(), 1.0f, pitch);
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.6, this.getZ(),
                    16, 0.35, 0.35, 0.35, 0.02);
            }
        }
    }

    private void doAbilityImpact(int ability) {
        switch (ability) {
            case ABILITY_CLEAVE -> doCleave();
            case ABILITY_RUNE_BOLT -> fireRuneBoltSingle();
            case ABILITY_GRAVITIC_PULL -> doGraviticPull();
            case ABILITY_RUNE_DASH -> doRuneDash();
            case ABILITY_RUNE_VOLLEY -> fireRuneVolley();
            case ABILITY_BLINK_STRIKE -> doBlinkStrike();
            case ABILITY_METEOR_ARM -> doMeteorArm();
            case ABILITY_ARCANE_STORM -> doArcaneStorm();
            case ABILITY_WHIRLWIND -> doWhirlwind();
            case ABILITY_METEOR_SHOWER -> doMeteorShower();
            case ABILITY_ARCANE_BEAM -> doArcaneBeam();
            default -> {}
        }
    }

    private void finishAbility(int ability) {
        this.globalAbilityCooldown = rollCooldown(Math.max(5, ModConfig.BOSS_ABILITY_GLOBAL_COOLDOWN_TICKS.get()));

        // Set per-ability cooldowns on completion so abilities don't stack.
        switch (ability) {
            case ABILITY_CLEAVE ->
                this.cleaveCooldown = rollCooldown(ModConfig.BOSS_PHASE_1_CLEAVE_COOLDOWN_TICKS.get());
            case ABILITY_RUNE_BOLT ->
                this.runeBoltCooldown = rollCooldown(ModConfig.BOSS_PHASE_2_RUNE_BOLT_COOLDOWN_TICKS.get());
            case ABILITY_GRAVITIC_PULL ->
                this.graviticPullCooldown = rollCooldown(ModConfig.BOSS_PHASE_2_GRAVITIC_PULL_COOLDOWN_TICKS.get());
            case ABILITY_RUNE_DASH ->
                this.runeDashCooldown = rollCooldown(ModConfig.BOSS_PHASE_3_DASH_COOLDOWN_TICKS.get());
            case ABILITY_RUNE_VOLLEY ->
                this.runeVolleyCooldown = rollCooldown(ModConfig.BOSS_PHASE_2_RUNE_VOLLEY_COOLDOWN_TICKS.get());
            case ABILITY_BLINK_STRIKE ->
                this.blinkStrikeCooldown = rollCooldown(ModConfig.BOSS_PHASE_3_BLINK_STRIKE_COOLDOWN_TICKS.get());
            case ABILITY_METEOR_ARM ->
                this.meteorArmCooldown = rollCooldown(ModConfig.BOSS_PHASE_3_METEOR_ARM_COOLDOWN_TICKS.get());
            case ABILITY_ARCANE_STORM ->
                this.arcaneStormCooldown = rollCooldown(ModConfig.BOSS_PHASE_3_ARCANE_STORM_COOLDOWN_TICKS.get());
            case ABILITY_WHIRLWIND ->
                this.whirlwindCooldown = rollCooldown(ModConfig.BOSS_PHASE_4_WHIRLWIND_COOLDOWN_TICKS.get());
            case ABILITY_METEOR_SHOWER ->
                this.meteorShowerCooldown = rollCooldown(ModConfig.BOSS_PHASE_3_METEOR_SHOWER_COOLDOWN_TICKS.get());
            case ABILITY_ARCANE_BEAM ->
                this.arcaneBeamCooldown = rollCooldown(ModConfig.BOSS_PHASE_4_ARCANE_BEAM_COOLDOWN_TICKS.get());
            default -> {}
        }
    }

    private int rollCooldown(int baseTicks) {
        int base = Math.max(1, Math.round(baseTicks * ATTACK_COOLDOWN_MULT));
        int jitter = Math.max(1, base / 5);
        return base + this.random.nextInt(jitter + 1);
    }

    // -----------------------------
    // ATTACK HOOKS (IMPLEMENTED IN MESSAGE 4)
    // -----------------------------
    private void fireRuneBoltSingle() {
        fireRuneBoltVolley(1, 0.6, 0.0);
    }

    private void fireRuneVolley() {
        fireRuneBoltVolley(3, 0.55, 12.0);
    }

    private void fireRuneBoltVolley(int count, double speed, double spreadDegrees) {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            this.playSound(ModSounds.KRUEMBLEGARD_ATTACK.get(), 1.0f, 0.9f + (this.random.nextFloat() * 0.2f));
        }

        Vec3 baseDir = target.position().add(0, target.getBbHeight() * 0.5, 0)
            .subtract(this.position().add(0, 2.0, 0)).normalize();

        int mid = count / 2;
        for (int i = 0; i < count; i++) {
            double angle = (i - mid) * Math.toRadians(spreadDegrees);
            Vec3 dir = rotateY(baseDir, angle).scale(speed);

            RuneBoltEntity bolt = new RuneBoltEntity(this.level(), this);
            bolt.setPos(this.getX(), this.getY() + 2, this.getZ());
            bolt.setDeltaMovement(dir);
            this.level().addFreshEntity(bolt);
        }
    }

    private Vec3 rotateY(Vec3 v, double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new Vec3(v.x * cos - v.z * sin, v.y, v.x * sin + v.z * cos);
    }

    private void doCleave() {
        if (this.level().isClientSide) return;

        double radius = 4.0;
        float damage = (float)(ModConfig.BOSS_ATTACK_DAMAGE.get() * 1.25);

        Vec3 forward = this.getLookAngle();
        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(radius))) {
            Vec3 to = p.position().subtract(this.position());
            if (to.lengthSqr() < 1.0E-6) continue;

            Vec3 dir = to.normalize();
            // Rough 120-degree cone in front.
            if (dir.dot(forward) < 0.25) continue;

            p.hurt(this.damageSources().mobAttack(this), damage);
            p.setDeltaMovement(p.getDeltaMovement().add(dir.x * 0.5, 0.25, dir.z * 0.5));
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY() + 1.2, this.getZ(),
                8, 0.4, 0.2, 0.4, 0.02);
        }
    }

    private void doGraviticPull() {
        if (this.getTarget() == null) return;

        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16))) {
            Vec3 pull = this.position().subtract(p.position()).normalize().scale(0.4);
            p.setDeltaMovement(p.getDeltaMovement().add(pull));
        }
    }

    private void doRuneDash() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            this.playSound(ModSounds.KRUEMBLEGARD_DASH.get(), 1.0f, 0.9f + (this.random.nextFloat() * 0.2f));
        }

        Vec3 dir = target.position().subtract(this.position()).normalize().scale(1.25);
        this.setDeltaMovement(dir);

        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(2.2))) {
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, true));
            p.hurt(this.damageSources().mobAttack(this), 8.0f);
        }
    }

    private void doBlinkStrike() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            this.playSound(ModSounds.KRUEMBLEGARD_DASH.get(), 1.0f, 0.8f + (this.random.nextFloat() * 0.2f));
        }

        Vec3 behind = target.getLookAngle().normalize().scale(-2.5);
        Vec3 dest = target.position().add(behind);
        this.teleportTo(dest.x, dest.y, dest.z);
        this.getNavigation().stop();

        if (this.distanceToSqr(target) <= getAttackReachSqr(target) + 2.0) {
            this.doHurtTarget(target);
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL, dest.x, dest.y + 1.0, dest.z, 20,
                0.4, 0.6, 0.4, 0.05);
        }
    }

    private void doMeteorArm() {
        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            this.playSound(ModSounds.KRUEMBLEGARD_ATTACK.get(), 1.0f, 0.9f + (this.random.nextFloat() * 0.2f));
        }

        MeteorArmEntity arm = new MeteorArmEntity(this.level(), this);
        arm.setPos(this.getX(), this.getY() + 4, this.getZ());
        arm.setDeltaMovement(0, -0.6, 0);
        this.level().addFreshEntity(arm);
    }

    private void doArcaneStorm() {
        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            this.playSound(ModSounds.KRUEMBLEGARD_STORM.get(), 1.0f, 0.9f + (this.random.nextFloat() * 0.2f));
        }

        for (int i = 0; i < 8; i++) {
            ArcaneStormProjectileEntity proj = new ArcaneStormProjectileEntity(this.level(), this);
            double ox = (this.random.nextDouble() - 0.5) * 10;
            double oz = (this.random.nextDouble() - 0.5) * 10;

            proj.setPos(this.getX() + ox, this.getY() + 10, this.getZ() + oz);
            this.level().addFreshEntity(proj);
        }
    }

    private void doWhirlwind() {
        if (this.level().isClientSide) return;

        this.swing(InteractionHand.MAIN_HAND);
        this.playSound(ModSounds.KRUEMBLEGARD_ATTACK.get(), 1.0f, 0.75f + (this.random.nextFloat() * 0.2f));

        double radius = 5.0;
        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(radius))) {
            Vec3 away = p.position().subtract(this.position());
            Vec3 dir = away.lengthSqr() < 1.0E-6 ? new Vec3(0, 0, 0) : away.normalize();
            p.hurt(this.damageSources().mobAttack(this), 10.0f);
            p.setDeltaMovement(p.getDeltaMovement().add(dir.x * 0.6, 0.25, dir.z * 0.6));
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0, this.getZ(),
                30, 1.2, 0.4, 1.2, 0.03);
        }
    }

    private void doMeteorShower() {
        if (this.level().isClientSide) return;

        this.swing(InteractionHand.MAIN_HAND);
        this.playSound(ModSounds.KRUEMBLEGARD_ATTACK.get(), 1.2f, 0.7f + (this.random.nextFloat() * 0.2f));

        int count = 5;
        for (int i = 0; i < count; i++) {
            double ox = (this.random.nextDouble() - 0.5) * 12;
            double oz = (this.random.nextDouble() - 0.5) * 12;

            MeteorArmEntity arm = new MeteorArmEntity(this.level(), this);
            arm.setPos(this.getX() + ox, this.getY() + 10, this.getZ() + oz);
            arm.setDeltaMovement(0, -0.8, 0);
            this.level().addFreshEntity(arm);
        }
    }

    private void doArcaneBeam() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        if (this.level().isClientSide) return;

        Vec3 start = this.position().add(0, 1.8, 0);
        Vec3 end = target.position().add(0, target.getBbHeight() * 0.5, 0);
        double range = 22.0;
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 1.0E-6) return;
        dir = dir.normalize();
        end = start.add(dir.scale(range));

        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i <= (int)range; i++) {
                Vec3 p = start.add(dir.scale(i));
                serverLevel.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            }
        }

        AABB box = new AABB(
            Math.min(start.x, end.x), Math.min(start.y, end.y), Math.min(start.z, end.z),
            Math.max(start.x, end.x), Math.max(start.y, end.y), Math.max(start.z, end.z)
        ).inflate(1.75);

        for (Player p : this.level().getEntitiesOfClass(Player.class, box)) {
            double d = distanceToSegment(p.position().add(0, p.getBbHeight() * 0.5, 0), start, end);
            if (d <= 1.5) {
                p.hurt(this.damageSources().mobAttack(this), 14.0f);
                p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, false, true));
            }
        }
    }

    private double distanceToSegment(Vec3 point, Vec3 a, Vec3 b) {
        Vec3 ab = b.subtract(a);
        double abLen2 = ab.lengthSqr();
        if (abLen2 < 1.0E-6) return point.distanceTo(a);
        double t = point.subtract(a).dot(ab) / abLen2;
        t = Math.max(0.0, Math.min(1.0, t));
        Vec3 proj = a.add(ab.scale(t));
        return point.distanceTo(proj);
    }

    // -----------------------------
    // BOSS BAR
    // -----------------------------
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    // -----------------------------
    // SOUNDS
    // -----------------------------
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.KRUEMBLEGARD_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.KRUEMBLEGARD_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        // No footsteps — floating
    }

    // -----------------------------
    // GECKOLIB CONTROLLERS
    // -----------------------------
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            Vec3 delta = this.getDeltaMovement();
            boolean moving = state.isMoving() && (delta.x * delta.x + delta.z * delta.z) > 1.0E-5;

            boolean phase4 = this.getPhase() >= 4;
            RawAnimation moveAnim = phase4 ? MOVE_PHASE4 : MOVE;
            RawAnimation idleAnim = phase4 ? IDLE_PHASE4 : IDLE;

            if (moving) {
                return state.setAndContinue(moveAnim);
            }
            return state.setAndContinue(idleAnim);
        }));

        controllers.add(new AnimationController<>(this, "attack", 0, state -> {
            int attackAnim = getAttackAnim();
            if (attackAnim != ATTACK_ANIM_NONE) {
                return switch (attackAnim) {
                    case ATTACK_ANIM_MELEE_SWIPE -> state.setAndContinue(ATTACK_MELEE_SWIPE);
                    case ATTACK_ANIM_CLEAVE -> state.setAndContinue(ATTACK_CLEAVE);
                    case ATTACK_ANIM_RUNE_BOLT -> state.setAndContinue(ATTACK_RUNE_BOLT);
                    case ATTACK_ANIM_RUNE_DASH -> state.setAndContinue(ATTACK_RUNE_DASH);
                    case ATTACK_ANIM_GRAVITIC_PULL -> state.setAndContinue(ATTACK_GRAVITIC_PULL);
                    case ATTACK_ANIM_RUNE_VOLLEY -> state.setAndContinue(ATTACK_RUNE_VOLLEY);
                    case ATTACK_ANIM_BLINK_STRIKE -> state.setAndContinue(ATTACK_BLINK_STRIKE);
                    case ATTACK_ANIM_METEOR_ARM -> state.setAndContinue(ATTACK_METEOR_ARM);
                    case ATTACK_ANIM_ARCANE_STORM -> state.setAndContinue(ATTACK_ARCANE_STORM);
                    case ATTACK_ANIM_WHIRLWIND -> state.setAndContinue(ATTACK_WHIRLWIND);
                    case ATTACK_ANIM_METEOR_SHOWER -> state.setAndContinue(ATTACK_METEOR_SHOWER);
                    case ATTACK_ANIM_ARCANE_BEAM -> state.setAndContinue(ATTACK_ARCANE_BEAM);
					case ATTACK_ANIM_HURT -> state.setAndContinue(HURT);
					case ATTACK_ANIM_DEATH -> state.setAndContinue(DEATH);
                    default -> PlayState.STOP;
                };
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "phase_change", 0, state -> PlayState.STOP)
                .triggerableAnim("phase2", RawAnimation.begin().thenPlay("animation.kruemblegard.phase2_transition"))
                .triggerableAnim("phase3", RawAnimation.begin().thenPlay("animation.kruemblegard.phase3_transition"))
                .triggerableAnim("phase4", RawAnimation.begin().thenPlay("animation.kruemblegard.phase4_transition")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

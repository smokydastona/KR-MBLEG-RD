package com.kruemblegard.entity;

import com.kruemblegard.config.ModConfig;
import com.kruemblegard.entity.projectile.ArcaneStormProjectileEntity;
import com.kruemblegard.entity.projectile.MeteorArmEntity;
import com.kruemblegard.entity.projectile.RuneBoltEntity;
import com.kruemblegard.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

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
        private static final int ATTACK_ANIM_MELEE = 1;
        private static final int ATTACK_ANIM_RUNE_BOLT = 2;
        private static final int ATTACK_ANIM_DASH = 3;
        private static final int ATTACK_ANIM_METEOR_ARM = 4;
        private static final int ATTACK_ANIM_ARCANE_STORM = 5;

    // -----------------------------
    // BOSS BAR
    // -----------------------------
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.literal("Kr\u00FCmbleg\u00E5rd"),
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

    private static final RawAnimation ATTACK =
            RawAnimation.begin().thenPlay("animation.kruemblegard.attack");

        private static final RawAnimation ATTACK_MELEE =
            RawAnimation.begin().thenPlay("animation.kruemblegard.attack_melee");

        private static final RawAnimation ATTACK_RUNE_BOLT =
            RawAnimation.begin().thenPlay("animation.kruemblegard.attack_rune_bolt");

        private static final RawAnimation ATTACK_DASH =
            RawAnimation.begin().thenPlay("animation.kruemblegard.attack_dash");

        private static final RawAnimation ATTACK_METEOR_ARM =
            RawAnimation.begin().thenPlay("animation.kruemblegard.attack_meteor_arm");

        private static final RawAnimation ATTACK_ARCANE_STORM =
            RawAnimation.begin().thenPlay("animation.kruemblegard.attack_arcane_storm");

    // -----------------------------
    // ARENA / EMERGENCE (integration)
    // -----------------------------
    @Nullable
    private BlockPos arenaCenter;

    private boolean emerging;
    private double emergenceTargetY;

    private int attackAnimTicks;

    private int runeBoltCooldown;
    private int graviticPullCooldown;
    private int dashCooldown;
    private int meteorArmCooldown;
    private int arcaneStormCooldown;

    // -----------------------------
    // CONSTRUCTOR
    // -----------------------------
    public KruemblegardBossEntity(EntityType<? extends KruemblegardBossEntity> type, Level level) {
        super(type, level);
        this.xpReward = 50;
        this.noCulling = true;

        this.runeBoltCooldown = 0;
        this.graviticPullCooldown = 0;
        this.dashCooldown = 0;
        this.meteorArmCooldown = 0;
        this.arcaneStormCooldown = 0;
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

    // -----------------------------
    // AI GOALS
    // -----------------------------
    @Override
    protected void registerGoals() {

        // Movement
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6D));

        // Awareness
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
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

        // Phase logic
        if (!this.level().isClientSide) {
            float ratio = this.getHealth() / this.getMaxHealth();
            float phase2 = ModConfig.BOSS_PHASE_2_HEALTH_RATIO.get().floatValue();
            float phase3 = ModConfig.BOSS_PHASE_3_HEALTH_RATIO.get().floatValue();
            int phase = ratio <= phase3 ? 3 : ratio <= phase2 ? 2 : 1;

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
            }
        }
    }

    // -----------------------------
    // PHASE 1 — BASIC MELEE + SLOW MOVEMENT
    // -----------------------------
    private void phaseOneBehavior() {
        // Slow idle drifting — no special attacks
    }

    // -----------------------------
    // PHASE 2 — RUNE BOLTS + GRAVITIC PULL
    // -----------------------------
    private void phaseTwoBehavior() {
        if (this.getTarget() == null) return;

        if (this.graviticPullCooldown-- <= 0) {
            doGraviticPull();
            this.graviticPullCooldown = rollCooldown(ModConfig.BOSS_PHASE_2_GRAVITIC_PULL_COOLDOWN_TICKS.get());
        }

        if (this.runeBoltCooldown-- <= 0) {
            fireRuneBolt();
            this.runeBoltCooldown = rollCooldown(ModConfig.BOSS_PHASE_2_RUNE_BOLT_COOLDOWN_TICKS.get());
        }
    }

    // -----------------------------
    // PHASE 3 — DASH + METEOR ARM + ARCANE STORM
    // -----------------------------
    private void phaseThreeBehavior() {
        if (this.getTarget() == null) return;

        if (this.dashCooldown-- <= 0) {
            doDashAttack();
            this.dashCooldown = rollCooldown(ModConfig.BOSS_PHASE_3_DASH_COOLDOWN_TICKS.get());
        }

        if (this.meteorArmCooldown-- <= 0) {
            doMeteorArm();
            this.meteorArmCooldown = rollCooldown(ModConfig.BOSS_PHASE_3_METEOR_ARM_COOLDOWN_TICKS.get());
        }

        if (this.arcaneStormCooldown-- <= 0) {
            doArcaneStorm();
            this.arcaneStormCooldown = rollCooldown(ModConfig.BOSS_PHASE_3_ARCANE_STORM_COOLDOWN_TICKS.get());
        }
    }

    private void onPhaseChanged(int phase) {
        // Reset/seed cooldowns so a new phase doesn't instantly fire every ability on the same tick.
        if (phase <= 1) {
            this.runeBoltCooldown = 0;
            this.graviticPullCooldown = 0;
            this.dashCooldown = 0;
            this.meteorArmCooldown = 0;
            this.arcaneStormCooldown = 0;
            return;
        }

        if (phase == 2) {
            this.runeBoltCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_2_RUNE_BOLT_COOLDOWN_TICKS.get()));
            this.graviticPullCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_2_GRAVITIC_PULL_COOLDOWN_TICKS.get()));
            this.dashCooldown = 0;
            this.meteorArmCooldown = 0;
            this.arcaneStormCooldown = 0;
        } else {
            this.dashCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_3_DASH_COOLDOWN_TICKS.get()));
            this.meteorArmCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_3_METEOR_ARM_COOLDOWN_TICKS.get()));
            this.arcaneStormCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_3_ARCANE_STORM_COOLDOWN_TICKS.get()));
            this.runeBoltCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_2_RUNE_BOLT_COOLDOWN_TICKS.get()));
            this.graviticPullCooldown = rollCooldown(Math.max(10, ModConfig.BOSS_PHASE_2_GRAVITIC_PULL_COOLDOWN_TICKS.get()));
        }
    }

    private int rollCooldown(int baseTicks) {
        int base = Math.max(1, baseTicks);
        int jitter = Math.max(1, base / 5);
        return base + this.random.nextInt(jitter + 1);
    }

    // -----------------------------
    // ATTACK HOOKS (IMPLEMENTED IN MESSAGE 4)
    // -----------------------------
    private void fireRuneBolt() {
        if (this.getTarget() == null) return;

        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            this.setAttackAnim(ATTACK_ANIM_RUNE_BOLT, 14);
        }

        if (!this.level().isClientSide) {
            this.playSound(ModSounds.KRUEMBLEGARD_ATTACK.get(), 1.0f, 0.9f + (this.random.nextFloat() * 0.2f));
        }

        RuneBoltEntity bolt = new RuneBoltEntity(this.level(), this);
        bolt.setPos(this.getX(), this.getY() + 2, this.getZ());

        Vec3 dir = this.getTarget().position().subtract(this.position()).normalize().scale(0.6);
        bolt.setDeltaMovement(dir);

        this.level().addFreshEntity(bolt);
    }

    private void doGraviticPull() {
        if (this.getTarget() == null) return;

        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16))) {
            Vec3 pull = this.position().subtract(p.position()).normalize().scale(0.4);
            p.setDeltaMovement(p.getDeltaMovement().add(pull));
        }
    }

    private void doDashAttack() {
        if (this.getTarget() == null) return;

        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            this.setAttackAnim(ATTACK_ANIM_DASH, 10);
        }

        if (!this.level().isClientSide) {
            this.playSound(ModSounds.KRUEMBLEGARD_DASH.get(), 1.0f, 0.9f + (this.random.nextFloat() * 0.2f));
        }

        Vec3 dir = this.getTarget().position().subtract(this.position()).normalize().scale(1.5);
        this.setDeltaMovement(dir);

        // Damage on contact
        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(2))) {
            p.hurt(this.damageSources().mobAttack(this), 15f);
        }
    }

    private void doMeteorArm() {
        if (!this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            this.setAttackAnim(ATTACK_ANIM_METEOR_ARM, 16);
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
            this.setAttackAnim(ATTACK_ANIM_ARCANE_STORM, 24);
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

            if (moving) {
                return state.setAndContinue(MOVE);
            }
            return state.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(this, "attack", 0, state -> {
            int attackAnim = getAttackAnim();
            if (attackAnim != ATTACK_ANIM_NONE) {
                return switch (attackAnim) {
                    case ATTACK_ANIM_MELEE -> state.setAndContinue(ATTACK_MELEE);
                    case ATTACK_ANIM_RUNE_BOLT -> state.setAndContinue(ATTACK_RUNE_BOLT);
                    case ATTACK_ANIM_DASH -> state.setAndContinue(ATTACK_DASH);
                    case ATTACK_ANIM_METEOR_ARM -> state.setAndContinue(ATTACK_METEOR_ARM);
                    case ATTACK_ANIM_ARCANE_STORM -> state.setAndContinue(ATTACK_ARCANE_STORM);
                    default -> state.setAndContinue(ATTACK);
                };
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

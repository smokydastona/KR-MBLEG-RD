package com.kruemblegard.entity;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.kruemblegard.entity.projectile.GraveCairnStoneProjectileEntity;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.registry.ModProjectileEntities;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GraveCairnEntity extends Monster implements GeoEntity {

    private static final String NBT_DORMANT = "Dormant";
    private static final String NBT_WAVE_MASK = "WaveMask";
    private static final String NBT_FINAL_STAND = "FinalStand";

    private static final EntityDataAccessor<Boolean> DORMANT =
        SynchedEntityData.defineId(GraveCairnEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Byte> WAVE_MASK =
        SynchedEntityData.defineId(GraveCairnEntity.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Boolean> FINAL_STAND =
        SynchedEntityData.defineId(GraveCairnEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE_LOOP =
        RawAnimation.begin().thenLoop("animation.grave_cairn.idle");

    private static final RawAnimation WALK_LOOP =
        RawAnimation.begin().thenLoop("animation.grave_cairn.walk");

    private static final RawAnimation ATTACK_ONCE =
        RawAnimation.begin().thenPlay("animation.grave_cairn.attack");

    private static final RawAnimation HIT_ONCE =
        RawAnimation.begin().thenPlay("animation.grave_cairn.hit");

    private static final RawAnimation DIE_ONCE =
        RawAnimation.begin().thenPlay("animation.grave_cairn.die");

    private static final int ATTACK_ANIM_TICKS = 10;
    private static final int HIT_ANIM_TICKS = 8;

    private static final int DAMAGE_BURST_COOLDOWN_TICKS = 40;

    private static final int WAKE_RADIUS_BLOCKS = 8;
    private static final double PEBBLIT_SYNERGY_RADIUS = 12.0D;
    private static final int SUMMON_BUFF_TICKS = 20 * 30;

    private static final int SLAM_WINDUP_TICKS = 18;
    private static final int SLAM_COOLDOWN_TICKS = 60;
    private static final float SLAM_RADIUS = 3.25F;

    private static final int TOSS_WINDUP_TICKS = 14;
    private static final int TOSS_COOLDOWN_TICKS = 70;

    private static final int FINAL_STAND_HEALTH_PERCENT = 20;

    private static final AttributeModifier FINAL_STAND_SPEED_BOOST = new AttributeModifier(
        "kruemblegard:grave_cairn_final_stand_speed",
        0.30D,
        AttributeModifier.Operation.MULTIPLY_TOTAL
    );

    private static final AttributeModifier FINAL_STAND_KB_RESIST = new AttributeModifier(
        "kruemblegard:grave_cairn_final_stand_kb_resist",
        0.50D,
        AttributeModifier.Operation.ADDITION
    );

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackAnimTicks;
    private boolean wasSwinging;

    private int hitAnimTicks;
    private int damageBurstCooldownTicks;
    private boolean didDeathCollapse;

    private int stepSoundCooldownTicks;

    private int slamCooldownTicks;
    private int slamWindupTicks;

    private int tossCooldownTicks;
    private int tossWindupTicks;

    public GraveCairnEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DORMANT, true);
        this.entityData.define(WAVE_MASK, (byte) 0);
        this.entityData.define(FINAL_STAND, false);
    }

    public boolean isDormant() {
        return this.entityData.get(DORMANT);
    }

    private boolean isFinalStand() {
        return this.entityData.get(FINAL_STAND);
    }

    public void wakeFromDisturbance(@Nullable Player player) {
        if (level().isClientSide) {
            return;
        }

        if (!isDormant()) {
            return;
        }

        this.entityData.set(DORMANT, false);
        this.setNoAi(false);
        this.setPersistenceRequired();

        this.playSound(SoundEvents.STONE_PLACE, 0.8F, 0.7F + (this.random.nextFloat() * 0.2F));

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF, getX(), getY(0.6D), getZ(), 14, 0.4D, 0.25D, 0.4D, 0.02D);
        }

        // Player parameter is intentionally unused right now (reserved for future criteria triggers).
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.22D)
            .add(Attributes.ATTACK_DAMAGE, 7.0D)
            .add(Attributes.ARMOR, 5.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (isDormant()) {
                this.setNoAi(true);
                this.getNavigation().stop();
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                this.setTarget(null);

                Player nearby = level().getNearestPlayer(this, WAKE_RADIUS_BLOCKS);
                if (nearby != null && !nearby.isSpectator()) {
                    wakeFromDisturbance(nearby);
                }
            } else {
                this.setNoAi(false);
                tickSummoningAndSynergy();
                tickCombatActions();
            }
        }

        if (this.hitAnimTicks > 0) {
            --this.hitAnimTicks;
        }

        if (this.damageBurstCooldownTicks > 0) {
            --this.damageBurstCooldownTicks;
        }

        if (this.stepSoundCooldownTicks > 0) {
            --this.stepSoundCooldownTicks;
        }

        boolean swingingNow = this.swinging;
        if (swingingNow && !this.wasSwinging) {
            this.attackAnimTicks = ATTACK_ANIM_TICKS;
        }
        this.wasSwinging = swingingNow;

        if (this.attackAnimTicks > 0) {
            --this.attackAnimTicks;
        }
    }

    private void triggerHitCollapse() {
        this.hitAnimTicks = HIT_ANIM_TICKS;

        // A short stumble so it reads like a heap settling.
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0, true, false, true));
    }

    private void spawnDamageRubbleBurst() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockParticleOption burst = new BlockParticleOption(
            net.minecraft.core.particles.ParticleTypes.BLOCK,
            Blocks.COBBLESTONE.defaultBlockState()
        );

        serverLevel.sendParticles(burst, getX(), getY(0.85D), getZ(), 18, 0.55D, 0.35D, 0.55D, 0.18D);
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF, getX(), getY(0.85D), getZ(), 8, 0.35D, 0.20D, 0.35D, 0.02D);
    }

    private void maybeEjectPebblitOnHit(@Nullable LivingEntity attacker) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.damageBurstCooldownTicks > 0) {
            return;
        }

        // Avoid runaway entity counts.
        int nearbyPebblits = serverLevel.getEntitiesOfClass(PebblitEntity.class, getBoundingBox().inflate(6.0D)).size();
        if (nearbyPebblits >= 6) {
            return;
        }

        if (this.random.nextFloat() >= 0.35F) {
            return;
        }

        PebblitEntity pebblit = ModEntities.PEBBLIT.get().create(serverLevel);
        if (pebblit == null) {
            return;
        }

        double angle = this.random.nextDouble() * (Math.PI * 2.0D);
        double radius = 0.7D + (this.random.nextDouble() * 0.6D);
        double x = getX() + (Math.cos(angle) * radius);
        double z = getZ() + (Math.sin(angle) * radius);
        double y = getY() + 0.15D;

        pebblit.moveTo(x, y, z, this.getYRot(), 0.0F);
        pebblit.setDeltaMovement(
            Math.cos(angle) * 0.22D,
            0.22D + (this.random.nextDouble() * 0.10D),
            Math.sin(angle) * 0.22D
        );

        if (attacker != null) {
            pebblit.setTarget(attacker);
        }

        pebblit.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 10, 0, true, false, true));
        serverLevel.addFreshEntity(pebblit);

        BlockParticleOption dust = new BlockParticleOption(net.minecraft.core.particles.ParticleTypes.FALLING_DUST, Blocks.COBBLESTONE.defaultBlockState());
        serverLevel.sendParticles(dust, x, y + 0.1D, z, 10, 0.18D, 0.18D, 0.18D, 0.04D);

        this.damageBurstCooldownTicks = DAMAGE_BURST_COOLDOWN_TICKS;
    }

    private void doDeathCollapse(@Nullable LivingEntity killer) {
        if (this.didDeathCollapse) {
            return;
        }

        this.didDeathCollapse = true;

        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockParticleOption burst = new BlockParticleOption(
            net.minecraft.core.particles.ParticleTypes.BLOCK,
            Blocks.COBBLESTONE.defaultBlockState()
        );

        serverLevel.sendParticles(burst, getX(), getY(0.85D), getZ(), 60, 0.9D, 0.6D, 0.9D, 0.22D);
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD, getX(), getY(0.25D), getZ(), 22, 0.6D, 0.2D, 0.6D, 0.02D);
        this.playSound(SoundEvents.STONE_BREAK, 1.0F, 0.75F);

        // Final swarm: reinforces "it was just a pile... until it wasn't".
        int count = 4 + this.random.nextInt(4);
        for (int i = 0; i < count; i++) {
            PebblitEntity pebblit = ModEntities.PEBBLIT.get().create(serverLevel);
            if (pebblit == null) {
                continue;
            }

            double angle = (Math.PI * 2.0D) * (i / (double) count);
            double radius = 0.6D + (this.random.nextDouble() * 0.9D);
            double x = getX() + (Math.cos(angle) * radius);
            double z = getZ() + (Math.sin(angle) * radius);
            double y = getY() + 0.15D;

            pebblit.moveTo(x, y, z, this.getYRot(), 0.0F);
            pebblit.setDeltaMovement(
                Math.cos(angle) * 0.18D,
                0.18D + (this.random.nextDouble() * 0.10D),
                Math.sin(angle) * 0.18D
            );

            if (killer != null) {
                pebblit.setTarget(killer);
            }

            pebblit.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 0, true, false, true));
            serverLevel.addFreshEntity(pebblit);
        }
    }

    private void tickSummoningAndSynergy() {
        if (isFinalStand()) {
            applyPebblitSynergy();
            return;
        }

        float max = getMaxHealth();
        if (max <= 0.0F) {
            return;
        }

        float percent = (getHealth() / max) * 100.0F;

        if (percent <= FINAL_STAND_HEALTH_PERCENT) {
            enterFinalStand();
            applyPebblitSynergy();
            return;
        }

        byte mask = this.entityData.get(WAVE_MASK);

        if (percent <= 25.0F && (mask & 0x04) == 0) {
            summonPebblitWave();
            this.entityData.set(WAVE_MASK, (byte) (mask | 0x04));
        } else if (percent <= 50.0F && (mask & 0x02) == 0) {
            summonPebblitWave();
            this.entityData.set(WAVE_MASK, (byte) (mask | 0x02));
        } else if (percent <= 75.0F && (mask & 0x01) == 0) {
            summonPebblitWave();
            this.entityData.set(WAVE_MASK, (byte) (mask | 0x01));
        }

        applyPebblitSynergy();
    }

    private void enterFinalStand() {
        if (isFinalStand()) {
            return;
        }

        this.entityData.set(FINAL_STAND, true);

        Optional.ofNullable(getAttribute(Attributes.MOVEMENT_SPEED))
            .filter(attr -> !attr.hasModifier(FINAL_STAND_SPEED_BOOST))
            .ifPresent(attr -> attr.addTransientModifier(FINAL_STAND_SPEED_BOOST));

        Optional.ofNullable(getAttribute(Attributes.KNOCKBACK_RESISTANCE))
            .filter(attr -> !attr.hasModifier(FINAL_STAND_KB_RESIST))
            .ifPresent(attr -> attr.addTransientModifier(FINAL_STAND_KB_RESIST));

        this.playSound(SoundEvents.STONE_BREAK, 0.9F, 0.6F + (this.random.nextFloat() * 0.15F));

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, getX(), getY(0.9D), getZ(), 20, 0.5D, 0.45D, 0.5D, 0.05D);
        }
    }

    private void summonPebblitWave() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        this.playSound(SoundEvents.STONE_FALL, 1.0F, 0.65F + (this.random.nextFloat() * 0.15F));

        BlockParticleOption blockBurst = new BlockParticleOption(net.minecraft.core.particles.ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState());
        serverLevel.sendParticles(blockBurst, getX(), getY(0.9D), getZ(), 24, 0.55D, 0.4D, 0.55D, 0.15D);

        int count = 2 + this.random.nextInt(3);
        LivingEntity currentTarget = getTarget();

        for (int i = 0; i < count; i++) {
            PebblitEntity pebblit = ModEntities.PEBBLIT.get().create(serverLevel);
            if (pebblit == null) {
                continue;
            }

            double angle = (Math.PI * 2.0D) * (i / (double) count);
            double radius = 0.7D + (this.random.nextDouble() * 0.5D);
            double x = getX() + (Math.cos(angle) * radius);
            double z = getZ() + (Math.sin(angle) * radius);
            double y = getY() + 0.15D;

            pebblit.moveTo(x, y, z, this.getYRot(), 0.0F);
            pebblit.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, SUMMON_BUFF_TICKS, 0, true, false, true));
            pebblit.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, SUMMON_BUFF_TICKS, 0, true, false, true));

            if (currentTarget != null) {
                pebblit.setTarget(currentTarget);
            }

            serverLevel.addFreshEntity(pebblit);

            BlockParticleOption dust = new BlockParticleOption(net.minecraft.core.particles.ParticleTypes.FALLING_DUST, Blocks.COBBLESTONE.defaultBlockState());
            serverLevel.sendParticles(dust, x, y + 0.1D, z, 8, 0.15D, 0.15D, 0.15D, 0.03D);
        }
    }

    private void applyPebblitSynergy() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if ((this.tickCount % 40) != 0) {
            return;
        }

        LivingEntity currentTarget = getTarget();

        for (PebblitEntity pebblit : serverLevel.getEntitiesOfClass(PebblitEntity.class, getBoundingBox().inflate(PEBBLIT_SYNERGY_RADIUS))) {
            if (!pebblit.isAlive()) {
                continue;
            }

            pebblit.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, true, false, true));

            if (currentTarget != null && pebblit.getTarget() == null) {
                pebblit.setTarget(currentTarget);
            }
        }
    }

    private void tickCombatActions() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        if (slamCooldownTicks > 0) {
            slamCooldownTicks--;
        }
        if (tossCooldownTicks > 0) {
            tossCooldownTicks--;
        }

        if (slamWindupTicks > 0) {
            slamWindupTicks--;
            if (slamWindupTicks == 5) {
                performSlam();
            }
            return;
        }

        if (tossWindupTicks > 0) {
            tossWindupTicks--;
            if (tossWindupTicks == 4) {
                performToss(target);
            }
            return;
        }

        double distSqr = this.distanceToSqr(target);
        if (slamCooldownTicks <= 0 && distSqr <= (3.6D * 3.6D)) {
            slamWindupTicks = SLAM_WINDUP_TICKS;
            slamCooldownTicks = SLAM_COOLDOWN_TICKS;
            this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            this.playSound(SoundEvents.STONE_STEP, 0.6F, 0.6F);
            return;
        }

        if (tossCooldownTicks <= 0 && distSqr >= (6.0D * 6.0D) && distSqr <= (14.0D * 14.0D) && this.hasLineOfSight(target)) {
            tossWindupTicks = TOSS_WINDUP_TICKS;
            tossCooldownTicks = TOSS_COOLDOWN_TICKS;
            this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            this.playSound(SoundEvents.STONE_PLACE, 0.6F, 0.7F);
        }
    }

    private void performSlam() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        this.playSound(SoundEvents.ANVIL_LAND, 0.55F, 0.95F);
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD, getX(), getY(), getZ(), 12, 0.45D, 0.10D, 0.45D, 0.02D);

        AABB area = getBoundingBox().inflate(SLAM_RADIUS, 0.75D, SLAM_RADIUS);
        for (LivingEntity victim : serverLevel.getEntitiesOfClass(LivingEntity.class, area, e -> e != this && e.isAlive() && !e.isAlliedTo(this))) {
            victim.hurt(this.damageSources().mobAttack(this), 6.0F);

            Vec3 push = victim.position().subtract(this.position());
            double dx = push.x;
            double dz = push.z;
            double len = Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));
            double strength = 0.9D;
            victim.push((dx / len) * strength, 0.25D, (dz / len) * strength);
        }
    }

    private void performToss(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        GraveCairnStoneProjectileEntity proj = new GraveCairnStoneProjectileEntity(ModProjectileEntities.graveCairnStoneType(), serverLevel, this);
        proj.setPos(getX(), getEyeY() - 0.1D, getZ());

        double tx = target.getX();
        double ty = target.getY(0.33D);
        double tz = target.getZ();

        double dx = tx - getX();
        double dy = ty - proj.getY();
        double dz = tz - getZ();

        proj.shoot(dx, dy, dz, 1.05F, 1.0F);
        serverLevel.addFreshEntity(proj);

        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, proj.getX(), proj.getY(), proj.getZ(), 6, 0.1D, 0.1D, 0.1D, 0.03D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.85D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) {
        return net.minecraft.sounds.SoundEvents.STONE_HIT;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.STONE_BREAK;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        if (isDormant()) {
            return null;
        }
        return SoundEvents.STONE_STEP;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        // Don't sound like a walking creature; more like a dragging heap.
        if (this.stepSoundCooldownTicks > 0) {
            return;
        }

        this.stepSoundCooldownTicks = 6 + this.random.nextInt(6);
        this.playSound(net.minecraft.sounds.SoundEvents.STONE_STEP, 0.18F, 0.75F + (this.random.nextFloat() * 0.25F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            boolean dead = this.isDeadOrDying() || this.getHealth() <= 0.0F;
            if (dead) {
                state.setAnimation(DIE_ONCE);
            } else if (this.hitAnimTicks > 0) {
                state.setAnimation(HIT_ONCE);
            } else if (this.attackAnimTicks > 0) {
                state.setAnimation(ATTACK_ONCE);
            } else {
                state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && isDormant()) {
            wakeFromDisturbance(source.getEntity() instanceof Player p ? p : null);
        }

        boolean result = super.hurt(source, amount);

        if (!level().isClientSide && result && amount > 0.0F && this.isAlive()) {
            LivingEntity attacker = (source.getEntity() instanceof LivingEntity le) ? le : null;
            triggerHitCollapse();
            spawnDamageRubbleBurst();
            maybeEjectPebblitOnHit(attacker);
        }

        return result;
    }

    @Override
    public void die(DamageSource source) {
        LivingEntity killer = (source.getEntity() instanceof LivingEntity le) ? le : null;
        doDeathCollapse(killer);
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(NBT_DORMANT, isDormant());
        tag.putByte(NBT_WAVE_MASK, this.entityData.get(WAVE_MASK));
        tag.putBoolean(NBT_FINAL_STAND, isFinalStand());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(NBT_DORMANT)) {
            this.entityData.set(DORMANT, tag.getBoolean(NBT_DORMANT));
        }

        if (tag.contains(NBT_WAVE_MASK)) {
            this.entityData.set(WAVE_MASK, tag.getByte(NBT_WAVE_MASK));
        }

        if (tag.contains(NBT_FINAL_STAND)) {
            this.entityData.set(FINAL_STAND, tag.getBoolean(NBT_FINAL_STAND));
        }
    }
}

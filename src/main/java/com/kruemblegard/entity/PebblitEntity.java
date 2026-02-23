package com.kruemblegard.entity;

import java.util.Optional;
import java.util.UUID;

import com.kruemblegard.init.ModCriteria;
import com.kruemblegard.registry.ModSounds;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.kruemblegard.registry.ModItems;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PebblitEntity extends Silverfish implements GeoEntity {

    private static final String NBT_TAMED = "Tamed";
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_SITTING = "Sitting";
    private static final String NBT_SHOULDER_LOCKED = "ShoulderLocked";

    private static final float ATTACK_KNOCKBACK_STRENGTH = 0.6F;

    private static final int ANGRY_REFRESH_TICKS = 200;

    private static final int CALL_DURATION_TICKS = 15;
    private static final double CALL_BROADCAST_RADIUS = 18.0D;

    private static final EntityDataAccessor<Boolean> TAMED =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.OPTIONAL_UUID);

        private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.BOOLEAN);

        private static final EntityDataAccessor<Boolean> SHOULDER_LOCKED =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.BOOLEAN);

        private static final EntityDataAccessor<Integer> ANGRY_TICKS =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.INT);

        private static final EntityDataAccessor<Boolean> CALLING =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.BOOLEAN);

        private static final RawAnimation IDLE_LOOP =
            RawAnimation.begin().thenLoop("animation.pebblit.idle");

        private static final RawAnimation WALK_LOOP =
            RawAnimation.begin().thenLoop("animation.pebblit.walk");

        private static final RawAnimation ANGRY_IDLE_LOOP =
            RawAnimation.begin().thenLoop("animation.pebblit.angry_idle");

        private static final RawAnimation ANGRY_WALK_LOOP =
            RawAnimation.begin().thenLoop("animation.pebblit.angry_walk");

        private static final RawAnimation SIT_LOOP =
            RawAnimation.begin().thenLoop("animation.pebblit.sit");

        private static final RawAnimation PERCHING_ONCE =
            RawAnimation.begin().thenPlay("animation.pebblit.perching");

        private static final RawAnimation PERCHED_LOOP =
            RawAnimation.begin().thenLoop("animation.pebblit.perched");

        private static final RawAnimation ATTACK_ONCE =
            RawAnimation.begin().thenPlay("animation.pebblit.attack");

        private static final RawAnimation CALL_ONCE =
            RawAnimation.begin().thenPlay("animation.pebblit.call");

        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean wasPerchedLastTick = false;
    private boolean playPerchingOnce = false;

    private int callTicksRemaining = 0;

    @Nullable
    private UUID callAggressorUuid = null;

    public PebblitEntity(EntityType<? extends Silverfish> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TAMED, false);
        this.entityData.define(OWNER_UUID, Optional.empty());
        this.entityData.define(SITTING, false);
        this.entityData.define(SHOULDER_LOCKED, false);
        this.entityData.define(ANGRY_TICKS, 0);
        this.entityData.define(CALLING, false);
    }

    public boolean isCalling() {
        return this.entityData.get(CALLING);
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY_TICKS) > 0;
    }

    private void refreshAngry() {
        int current = this.entityData.get(ANGRY_TICKS);
        if (current < ANGRY_REFRESH_TICKS) {
            this.entityData.set(ANGRY_TICKS, ANGRY_REFRESH_TICKS);
        }
    }

    private void startCall(@Nullable LivingEntity aggressor, boolean broadcast) {
        if (level().isClientSide) return;
        if (isTamed()) return;
        if (isCalling()) return;

        this.entityData.set(CALLING, true);
        this.callTicksRemaining = CALL_DURATION_TICKS;
        this.callAggressorUuid = aggressor != null ? aggressor.getUUID() : null;

        // The call is a pre-aggro warning; do not become hostile until the call completes.
        setTarget(null);
        if (this.entityData.get(ANGRY_TICKS) != 0) {
            this.entityData.set(ANGRY_TICKS, 0);
        }

        triggerAnim("actionController", "call");

        if (broadcast && aggressor != null) {
            for (PebblitEntity other : level().getEntitiesOfClass(
                    PebblitEntity.class,
                    getBoundingBox().inflate(CALL_BROADCAST_RADIUS),
                    p -> p != this && !p.isTamed() && !p.isCalling())) {
                other.startCall(aggressor, false);
            }
        }
    }

    private void stopCalling() {
        if (!isCalling()) return;
        this.entityData.set(CALLING, false);
        this.callTicksRemaining = 0;
        this.callAggressorUuid = null;
    }

    private void finishCall() {
        if (!isCalling()) return;

        this.entityData.set(CALLING, false);
        this.callTicksRemaining = 0;

        LivingEntity aggressor = null;
        if (callAggressorUuid != null && level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(callAggressorUuid);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                aggressor = living;
            }
        }

        callAggressorUuid = null;

        refreshAngry();
        if (aggressor != null) {
            setTarget(aggressor);
        }
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    private void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);

        if (sitting) {
            getNavigation().stop();
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        }
    }

    public boolean isShoulderLocked() {
        return this.entityData.get(SHOULDER_LOCKED);
    }

    private void setShoulderLocked(boolean shoulderLocked) {
        this.entityData.set(SHOULDER_LOCKED, shoulderLocked);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    @Nullable
    public LivingEntity getOwner() {
        UUID uuid = getOwnerUUID();
        if (uuid == null) return null;

        if (level() instanceof ServerLevel serverLevel) {
            return serverLevel.getPlayerByUUID(uuid);
        }

        return null;
    }

    private void tame(Player player) {
        this.entityData.set(TAMED, true);
        this.entityData.set(OWNER_UUID, Optional.of(player.getUUID()));
        this.setPersistenceRequired();

        this.playSound(ModSounds.PEBBLIT_TAME.get(), 0.8F, 0.95F + (this.random.nextFloat() * 0.1F));

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART,
                    getX(), getY(0.5D), getZ(),
                    6, 0.3D, 0.3D, 0.3D, 0.05D);
        }
    }

    @Override
    protected void registerGoals() {
        // Intentionally do NOT call super.registerGoals(). Silverfish adds always-hostile targeting.

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // If tamed, follow the owner around.
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.1D, 8.0F, 3.0F));

        // Retaliate when hurt (but do not auto-alert others; group behavior is handled by the call).
        this.targetSelector.addGoal(1, new PebblitHurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        boolean perchedNow = isTamed() && isPassenger() && getVehicle() instanceof Player;
        if (perchedNow && !wasPerchedLastTick) {
            playPerchingOnce = true;

            if (!level().isClientSide) {
                this.playSound(ModSounds.PEBBLIT_PERCH.get(), 0.75F, 0.95F + (this.random.nextFloat() * 0.1F));
            }
        }
        wasPerchedLastTick = perchedNow;

        if (!level().isClientSide) {
            if ((isTamed() || perchedNow) && isCalling()) {
                stopCalling();
            }

            if (isCalling()) {
                if (callTicksRemaining > 0) {
                    callTicksRemaining--;
                }

                // Freeze during the call so it reads clearly as a pre-aggro warning.
                getNavigation().stop();
                setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);

                // Ensure we do not appear "angry" until the call completes.
                if (this.entityData.get(ANGRY_TICKS) != 0) {
                    this.entityData.set(ANGRY_TICKS, 0);
                }

                if (callTicksRemaining <= 0) {
                    finishCall();
                }
            }

            if (perchedNow) {
                // While perched, keep the Pebblit calm to avoid odd shoulder visuals.
                if (this.entityData.get(ANGRY_TICKS) != 0) {
                    this.entityData.set(ANGRY_TICKS, 0);
                }
            } else {
                if (!isCalling() && getTarget() != null) {
                    refreshAngry();
                } else {
                    int ticks = this.entityData.get(ANGRY_TICKS);
                    if (ticks > 0) {
                        this.entityData.set(ANGRY_TICKS, ticks - 1);
                    }
                }
            }
        }

        if (!level().isClientSide && isTamed() && isSitting() && !isPassenger()) {
            // "Sit and stay" - keep it rooted in place while still allowing it to attack if something comes close.
            getNavigation().stop();
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        }

        if (!level().isClientSide && isTamed() && isShoulderLocked() && !isPassenger()) {
            LivingEntity owner = getOwner();
            if (owner instanceof Player ownerPlayer && ownerPlayer.isAlive() && !ownerPlayer.isSpectator()) {
                if (distanceToSqr(ownerPlayer) < (16.0D * 16.0D)) {
                    startRiding(ownerPlayer, true);
                }
            }
        }

        if (!level().isClientSide && isTamed()) {
            // Keep it from staying aggressive to players once tamed.
            if (getTarget() instanceof Player) {
                setTarget(null);
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.PEBBLIT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return ModSounds.PEBBLIT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.PEBBLIT_DEATH.get();
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(ModSounds.PEBBLIT_STEP.get(), 0.12F, 1.0F + (this.random.nextFloat() * 0.2F));
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (!level().isClientSide) {
            // During the call, Pebblits are immune to all damage.
            if (isCalling()) {
                return false;
            }

            if (!isTamed() && !isPassenger() && source.getEntity() instanceof LivingEntity attacker) {
                // Pre-aggro: call first, then become hostile after the call completes.
                if (!isAngry()) {
                    startCall(attacker, true);
                    return false;
                }
            }
        }

        boolean didHurt = super.hurt(source, amount);

        if (didHurt && !level().isClientSide && !isTamed() && source.getEntity() instanceof LivingEntity attacker) {
            refreshAngry();
            if (!isCalling()) {
                setTarget(attacker);
            }
        }

        return didHurt;
    }

    @Override
    public void rideTick() {
        super.rideTick();

        if (!isTamed() || !isPassenger() || !(getVehicle() instanceof Player rider)) {
            return;
        }

        // Keep the Pebblit visually perched on the player's shoulder.
        // Do this on both client and server so it actually renders correctly.
        float yawRad = (float) Math.toRadians(rider.getYRot());
        double sideOffset = 0.35D;
        double backOffset = -0.10D;

        HumanoidArm mainArm = rider.getMainArm();
        double sideSign = (mainArm == HumanoidArm.LEFT) ? -1.0D : 1.0D;

        double xOff = -Math.sin(yawRad) * backOffset + Math.cos(yawRad) * (sideOffset * sideSign);
        double zOff = Math.cos(yawRad) * backOffset + Math.sin(yawRad) * (sideOffset * sideSign);

        setPos(rider.getX() + xOff, rider.getY() + rider.getBbHeight() - 0.15D, rider.getZ() + zOff);
        setYRot(rider.getYRot());
        setXRot(0.0F);
        setDeltaMovement(0.0D, 0.0D, 0.0D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level().isClientSide && !isTamed() && stack.is(ModItems.ECHOKERN.get())) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            tame(player);
            return InteractionResult.CONSUME;
        }

        if (!level().isClientSide && isTamed() && isAlliedTo(player) && stack.isEmpty()) {
            // Non-shift: toggle "sit and stay".
            // Shift: perch on shoulder until death (no manual un-perch).

            if (isPassenger()) {
                // Don't change behavior while already perched.
                return InteractionResult.CONSUME;
            }

            if (player.isCrouching()) {
                // Permanent perch.
                setSitting(false);
                setShoulderLocked(true);

                boolean mounted = startRiding(player, true);
                if (mounted && player instanceof ServerPlayer serverPlayer) {
                    ModCriteria.PEBBLIT_SHOULDER.trigger(serverPlayer);
                }

                return InteractionResult.CONSUME;
            }

            // Toggle sit.
            if (!isShoulderLocked()) {
                setSitting(!isSitting());
            }
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean didHurt = super.doHurtTarget(target);

        if (didHurt && target instanceof LivingEntity livingTarget) {
            double dx = livingTarget.getX() - getX();
            double dz = livingTarget.getZ() - getZ();
            livingTarget.knockback(ATTACK_KNOCKBACK_STRENGTH, dx, dz);

            if (!level().isClientSide && isAngry() && !isPassenger()) {
                triggerAnim("actionController", "attack");
            }
        }

        return didHurt;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(NBT_TAMED, isTamed());
        tag.putBoolean(NBT_SITTING, isSitting());
        tag.putBoolean(NBT_SHOULDER_LOCKED, isShoulderLocked());

        UUID owner = getOwnerUUID();
        if (owner != null) {
            tag.putUUID(NBT_OWNER, owner);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(NBT_TAMED)) {
            this.entityData.set(TAMED, tag.getBoolean(NBT_TAMED));
        }

        if (tag.hasUUID(NBT_OWNER)) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID(NBT_OWNER)));
        }

        if (tag.contains(NBT_SITTING)) {
            this.entityData.set(SITTING, tag.getBoolean(NBT_SITTING));
        }

        if (tag.contains(NBT_SHOULDER_LOCKED)) {
            this.entityData.set(SHOULDER_LOCKED, tag.getBoolean(NBT_SHOULDER_LOCKED));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            boolean perched = isTamed() && isPassenger() && getVehicle() instanceof Player;

            if (perched) {
                if (playPerchingOnce) {
                    state.setAnimation(PERCHING_ONCE);
                    playPerchingOnce = false;
                } else {
                    state.setAnimation(PERCHED_LOOP);
                }

                return PlayState.CONTINUE;
            }

            if (isTamed() && isSitting()) {
                state.setAnimation(SIT_LOOP);
                return PlayState.CONTINUE;
            }

            if (isAngry()) {
                state.setAnimation(state.isMoving() ? ANGRY_WALK_LOOP : ANGRY_IDLE_LOOP);
                return PlayState.CONTINUE;
            }

            state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ONCE)
                .triggerableAnim("call", CALL_ONCE));
    }

    private static final class PebblitHurtByTargetGoal extends HurtByTargetGoal {
        private final PebblitEntity pebblit;

        private PebblitHurtByTargetGoal(PebblitEntity pebblit) {
            super(pebblit);
            this.pebblit = pebblit;
        }

        @Override
        public boolean canUse() {
            if (pebblit.isCalling()) return false;
            return super.canUse();
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        if (isTamed() && other instanceof Player p) {
            UUID owner = getOwnerUUID();
            return owner != null && owner.equals(p.getUUID());
        }

        return super.isAlliedTo(other);
    }

    private static final class FollowOwnerGoal extends Goal {
        private final PebblitEntity pebblit;
        private final double speed;
        private final float startDistance;
        private final float stopDistance;

        @Nullable
        private LivingEntity owner;

        private int timeToRecalcPath;

        private FollowOwnerGoal(PebblitEntity pebblit, double speed, float startDistance, float stopDistance) {
            this.pebblit = pebblit;
            this.speed = speed;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
        }

        @Override
        public boolean canUse() {
            if (!pebblit.isTamed()) return false;
            if (pebblit.isPassenger() || pebblit.isSitting() || pebblit.isShoulderLocked()) return false;

            owner = pebblit.getOwner();
            if (owner == null) return false;

            if (owner.isSpectator() || !owner.isAlive()) return false;

            return pebblit.distanceToSqr(owner) > (double) (startDistance * startDistance);
        }

        @Override
        public boolean canContinueToUse() {
            if (!pebblit.isTamed()) return false;
            if (pebblit.isPassenger() || pebblit.isSitting() || pebblit.isShoulderLocked()) return false;
            if (owner == null || !owner.isAlive()) return false;

            return pebblit.distanceToSqr(owner) > (double) (stopDistance * stopDistance);
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            pebblit.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (owner == null) return;

            if (--timeToRecalcPath <= 0) {
                timeToRecalcPath = 10;
                pebblit.getNavigation().moveTo(owner, speed);
            }
        }
    }
}

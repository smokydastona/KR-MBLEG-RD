package com.kruemblegard.entity;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

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

    private static final float ATTACK_KNOCKBACK_STRENGTH = 0.6F;

    private static final EntityDataAccessor<Boolean> TAMED =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.OPTIONAL_UUID);

        private static final RawAnimation IDLE_LOOP =
            RawAnimation.begin().thenLoop("animation.pebblit.idle");

        private static final RawAnimation WALK_LOOP =
            RawAnimation.begin().thenLoop("animation.pebblit.walk");

        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PebblitEntity(EntityType<? extends Silverfish> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TAMED, false);
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
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

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART,
                    getX(), getY(0.5D), getZ(),
                    6, 0.3D, 0.3D, 0.3D, 0.05D);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // If tamed, follow the owner around.
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.1D, 8.0F, 3.0F));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && isTamed() && isPassenger() && getVehicle() instanceof Player rider) {
            // Keep the Pebblit visually perched on the player's shoulder.
            // We use riding mechanics (instead of vanilla shoulder NBT) so it works reliably with our mappings.
            float yawRad = (float) Math.toRadians(rider.getYRot());
            double sideOffset = 0.35D;
            double backOffset = -0.10D;

            double xOff = -Math.sin(yawRad) * backOffset + Math.cos(yawRad) * sideOffset;
            double zOff = Math.cos(yawRad) * backOffset + Math.sin(yawRad) * sideOffset;

            setPos(rider.getX() + xOff, rider.getY() + rider.getBbHeight() - 0.15D, rider.getZ() + zOff);
            setYRot(rider.getYRot());
            setXRot(0.0F);
        }

        if (!level().isClientSide && isTamed()) {
            // Keep it from staying aggressive to players once tamed.
            if (getTarget() instanceof Player) {
                setTarget(null);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level().isClientSide && !isTamed() && stack.is(Items.COBBLESTONE)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            tame(player);
            return InteractionResult.CONSUME;
        }

        if (!level().isClientSide && isTamed() && isAlliedTo(player) && stack.isEmpty()) {
            // Toggle perching on the owner's shoulder.
            if (isPassenger() && getVehicle() == player) {
                stopRiding();
                moveTo(player.getX(), player.getY(), player.getZ(), getYRot(), getXRot());
            } else {
                startRiding(player, true);
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
        }

        return didHurt;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(NBT_TAMED, isTamed());

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
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));
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

            owner = pebblit.getOwner();
            if (owner == null) return false;

            if (owner.isSpectator() || !owner.isAlive()) return false;

            return pebblit.distanceToSqr(owner) > (double) (startDistance * startDistance);
        }

        @Override
        public boolean canContinueToUse() {
            if (!pebblit.isTamed()) return false;
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

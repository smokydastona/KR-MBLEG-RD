package com.kruemblegard.entity;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
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

public class PebblitEntity extends Silverfish {

    private static final EntityDataAccessor<Boolean> TAMED =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(PebblitEntity.class, EntityDataSerializers.OPTIONAL_UUID);

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

        return super.mobInteract(player, hand);
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

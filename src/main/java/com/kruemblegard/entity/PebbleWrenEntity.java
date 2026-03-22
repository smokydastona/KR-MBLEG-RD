package com.kruemblegard.entity;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.Tags;

import com.kruemblegard.registry.ModEntities;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PebbleWrenEntity extends TamableAnimal implements GeoEntity {

    private static final int TEXTURE_VARIANTS = 14;
    private static final String NBT_TEXTURE_VARIANT = "TextureVariant";

    private static final EntityDataAccessor<Integer> DATA_TEXTURE_VARIANT = SynchedEntityData.defineId(
        PebbleWrenEntity.class,
        EntityDataSerializers.INT
    );

    private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(
        PebbleWrenEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    private static final RawAnimation IDLE_LOOP =
        RawAnimation.begin().thenLoop("animation.pebble_wren.idle");

    private static final RawAnimation WALK_LOOP =
        RawAnimation.begin().thenLoop("animation.pebble_wren.walk");

    private static final RawAnimation FLY_LOOP =
        RawAnimation.begin().thenLoop("animation.pebble_wren.fly");

    private static final RawAnimation DISPLAY_LOOP =
        RawAnimation.begin().thenLoop("animation.pebble_wren.display");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int ORE_FIND_COOLDOWN_TICKS = 20 * 8;
    private static final int ORE_FIND_SAMPLE_ATTEMPTS = 90;
    private static final int ORE_FIND_RADIUS = 14;
    private static final int ORE_FIND_Y_RANGE = 8;

    private static final byte ENTITY_EVENT_DISPLAY = 15;
    private static final int DISPLAY_ANIM_TICKS = 20 * 3;

    private static final double FLOCK_SCAN_RADIUS = 14.0D;
    private static final double FLOCK_RETURN_DISTANCE = 12.0D;
    private static final double FLOCK_MAX_WANDER_RADIUS = 8.0D;
    private static final int MIN_AIRBORNE_TICKS = 14;
    private static final int MAX_AIRBORNE_TICKS = 20 * 3;
    private static final int MIN_GROUNDED_TICKS = 8;
    private static final int MAX_GROUNDED_TICKS = 20 * 2;
    private static final double OWNER_TAKEOFF_DISTANCE = 5.5D;
    private static final double FLOCK_TAKEOFF_DISTANCE = 6.5D;

    private int oreFindCooldownTicks = 0;
    private int displayAnimTicks = 0;
    private int airborneTicks = 0;
    private int groundedTicks = 0;
    private int flightPhaseTargetTicks = MIN_GROUNDED_TICKS;

    public PebbleWrenEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 12, false);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.beginGroundPhase();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TEXTURE_VARIANT, 1);
        this.entityData.define(DATA_FLYING, false);
    }

    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    private void setFlying(boolean flying) {
        this.entityData.set(DATA_FLYING, flying);
        this.setNoGravity(flying);
        if (flying) {
            this.fallDistance = 0.0F;
        }
    }

    public int getTextureVariant() {
        int variant = this.entityData.get(DATA_TEXTURE_VARIANT);
        if (variant < 1) {
            return 1;
        }
        if (variant > TEXTURE_VARIANTS) {
            return TEXTURE_VARIANTS;
        }
        return variant;
    }

    private void setTextureVariant(int variant) {
        if (variant < 1) {
            variant = 1;
        } else if (variant > TEXTURE_VARIANTS) {
            variant = TEXTURE_VARIANTS;
        }
        this.entityData.set(DATA_TEXTURE_VARIANT, variant);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        net.minecraft.world.DifficultyInstance difficulty,
        MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData,
        @Nullable net.minecraft.nbt.CompoundTag dataTag
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, dataTag);
        this.setOrderedToSit(false);
        this.setTextureVariant(this.random.nextInt(TEXTURE_VARIANTS) + 1);
        return data;
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_TEXTURE_VARIANT, getTextureVariant());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(NBT_TEXTURE_VARIANT)) {
            setTextureVariant(tag.getInt(NBT_TEXTURE_VARIANT));
        }

        this.beginGroundPhase();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 6.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.30D)
            .add(Attributes.FLYING_SPEED, 0.42D)
            .add(Attributes.FOLLOW_RANGE, 12.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.35D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(Items.WHEAT_SEEDS), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.15D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new PebbleWrenFlockCohesionGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new PebbleWrenAirWanderGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
    }

    private boolean shouldTakeOff() {
        if (this.isOrderedToSit() || this.isPassenger() || this.isInWaterOrBubble()) {
            return false;
        }

        if (!this.onGround()) {
            return true;
        }

        if (this.getTarget() != null) {
            return true;
        }

        if (this.isTame() && this.getOwner() != null) {
            if (this.distanceToSqr(this.getOwner()) > (OWNER_TAKEOFF_DISTANCE * OWNER_TAKEOFF_DISTANCE)) {
                return true;
            }
        } else {
            Vec3 focus = this.getFlockFocusPoint();
            if (this.position().distanceToSqr(focus) > (FLOCK_TAKEOFF_DISTANCE * FLOCK_TAKEOFF_DISTANCE)) {
                return true;
            }
        }

        return this.groundedTicks >= this.flightPhaseTargetTicks && this.random.nextInt(10) == 0;
    }

    private boolean shouldLand() {
        if (this.isOrderedToSit() || this.isPassenger() || this.isInWaterOrBubble()) {
            return true;
        }

        if (this.getTarget() != null) {
            return false;
        }

        if (this.airborneTicks < MIN_AIRBORNE_TICKS) {
            return false;
        }

        if (this.isTame() && this.getOwner() != null) {
            if (this.distanceToSqr(this.getOwner()) > (OWNER_TAKEOFF_DISTANCE * OWNER_TAKEOFF_DISTANCE)) {
                return false;
            }
        } else {
            Vec3 focus = this.getFlockFocusPoint();
            if (this.position().distanceToSqr(focus) > (FLOCK_TAKEOFF_DISTANCE * FLOCK_TAKEOFF_DISTANCE)) {
                return false;
            }
        }

        return this.airborneTicks >= this.flightPhaseTargetTicks && (this.onGround() || this.random.nextInt(12) == 0);
    }

    private void beginFlightPhase() {
        this.airborneTicks = 0;
        this.flightPhaseTargetTicks = MIN_AIRBORNE_TICKS + this.random.nextInt((MAX_AIRBORNE_TICKS - MIN_AIRBORNE_TICKS) + 1);
    }

    private void beginGroundPhase() {
        this.groundedTicks = 0;
        this.flightPhaseTargetTicks = MIN_GROUNDED_TICKS + this.random.nextInt((MAX_GROUNDED_TICKS - MIN_GROUNDED_TICKS) + 1);
    }

    private Vec3 getFlockFocusPoint() {
        Vec3 focus = this.position();

        AABB scan = this.getBoundingBox().inflate(FLOCK_SCAN_RADIUS);
        List<PebbleWrenEntity> mates = this.level().getEntitiesOfClass(
            PebbleWrenEntity.class,
            scan,
            other -> other != this && other.isAlive() && !other.isTame()
        );

        if (mates.isEmpty()) {
            return focus;
        }

        double sumX = this.getX();
        double sumY = this.getY();
        double sumZ = this.getZ();
        int count = 1;

        int limit = Math.min(7, mates.size());
        for (int i = 0; i < limit; i++) {
            PebbleWrenEntity mate = mates.get(i);
            sumX += mate.getX();
            sumY += mate.getY();
            sumZ += mate.getZ();
            count++;
        }

        return new Vec3(sumX / count, sumY / count, sumZ / count);
    }

    private static final class PebbleWrenFlockCohesionGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final PebbleWrenEntity mob;
        private int retargetTicks;

        private PebbleWrenFlockCohesionGoal(PebbleWrenEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.isTame() || mob.isBaby() || mob.isOrderedToSit()) {
                return false;
            }

            if (mob.getTarget() != null) {
                return false;
            }

            if (mob.getNavigation().isInProgress()) {
                return false;
            }

            RandomSource random = mob.getRandom();
            if (random.nextInt(4) != 0) {
                return false;
            }

            Vec3 focus = mob.getFlockFocusPoint();
            return mob.position().distanceToSqr(focus) > (FLOCK_RETURN_DISTANCE * FLOCK_RETURN_DISTANCE);
        }

        @Override
        public boolean canContinueToUse() {
            if (mob.isTame() || mob.isBaby() || mob.isOrderedToSit()) {
                return false;
            }

            if (mob.getTarget() != null) {
                return false;
            }

            Vec3 focus = mob.getFlockFocusPoint();
            return mob.position().distanceToSqr(focus) > (FLOCK_MAX_WANDER_RADIUS * FLOCK_MAX_WANDER_RADIUS)
                && mob.getNavigation().isInProgress();
        }

        @Override
        public void start() {
            this.retargetTicks = 0;
            Vec3 focus = mob.getFlockFocusPoint();
            mob.getNavigation().moveTo(focus.x, focus.y + 1.5D, focus.z, 1.10D);
        }

        @Override
        public void tick() {
            if (retargetTicks-- > 0) {
                return;
            }

            retargetTicks = 20;
            Vec3 focus = mob.getFlockFocusPoint();
            mob.getNavigation().moveTo(focus.x, focus.y + 1.5D, focus.z, 1.10D);
        }
    }

    private static final class PebbleWrenAirWanderGoal extends Goal {
        private final PebbleWrenEntity mob;
        private int cooldownTicks;

        private PebbleWrenAirWanderGoal(PebbleWrenEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.isOrderedToSit() || mob.isPassenger() || mob.isInWaterOrBubble()) {
                return false;
            }

            if (!mob.isFlying()) {
                return false;
            }

            if (mob.getTarget() != null || mob.getNavigation().isInProgress()) {
                return false;
            }

            if (cooldownTicks > 0) {
                cooldownTicks--;
                return false;
            }

            return mob.getRandom().nextInt(4) == 0;
        }

        @Override
        public void start() {
            RandomSource random = mob.getRandom();
            Vec3 origin = mob.isTame() && mob.getOwner() != null ? mob.getOwner().position() : mob.getFlockFocusPoint();

            double dx = origin.x + (random.nextDouble() * 10.0D - 5.0D);
            double dz = origin.z + (random.nextDouble() * 10.0D - 5.0D);
            double dy = origin.y + 1.5D + (random.nextDouble() * 4.0D - 1.5D);
            dy = Math.max(mob.level().getMinBuildHeight() + 2.0D, Math.min(mob.level().getMaxBuildHeight() - 2.0D, dy));

            BlockPos candidate = BlockPos.containing(dx, dy, dz);
            if (!mob.level().getBlockState(candidate).getCollisionShape(mob.level(), candidate).isEmpty()) {
                cooldownTicks = 12;
                return;
            }

            if (!mob.level().noCollision(mob, mob.getBoundingBox().move(dx - mob.getX(), dy - mob.getY(), dz - mob.getZ()))) {
                cooldownTicks = 10;
                return;
            }

            mob.getNavigation().moveTo(dx, dy, dz, 1.05D);
            cooldownTicks = 8 + random.nextInt(12);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.WHEAT_SEEDS);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        PebbleWrenEntity child = ModEntities.PEBBLE_WREN.get().create(level);
        if (child == null) {
            return null;
        }

        child.setTextureVariant(this.random.nextInt(TEXTURE_VARIANTS) + 1);

        if (this.isTame() && this.getOwnerUUID() != null) {
            child.setOwnerUUID(this.getOwnerUUID());
            child.setTame(true);
        }

        return child;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.isFlying()) {
                this.airborneTicks++;
                this.groundedTicks = 0;

                if (this.shouldLand()) {
                    this.setFlying(false);
                    this.getNavigation().stop();
                    this.beginGroundPhase();
                }
            } else {
                this.groundedTicks++;
                this.airborneTicks = 0;

                if (this.shouldTakeOff()) {
                    this.setFlying(true);
                    this.beginFlightPhase();
                    Vec3 delta = this.getDeltaMovement();
                    this.setDeltaMovement(delta.x, Math.max(0.18D, delta.y), delta.z);
                    this.hasImpulse = true;
                }
            }

            if (this.isFlying()) {
                Vec3 delta = this.getDeltaMovement();
                if (this.onGround() && delta.y <= 0.03D) {
                    this.setDeltaMovement(delta.x, 0.18D, delta.z);
                    this.hasImpulse = true;
                } else if (delta.y < -0.08D) {
                    this.setDeltaMovement(delta.x, -0.08D, delta.z);
                }
            }
        }

        if (this.oreFindCooldownTicks > 0) {
            --this.oreFindCooldownTicks;
        }

        if (this.displayAnimTicks > 0) {
            --this.displayAnimTicks;
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == ENTITY_EVENT_DISPLAY) {
            this.displayAnimTicks = DISPLAY_ANIM_TICKS;
            return;
        }

        super.handleEntityEvent(id);
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.level().isClientSide) {
            boolean canInteract = isFood(stack) || (this.isTame() && this.isOwnedBy(player) && stack.isEmpty());
            return canInteract ? net.minecraft.world.InteractionResult.SUCCESS : super.mobInteract(player, hand);
        }

        if (isFood(stack)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (!this.isTame()) {
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.setOrderedToSit(false);
                    this.setTarget(null);
                    this.level().broadcastEntityEvent(this, (byte) 7); // hearts
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6); // smoke
                }
            } else {
                this.heal(1.0F);

                if (this.isOwnedBy(player) && player.isCrouching()) {
                    tryOreFindPing((ServerLevel) this.level(), player);
                }
            }

            this.playSound(SoundEvents.GENERIC_EAT, 0.6F, 0.9F + this.random.nextFloat() * 0.2F);
            return net.minecraft.world.InteractionResult.CONSUME;
        }

        // Owner control: right-click with empty hand to sit/stand.
        if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty()) {
            this.setOrderedToSit(!this.isOrderedToSit());
            this.getNavigation().stop();
            this.setTarget(null);
            return net.minecraft.world.InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    private void tryOreFindPing(ServerLevel serverLevel, Player owner) {
        if (this.oreFindCooldownTicks > 0) {
            return;
        }

        BlockPos orePos = findNearbyOre(serverLevel, owner.blockPosition());
        if (orePos == null) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, getX(), getY(0.5D), getZ(), 6, 0.25D, 0.15D, 0.25D, 0.01D);
            this.playSound(SoundEvents.PARROT_AMBIENT, 0.6F, 0.6F);
            this.oreFindCooldownTicks = ORE_FIND_COOLDOWN_TICKS;
            return;
        }

        double fromX = getX();
        double fromY = getY(0.55D);
        double fromZ = getZ();

        double toX = orePos.getX() + 0.5D;
        double toY = orePos.getY() + 0.5D;
        double toZ = orePos.getZ() + 0.5D;

        double dx = toX - fromX;
        double dy = toY - fromY;
        double dz = toZ - fromZ;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 0.0001D) {
            len = 1.0D;
        }

        dx /= len;
        dy /= len;
        dz /= len;

        // Direction hint: a short "trail" pointing toward the ore.
        for (int i = 0; i < 10; i++) {
            double t = 0.35D + (i * 0.25D);
            serverLevel.sendParticles(ParticleTypes.END_ROD, fromX + dx * t, fromY + dy * t, fromZ + dz * t, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY(0.6D), getZ(), 4, 0.18D, 0.18D, 0.18D, 0.02D);
        this.playSound(SoundEvents.PARROT_AMBIENT, 0.7F, 1.35F);

        // Kick off the fan-tail display animation client-side.
        this.displayAnimTicks = DISPLAY_ANIM_TICKS;
        this.level().broadcastEntityEvent(this, ENTITY_EVENT_DISPLAY);

        this.oreFindCooldownTicks = ORE_FIND_COOLDOWN_TICKS;
    }

    @Nullable
    private BlockPos findNearbyOre(ServerLevel serverLevel, BlockPos center) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int i = 0; i < ORE_FIND_SAMPLE_ATTEMPTS; i++) {
            int dx = this.random.nextInt(ORE_FIND_RADIUS * 2 + 1) - ORE_FIND_RADIUS;
            int dy = this.random.nextInt(ORE_FIND_Y_RANGE * 2 + 1) - ORE_FIND_Y_RANGE;
            int dz = this.random.nextInt(ORE_FIND_RADIUS * 2 + 1) - ORE_FIND_RADIUS;

            mutable.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
            if (!serverLevel.isLoaded(mutable)) {
                continue;
            }

            net.minecraft.world.level.block.state.BlockState state = serverLevel.getBlockState(mutable);
            if (state.is(Tags.Blocks.ORES)) {
                return mutable.immutable();
            }
        }

        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            if (this.displayAnimTicks > 0) {
                state.setAnimation(DISPLAY_LOOP);
            } else if (this.isFlying()) {
                state.setAnimation(FLY_LOOP);
            } else {
                state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

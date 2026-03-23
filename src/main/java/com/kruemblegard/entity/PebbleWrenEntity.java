package com.kruemblegard.entity;

import java.util.EnumSet;
import java.util.List;

import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.registry.ModSounds;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.Tags;

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

    private static final RawAnimation PERCH_LOOP =
        RawAnimation.begin().thenLoop("animation.pebble_wren.perch");

    private static final RawAnimation PERCH_FLOURISH_LOOP =
        RawAnimation.begin().thenLoop("animation.pebble_wren.perch_flourish");

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
    private static final byte ENTITY_EVENT_PERCH_FLOURISH = 16;
    private static final int DISPLAY_ANIM_TICKS = 20 * 3;
    private static final int PERCH_FLOURISH_TICKS = 10;

    private static final double FLOCK_SCAN_RADIUS = 14.0D;
    private static final double FLOCK_RETURN_DISTANCE = 12.0D;
    private static final double FLOCK_MAX_WANDER_RADIUS = 8.0D;
    private static final int MIN_AIRBORNE_TICKS = 14;
    private static final int MAX_AIRBORNE_TICKS = 20 * 2;
    private static final int MIN_GROUNDED_TICKS = 6;
    private static final int MAX_GROUNDED_TICKS = 20 + 12;
    private static final double OWNER_TAKEOFF_DISTANCE = 5.0D;
    private static final double FLOCK_TAKEOFF_DISTANCE = 6.0D;
    private static final int PERCH_SEARCH_RADIUS = 6;
    private static final double FLOCK_SYNC_RADIUS = 7.0D;
    private static final double PERCH_CALL_RESPONSE_RADIUS = 6.0D;
    private static final int MIN_PERCH_SOCIAL_COOLDOWN = 20 * 4;
    private static final int MAX_PERCH_SOCIAL_COOLDOWN = 20 * 9;
    private static final int MAX_PERCH_CALL_CHAIN_DEPTH = 1;
    private static final double GROUND_SOCIAL_RADIUS = 7.0D;
    private static final double GROUP_FLUSH_RADIUS = 9.0D;
    private static final int MIN_GROUND_SOCIAL_TICKS = 20;
    private static final int MAX_GROUND_SOCIAL_TICKS = 20 * 4;
    private static final int MIN_GROUP_FLUSH_TICKS = 8;
    private static final int MAX_GROUP_FLUSH_TICKS = 18;
    private static final int GROUND_FORAGE_RADIUS = 5;
    private static final int PLAYFUL_HOP_INTERVAL_TICKS = 8;
    private static final double STIMULUS_FLUSH_RADIUS = 10.0D;
    private static final double SPRINTING_PLAYER_FLUSH_RADIUS = 6.5D;
    private static final int STIMULUS_FLUSH_COOLDOWN_TICKS = 20 * 4;

    private int oreFindCooldownTicks = 0;
    private int displayAnimTicks = 0;
    private int perchFlourishAnimTicks = 0;
    private int airborneTicks = 0;
    private int groundedTicks = 0;
    private int flightPhaseTargetTicks = MIN_GROUNDED_TICKS;
    private int perchSocialCooldownTicks = 20 * 4;
    private int flockSyncTicks = 0;
    private int groundSocialTicks = 0;
    private int groupFlushTicks = 0;
    private int flushStimulusCooldownTicks = 0;

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
        boolean changed = this.isFlying() != flying;
        this.entityData.set(DATA_FLYING, flying);
        this.setNoGravity(flying);
        if (flying) {
            this.fallDistance = 0.0F;
        }

        if (changed) {
            this.playSound(
                ModSounds.PEBBLE_WREN_FLUTTER.get(),
                flying ? 0.16F : 0.12F,
                (flying ? 1.18F : 1.02F) + (this.random.nextFloat() * 0.12F)
            );
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
        this.goalSelector.addGoal(8, new PebbleWrenGroupFlushGoal(this));
        this.goalSelector.addGoal(9, new PebbleWrenGroundForageGoal(this));
        this.goalSelector.addGoal(10, new PebbleWrenGroundPlayGoal(this));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(12, new PebbleWrenAirWanderGoal(this));
        this.goalSelector.addGoal(13, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(14, new RandomLookAroundGoal(this));
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

        if (this.groupFlushTicks > 0) {
            return true;
        }

        if (this.flockSyncTicks > 0 && this.groundedTicks >= 2) {
            return this.random.nextInt(3) == 0;
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

        return this.groundedTicks >= this.flightPhaseTargetTicks && this.random.nextInt(7) == 0;
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

        return this.airborneTicks >= this.flightPhaseTargetTicks && (this.onGround() || this.isNearPreferredPerch() || this.random.nextInt(10) == 0);
    }

    private void beginFlightPhase() {
        this.airborneTicks = 0;
        this.flightPhaseTargetTicks = MIN_AIRBORNE_TICKS + this.random.nextInt((MAX_AIRBORNE_TICKS - MIN_AIRBORNE_TICKS) + 1);
    }

    private void beginGroundPhase() {
        this.groundedTicks = 0;
        this.flightPhaseTargetTicks = MIN_GROUNDED_TICKS + this.random.nextInt((MAX_GROUNDED_TICKS - MIN_GROUNDED_TICKS) + 1);
    }

    private void receiveFlockSyncSignal() {
        this.flockSyncTicks = 10 + this.random.nextInt(12);
    }

    private void receiveGroundSocialSignal() {
        this.groundSocialTicks = Math.max(
            this.groundSocialTicks,
            MIN_GROUND_SOCIAL_TICKS + this.random.nextInt((MAX_GROUND_SOCIAL_TICKS - MIN_GROUND_SOCIAL_TICKS) + 1)
        );
    }

    private void broadcastGroundSocialSignal() {
        if (this.isTame()) {
            return;
        }

        this.receiveGroundSocialSignal();

        AABB scan = this.getBoundingBox().inflate(GROUND_SOCIAL_RADIUS);
        List<PebbleWrenEntity> mates = this.level().getEntitiesOfClass(
            PebbleWrenEntity.class,
            scan,
            other -> other != this && other.isAlive() && !other.isTame() && !other.isOrderedToSit()
        );

        for (PebbleWrenEntity mate : mates) {
            if (!mate.isFlying() && mate.onGround() && !mate.isPassenger() && !mate.isInWaterOrBubble()) {
                mate.receiveGroundSocialSignal();
            }
        }
    }

    private void receiveGroupFlushSignal() {
        this.groupFlushTicks = Math.max(
            this.groupFlushTicks,
            MIN_GROUP_FLUSH_TICKS + this.random.nextInt((MAX_GROUP_FLUSH_TICKS - MIN_GROUP_FLUSH_TICKS) + 1)
        );
        this.flockSyncTicks = Math.max(this.flockSyncTicks, 8 + this.random.nextInt(8));
    }

    private void broadcastGroupFlushSignal() {
        if (this.isTame()) {
            return;
        }

        this.receiveGroupFlushSignal();

        AABB scan = this.getBoundingBox().inflate(GROUP_FLUSH_RADIUS);
        List<PebbleWrenEntity> mates = this.level().getEntitiesOfClass(
            PebbleWrenEntity.class,
            scan,
            other -> other != this && other.isAlive() && !other.isTame() && !other.isOrderedToSit()
        );

        for (PebbleWrenEntity mate : mates) {
            mate.receiveGroupFlushSignal();
        }
    }

    private void triggerStimulusFlush() {
        if (this.isTame() || this.flushStimulusCooldownTicks > 0) {
            return;
        }

        this.flushStimulusCooldownTicks = STIMULUS_FLUSH_COOLDOWN_TICKS;
        this.broadcastGroupFlushSignal();
    }

    private void tryStimulusFlushFromNearbyPlayers() {
        if (this.isTame() || this.isFlying() || this.groupFlushTicks > 0 || this.flushStimulusCooldownTicks > 0) {
            return;
        }

        AABB scan = this.getBoundingBox().inflate(STIMULUS_FLUSH_RADIUS);
        List<Player> players = this.level().getEntitiesOfClass(
            Player.class,
            scan,
            player -> player.isAlive() && !player.isSpectator() && !player.isCreative() && player.isSprinting()
        );

        for (Player player : players) {
            if (this.distanceToSqr(player) <= (SPRINTING_PLAYER_FLUSH_RADIUS * SPRINTING_PLAYER_FLUSH_RADIUS)) {
                this.triggerStimulusFlush();
                return;
            }
        }
    }

    private void broadcastFlockSyncSignal() {
        if (this.isTame()) {
            return;
        }

        AABB scan = this.getBoundingBox().inflate(FLOCK_SYNC_RADIUS);
        List<PebbleWrenEntity> mates = this.level().getEntitiesOfClass(
            PebbleWrenEntity.class,
            scan,
            other -> other != this && other.isAlive() && !other.isTame() && !other.isOrderedToSit()
        );

        for (PebbleWrenEntity mate : mates) {
            mate.receiveFlockSyncSignal();
        }
    }

    private boolean canAnswerPerchCall() {
        return !this.isFlying() && this.onGround() && !this.isOrderedToSit() && !this.isPassenger()
            && !this.isInWaterOrBubble() && this.getTarget() == null && this.isNearPreferredPerch()
            && this.getNavigation().isDone();
    }

    private float getPerchCallPitch(boolean subdued) {
        float variantOffset = (this.getTextureVariant() - 1) / (float) (TEXTURE_VARIANTS - 1);
        float basePitch = subdued ? 1.05F : 1.12F;
        float variantPitch = (variantOffset - 0.5F) * 0.12F;
        float randomPitch = (this.random.nextFloat() - 0.5F) * (subdued ? 0.08F : 0.12F);
        return basePitch + variantPitch + randomPitch;
    }

    private void playPerchFlourishCue() {
        this.playSound(ModSounds.PEBBLE_WREN_FLOURISH.get(), 0.2F, 0.98F + this.random.nextFloat() * 0.16F);
    }

    private void receivePerchCall(int chainDepth) {
        if (!this.canAnswerPerchCall() || this.perchSocialCooldownTicks > (MIN_PERCH_SOCIAL_COOLDOWN / 2)) {
            return;
        }

        if (this.random.nextInt(3) != 0) {
            return;
        }

        this.perchFlourishAnimTicks = PERCH_FLOURISH_TICKS;
        this.level().broadcastEntityEvent(this, ENTITY_EVENT_PERCH_FLOURISH);

        if (this.random.nextBoolean()) {
            this.playSound(ModSounds.PEBBLE_WREN_PERCH_REPLY.get(), 0.24F, this.getPerchCallPitch(true));
        }

        this.playPerchFlourishCue();

        if (chainDepth < MAX_PERCH_CALL_CHAIN_DEPTH && this.random.nextInt(5) == 0) {
            this.broadcastPerchCall(chainDepth + 1);
        }

        this.perchSocialCooldownTicks = 20 + this.random.nextInt(20);
    }

    private void broadcastPerchCall(int chainDepth) {
        AABB scan = this.getBoundingBox().inflate(PERCH_CALL_RESPONSE_RADIUS);
        List<PebbleWrenEntity> mates = this.level().getEntitiesOfClass(
            PebbleWrenEntity.class,
            scan,
            other -> other != this && other.isAlive()
        );

        PebbleWrenEntity nearestMate = null;
        double nearestDistance = Double.MAX_VALUE;

        for (PebbleWrenEntity mate : mates) {
            if (!mate.canAnswerPerchCall()) {
                continue;
            }

            double distance = this.distanceToSqr(mate);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestMate = mate;
            }
        }

        if (nearestMate != null) {
            nearestMate.receivePerchCall(chainDepth);
        }
    }

    private void tryPerchedSocialBehavior() {
        if (this.perchSocialCooldownTicks > 0 || this.isFlying() || !this.onGround() || this.isOrderedToSit()
            || this.isPassenger() || this.isInWaterOrBubble() || this.getTarget() != null || !this.isNearPreferredPerch()) {
            return;
        }

        if (this.getNavigation().isInProgress() || this.random.nextInt(120) != 0) {
            return;
        }

        this.playSound(ModSounds.PEBBLE_WREN_PERCH_CALL.get(), 0.35F, this.getPerchCallPitch(false));
        this.broadcastPerchCall(0);

        if (this.random.nextBoolean()) {
            this.perchFlourishAnimTicks = PERCH_FLOURISH_TICKS;
            this.level().broadcastEntityEvent(this, ENTITY_EVENT_PERCH_FLOURISH);
            this.playPerchFlourishCue();
        }

        this.perchSocialCooldownTicks = MIN_PERCH_SOCIAL_COOLDOWN
            + this.random.nextInt((MAX_PERCH_SOCIAL_COOLDOWN - MIN_PERCH_SOCIAL_COOLDOWN) + 1);
    }

    private boolean isPreferredPerch(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        if (state.isAir()) {
            return false;
        }

        if (state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS) || state.is(BlockTags.LEAVES)
            || state.is(BlockTags.LOGS) || state.is(BlockTags.LOGS_THAT_BURN)) {
            return true;
        }

        return state.isFaceSturdy(this.level(), pos, Direction.UP)
            && this.level().getBlockState(pos.above()).isAir()
            && this.level().getBlockState(pos.above(2)).isAir();
    }

    private int getPerchScore(BlockPos pos, Vec3 origin) {
        BlockState state = this.level().getBlockState(pos);
        int score = 0;

        if (state.is(BlockTags.LOGS) || state.is(BlockTags.LOGS_THAT_BURN)) {
            score += 6;
        }

        if (state.is(BlockTags.LEAVES)) {
            score += 5;
        }

        if (state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS)) {
            score += 4;
        }

        int heightDelta = pos.getY() - BlockPos.containing(origin).getY();
        score += Math.max(-2, Math.min(6, heightDelta * 2));
        score -= (int) Math.round(pos.distSqr(BlockPos.containing(origin)) * 0.12D);
        return score;
    }

    private boolean isNearPreferredPerch() {
        BlockPos below = this.blockPosition().below();
        return this.isPreferredPerch(below) || this.isPreferredPerch(below.north()) || this.isPreferredPerch(below.south())
            || this.isPreferredPerch(below.east()) || this.isPreferredPerch(below.west());
    }

    @Nullable
    private Vec3 findNearbyPerchTarget(Vec3 origin, int radius) {
        RandomSource random = this.getRandom();
        Vec3 bestTarget = null;
        int bestScore = Integer.MIN_VALUE;

        for (int attempt = 0; attempt < 20; attempt++) {
            int x = BlockPos.containing(origin).getX() + random.nextInt(radius * 2 + 1) - radius;
            int z = BlockPos.containing(origin).getZ() + random.nextInt(radius * 2 + 1) - radius;
            int y = BlockPos.containing(origin).getY() + random.nextInt(7) - 1;

            BlockPos perch = new BlockPos(x, y, z);
            if (!this.isPreferredPerch(perch)) {
                continue;
            }

            BlockPos landing = perch.above();
            if (!this.level().getBlockState(landing).getCollisionShape(this.level(), landing).isEmpty()) {
                continue;
            }

            if (!this.level().getBlockState(landing.above()).getCollisionShape(this.level(), landing.above()).isEmpty()) {
                continue;
            }

            if (!this.level().noCollision(this, this.getBoundingBox().move(
                landing.getX() + 0.5D - this.getX(),
                landing.getY() - this.getY(),
                landing.getZ() + 0.5D - this.getZ()
            ))) {
                continue;
            }

            int score = this.getPerchScore(perch, origin);
            if (score > bestScore) {
                bestScore = score;
                bestTarget = Vec3.atBottomCenterOf(landing).add(0.0D, 0.05D, 0.0D);
            }
        }

        return bestTarget;
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

    private boolean canUseWildGroundSocialBehavior() {
        return !this.isTame() && !this.isOrderedToSit() && !this.isPassenger() && !this.isInWaterOrBubble()
            && !this.isFlying() && this.onGround() && this.getTarget() == null;
    }

    private List<PebbleWrenEntity> getNearbyWildGroundedFlockmates(double radius) {
        AABB scan = this.getBoundingBox().inflate(radius);
        return this.level().getEntitiesOfClass(
            PebbleWrenEntity.class,
            scan,
            other -> other != this && other.isAlive() && !other.isTame() && !other.isOrderedToSit()
                && !other.isFlying() && other.onGround() && !other.isPassenger() && !other.isInWaterOrBubble()
        );
    }

    @Nullable
    private Vec3 findNearbyGroundSocialTarget(Vec3 origin, int radius) {
        RandomSource random = this.getRandom();

        for (int attempt = 0; attempt < 16; attempt++) {
            int x = BlockPos.containing(origin).getX() + random.nextInt(radius * 2 + 1) - radius;
            int z = BlockPos.containing(origin).getZ() + random.nextInt(radius * 2 + 1) - radius;
            int topY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (topY <= this.level().getMinBuildHeight() + 1) {
                continue;
            }

            BlockPos ground = new BlockPos(x, topY - 1, z);
            BlockPos stand = ground.above();
            if (!this.level().getBlockState(ground).isFaceSturdy(this.level(), ground, Direction.UP)) {
                continue;
            }

            if (!this.level().getFluidState(stand).isEmpty()) {
                continue;
            }

            if (!this.level().getBlockState(stand).getCollisionShape(this.level(), stand).isEmpty()) {
                continue;
            }

            if (!this.level().getBlockState(stand.above()).getCollisionShape(this.level(), stand.above()).isEmpty()) {
                continue;
            }

            Vec3 target = Vec3.atBottomCenterOf(stand);
            if (!this.level().noCollision(this, this.getBoundingBox().move(target.x - this.getX(), target.y - this.getY(), target.z - this.getZ()))) {
                continue;
            }

            return target;
        }

        return null;
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

            Vec3 perchTarget = mob.findNearbyPerchTarget(origin, PERCH_SEARCH_RADIUS);
            if (perchTarget != null && random.nextInt(5) != 0) {
                mob.getNavigation().moveTo(perchTarget.x, perchTarget.y, perchTarget.z, 1.08D);
                cooldownTicks = 6 + random.nextInt(10);
                return;
            }

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
            cooldownTicks = 6 + random.nextInt(10);
        }
    }

    private static final class PebbleWrenGroundForageGoal extends Goal {
        private final PebbleWrenEntity mob;
        private @Nullable Vec3 target;
        private int activeTicks;
        private int peckTicks;

        private PebbleWrenGroundForageGoal(PebbleWrenEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!mob.canUseWildGroundSocialBehavior() || mob.getNavigation().isInProgress()) {
                return false;
            }

            List<PebbleWrenEntity> mates = mob.getNearbyWildGroundedFlockmates(GROUND_SOCIAL_RADIUS);
            if (mates.isEmpty()) {
                return false;
            }

            if (mob.groundSocialTicks <= 0 && mob.getRandom().nextInt(90) != 0) {
                return false;
            }

            Vec3 focus = mob.getFlockFocusPoint();
            target = mob.findNearbyGroundSocialTarget(focus, GROUND_FORAGE_RADIUS);
            return target != null;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && activeTicks > 0 && mob.canUseWildGroundSocialBehavior();
        }

        @Override
        public void start() {
            activeTicks = 30 + mob.getRandom().nextInt(30);
            peckTicks = 0;
            mob.broadcastGroundSocialSignal();
            mob.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);
        }

        @Override
        public void tick() {
            activeTicks--;
            if (target == null) {
                return;
            }

            double distSqr = mob.distanceToSqr(target);
            if (distSqr > 1.2D * 1.2D) {
                mob.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);
                return;
            }

            mob.getNavigation().stop();
            mob.getLookControl().setLookAt(target.x, target.y - 0.35D, target.z);

            if (peckTicks > 0) {
                peckTicks--;
                return;
            }

            peckTicks = 6 + mob.getRandom().nextInt(8);
            if (mob.getRandom().nextInt(4) == 0) {
                mob.playSound(ModSounds.PEBBLE_WREN_AMBIENT.get(), 0.12F, 1.18F + mob.getRandom().nextFloat() * 0.14F);
            }
        }

        @Override
        public void stop() {
            target = null;
            activeTicks = 0;
            peckTicks = 0;
            mob.getNavigation().stop();
        }
    }

    private static final class PebbleWrenGroundPlayGoal extends Goal {
        private final PebbleWrenEntity mob;
        private @Nullable PebbleWrenEntity playmate;
        private int activeTicks;
        private int hopCooldownTicks;

        private PebbleWrenGroundPlayGoal(PebbleWrenEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!mob.canUseWildGroundSocialBehavior()) {
                return false;
            }

            List<PebbleWrenEntity> mates = mob.getNearbyWildGroundedFlockmates(GROUND_SOCIAL_RADIUS);
            if (mates.isEmpty()) {
                return false;
            }

            if (mob.groundSocialTicks <= 0 && mob.getRandom().nextInt(130) != 0) {
                return false;
            }

            playmate = mates.get(mob.getRandom().nextInt(mates.size()));
            return playmate != null;
        }

        @Override
        public boolean canContinueToUse() {
            return activeTicks > 0 && playmate != null && playmate.isAlive() && mob.canUseWildGroundSocialBehavior()
                && !playmate.isFlying() && playmate.onGround();
        }

        @Override
        public void start() {
            activeTicks = 24 + mob.getRandom().nextInt(26);
            hopCooldownTicks = 0;
            mob.broadcastGroundSocialSignal();
        }

        @Override
        public void tick() {
            activeTicks--;
            if (playmate == null) {
                return;
            }

            Vec3 matePos = playmate.position();
            double angle = (mob.tickCount * 0.35D) + (mob.getId() * 0.7D);
            double radius = 0.8D + (mob.getRandom().nextDouble() * 0.6D);
            Vec3 target = matePos.add(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius);

            mob.getNavigation().moveTo(target.x, target.y, target.z, 1.12D);
            mob.getLookControl().setLookAt(playmate, 30.0F, 30.0F);

            if (hopCooldownTicks > 0) {
                hopCooldownTicks--;
                return;
            }

            hopCooldownTicks = PLAYFUL_HOP_INTERVAL_TICKS + mob.getRandom().nextInt(7);
            if (mob.onGround()) {
                Vec3 hop = target.subtract(mob.position());
                if (hop.lengthSqr() > 0.0001D) {
                    hop = hop.normalize().scale(0.08D + mob.getRandom().nextDouble() * 0.05D);
                }
                mob.setDeltaMovement(hop.x, 0.16D + mob.getRandom().nextDouble() * 0.04D, hop.z);
                mob.hasImpulse = true;
            }

            if (mob.getRandom().nextInt(5) == 0) {
                mob.playSound(ModSounds.PEBBLE_WREN_PERCH_REPLY.get(), 0.14F, 1.12F + mob.getRandom().nextFloat() * 0.18F);
            }
        }

        @Override
        public void stop() {
            playmate = null;
            activeTicks = 0;
            hopCooldownTicks = 0;
            mob.getNavigation().stop();
        }
    }

    private static final class PebbleWrenGroupFlushGoal extends Goal {
        private final PebbleWrenEntity mob;

        private PebbleWrenGroupFlushGoal(PebbleWrenEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (!mob.canUseWildGroundSocialBehavior()) {
                return false;
            }

            if (mob.groundedTicks < 5) {
                return false;
            }

            if (mob.getNearbyWildGroundedFlockmates(GROUP_FLUSH_RADIUS).size() < 2) {
                return false;
            }

            return mob.getRandom().nextInt(220) == 0;
        }

        @Override
        public void start() {
            mob.broadcastGroupFlushSignal();
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
            if (this.perchSocialCooldownTicks > 0) {
                --this.perchSocialCooldownTicks;
            }

            if (this.flockSyncTicks > 0) {
                --this.flockSyncTicks;
            }

            if (this.groundSocialTicks > 0) {
                --this.groundSocialTicks;
            }

            if (this.groupFlushTicks > 0) {
                --this.groupFlushTicks;
            }

            if (this.flushStimulusCooldownTicks > 0) {
                --this.flushStimulusCooldownTicks;
            }

            this.tryStimulusFlushFromNearbyPlayers();

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
                    boolean wasPerched = this.isNearPreferredPerch();
                    this.setFlying(true);
                    this.beginFlightPhase();
                    Vec3 delta = this.getDeltaMovement();
                    this.setDeltaMovement(delta.x, Math.max(0.18D, delta.y), delta.z);
                    this.hasImpulse = true;

                    if (wasPerched) {
                        this.broadcastFlockSyncSignal();
                    }
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
            } else {
                this.tryPerchedSocialBehavior();
            }
        }

        if (this.oreFindCooldownTicks > 0) {
            --this.oreFindCooldownTicks;
        }

        if (this.displayAnimTicks > 0) {
            --this.displayAnimTicks;
        }

        if (this.perchFlourishAnimTicks > 0) {
            --this.perchFlourishAnimTicks;
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == ENTITY_EVENT_DISPLAY) {
            this.displayAnimTicks = DISPLAY_ANIM_TICKS;
            return;
        }

        if (id == ENTITY_EVENT_PERCH_FLOURISH) {
            this.perchFlourishAnimTicks = PERCH_FLOURISH_TICKS;
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
            this.playSound(ModSounds.PEBBLE_WREN_PERCH_REPLY.get(), 0.28F, 0.86F + this.random.nextFloat() * 0.08F);
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
    this.playSound(ModSounds.PEBBLE_WREN_ORE_PING.get(), 0.55F, 1.18F + this.random.nextFloat() * 0.08F);

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
            } else if (this.perchFlourishAnimTicks > 0) {
                state.setAnimation(PERCH_FLOURISH_LOOP);
            } else if (this.isFlying()) {
                state.setAnimation(FLY_LOOP);
            } else {
                state.setAnimation(state.isMoving() ? WALK_LOOP : (this.isNearPreferredPerch() ? PERCH_LOOP : IDLE_LOOP));
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && this.isAlive()) {
            this.triggerStimulusFlush();
        }
        return hurt;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.PEBBLE_WREN_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.PEBBLE_WREN_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.PEBBLE_WREN_DEATH.get();
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

package com.kruemblegard.entity.mount;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kruemblegard.entity.CephalariEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class CephalariMountEntity extends PathfinderMob implements GeoEntity {

    private static final String SEAT_BONE_NAME = "seat";

    private static final String NBT_TEXTURE_VARIANT = "KruemblegardCephalariMountTextureVariant";
    private static final int MOUNT_TEXTURE_VARIANTS = 5;

    private static final EntityDataAccessor<Integer> DATA_TEXTURE_VARIANT = SynchedEntityData.defineId(CephalariMountEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final String geoResourcePath;
    private final Vec3 defaultSeatOffsetBlocks;

    private boolean forwardingLinkedDamage = false;

    private volatile @Nullable Vec3 cachedSeatOffsetBlocks;

    protected CephalariMountEntity(
        EntityType<? extends PathfinderMob> type,
        Level level,
        String geoResourcePath,
        Vec3 defaultSeatOffsetBlocks
    ) {
        super(type, level);
        this.geoResourcePath = geoResourcePath;
        this.defaultSeatOffsetBlocks = defaultSeatOffsetBlocks;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // 1..MOUNT_TEXTURE_VARIANTS (we use 1-based because the files are named _1.._5)
        this.entityData.define(DATA_TEXTURE_VARIANT, 1);
    }

    public int getTextureVariant() {
        return this.entityData.get(DATA_TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        int clamped = Mth.clamp(variant, 1, MOUNT_TEXTURE_VARIANTS);
        this.entityData.set(DATA_TEXTURE_VARIANT, clamped);
    }

    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        net.minecraft.world.DifficultyInstance difficulty,
        MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnData,
        @Nullable net.minecraft.nbt.CompoundTag tag
    ) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);

        // Assign a stable random look per mount.
        if (!this.level().isClientSide) {
            setTextureVariant(1 + this.getRandom().nextInt(MOUNT_TEXTURE_VARIANTS));
        }

        return result;
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
    }

    public static AttributeSupplier.Builder createBaseAttributes() {
        return LivingEntity.createLivingAttributes()
            // Match villager-class health so linked Cephalari + mount die together.
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    protected abstract RawAnimation getIdleAnimation();

    protected abstract RawAnimation getMoveAnimation();

    protected abstract RawAnimation getManifestAnimation();

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (level().isClientSide) {
            return;
        }

        if (getFirstPassenger() instanceof CephalariEntity cephalari) {
            // Keep the pair alive/dead together.
            if (!cephalari.isAlive() && this.isAlive()) {
                this.kill();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && !forwardingLinkedDamage && getFirstPassenger() instanceof CephalariEntity cephalari) {
            boolean result = super.hurt(source, amount);
            if (result && cephalari.isAlive()) {
                cephalari.hurtLinkedFromMount(source, amount);
            }
            return result;
        }

        return super.hurt(source, amount);
    }

    public boolean hurtLinkedFromCephalari(DamageSource source, float amount) {
        if (level().isClientSide) {
            return super.hurt(source, amount);
        }

        if (forwardingLinkedDamage) {
            return super.hurt(source, amount);
        }

        forwardingLinkedDamage = true;
        try {
            return super.hurt(source, amount);
        } finally {
            forwardingLinkedDamage = false;
        }
    }

    @Override
    public void heal(float amount) {
        super.heal(amount);

        if (level().isClientSide) {
            return;
        }

        if (!forwardingLinkedDamage && getFirstPassenger() instanceof CephalariEntity cephalari && cephalari.isAlive()) {
            cephalari.healLinkedFromMount(amount);
        }
    }

    public void healLinkedFromCephalari(float amount) {
        if (level().isClientSide) {
            super.heal(amount);
            return;
        }

        if (forwardingLinkedDamage) {
            super.heal(amount);
            return;
        }

        forwardingLinkedDamage = true;
        try {
            super.heal(amount);
        } finally {
            forwardingLinkedDamage = false;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            state.setAnimation(state.isMoving() ? getMoveAnimation() : getIdleAnimation());
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.STOP)
            .triggerableAnim("manifest", getManifestAnimation()));
    }

    public void playManifest() {
        if (!level().isClientSide) {
            triggerAnim("actionController", "manifest");
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().isEmpty() && passenger instanceof LivingEntity;
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        if (getFirstPassenger() instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    public void travel(Vec3 travelVector) {
        LivingEntity controlling = getControllingPassenger();
        if (controlling instanceof Player player) {
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();

            float strafe = player.xxa * 0.5F;
            float forward = player.zza;
            if (forward <= 0.0F) {
                forward *= 0.25F;
            }

            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(strafe, travelVector.y, forward));
            return;
        }

        super.travel(travelVector);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }

        // If a Cephalari with a profession is riding, forward interaction to open trades.
        if (getFirstPassenger() instanceof CephalariEntity cephalari
            && cephalari.isAlive()
            && cephalari.getVillagerData().getProfession() != VillagerProfession.NONE) {
            return cephalari.mobInteract(player, hand);
        }

        if (!isVehicle()) {
            player.startRiding(this, true);
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (!(passenger instanceof LivingEntity living)) {
            super.positionRider(passenger, moveFunction);
            return;
        }

        Vec3 seat = getSeatOffsetBlocks();

        float yaw = this.yBodyRot;
        float yawRad = yaw * ((float) Math.PI / 180.0F);
        double sin = Math.sin(yawRad);
        double cos = Math.cos(yawRad);

        double dx = seat.x * cos - seat.z * sin;
        double dz = seat.x * sin + seat.z * cos;

        double x = getX() + dx;
        double y = getY() + seat.y + living.getMyRidingOffset();
        double z = getZ() + dz;

        moveFunction.accept(passenger, x, y, z);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    private Vec3 getSeatOffsetBlocks() {
        Vec3 cached = cachedSeatOffsetBlocks;
        if (cached != null) {
            return cached;
        }

        Vec3 computed = loadSeatOffsetFromGeo();
        cachedSeatOffsetBlocks = computed;
        return computed;
    }

    private Vec3 loadSeatOffsetFromGeo() {
        try (InputStream is = CephalariMountEntity.class.getClassLoader().getResourceAsStream(geoResourcePath)) {
            if (is == null) {
                return defaultSeatOffsetBlocks;
            }

            JsonElement rootEl = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            if (!rootEl.isJsonObject()) {
                return defaultSeatOffsetBlocks;
            }

            JsonObject root = rootEl.getAsJsonObject();
            JsonArray geos = root.getAsJsonArray("minecraft:geometry");
            if (geos == null || geos.isEmpty()) {
                return defaultSeatOffsetBlocks;
            }

            JsonObject geo0 = geos.get(0).getAsJsonObject();
            JsonArray bones = geo0.getAsJsonArray("bones");
            if (bones == null || bones.isEmpty()) {
                return defaultSeatOffsetBlocks;
            }

            for (JsonElement boneEl : bones) {
                if (!boneEl.isJsonObject()) {
                    continue;
                }

                JsonObject bone = boneEl.getAsJsonObject();
                String name = bone.has("name") ? bone.get("name").getAsString() : "";
                if (!SEAT_BONE_NAME.equals(name)) {
                    continue;
                }

                JsonArray pivot = bone.getAsJsonArray("pivot");
                if (pivot == null || pivot.size() < 3) {
                    return defaultSeatOffsetBlocks;
                }

                double px = pivot.get(0).getAsDouble() / 16.0D;
                double py = pivot.get(1).getAsDouble() / 16.0D;
                double pz = -pivot.get(2).getAsDouble() / 16.0D;

                px = Mth.clamp(px, -3.5D, 3.5D);
                py = Mth.clamp(py, 0.0D, 5.0D);
                pz = Mth.clamp(pz, -3.5D, 3.5D);

                return new Vec3(px, py, pz);
            }
        } catch (Exception ignored) {
            // Fall back silently; seat alignment isn't worth crashing the game.
        }

        return defaultSeatOffsetBlocks;
    }

    @Override
    public boolean isNoGravity() {
        return super.isNoGravity();
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
        // No fall damage.
    }
}

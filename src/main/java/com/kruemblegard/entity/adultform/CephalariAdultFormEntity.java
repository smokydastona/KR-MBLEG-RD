package com.kruemblegard.entity.adultform;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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

public abstract class CephalariAdultFormEntity extends PathfinderMob implements GeoEntity {

    private static final String SEAT_BONE_NAME = "seat";

    private static final String NBT_TEXTURE_VARIANT = "KruemblegardCephalariAdultFormTextureVariant";
    private static final String NBT_TEXTURE_VARIANT_LEGACY = "KruemblegardCephalariMountTextureVariant";
    private static final String NBT_BODY_TEXTURE = "KruemblegardCephalariBodyTexture";
    private static final String NBT_PROFESSION = "KruemblegardCephalariProfession";
    private static final String NBT_VILLAGER_LEVEL = "KruemblegardCephalariLevel";
    private static final int TEXTURE_VARIANTS = 6;

    private static final EntityDataAccessor<Integer> DATA_TEXTURE_VARIANT = SynchedEntityData.defineId(CephalariAdultFormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_BODY_TEXTURE = SynchedEntityData.defineId(CephalariAdultFormEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_PROFESSION = SynchedEntityData.defineId(CephalariAdultFormEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_VILLAGER_LEVEL = SynchedEntityData.defineId(CephalariAdultFormEntity.class, EntityDataSerializers.INT);

    private static final ResourceLocation DEFAULT_BODY_TEXTURE = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari/cephalari_underway_falls.png"
    );

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final String geoResourcePath;
    private final Vec3 defaultSeatOffsetBlocks;

    private volatile @Nullable Vec3 cachedSeatOffsetBlocks;

    protected CephalariAdultFormEntity(
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
        // 1..TEXTURE_VARIANTS (we use 1-based because the files are named _1.._6)
        this.entityData.define(DATA_TEXTURE_VARIANT, 1);

        // Cephalari appearance data lives on the adult-form entity itself.
        // Empty means "use default".
        this.entityData.define(DATA_BODY_TEXTURE, "");
        // Stored as a ResourceLocation string for the profession registry key. Empty means NONE.
        this.entityData.define(DATA_PROFESSION, "");
        // 1..5 (vanilla villager levels). Default 1.
        this.entityData.define(DATA_VILLAGER_LEVEL, 1);
    }

    public ResourceLocation getBodyTextureResource() {
        String raw = this.entityData.get(DATA_BODY_TEXTURE);
        if (raw == null || raw.isEmpty()) {
            return DEFAULT_BODY_TEXTURE;
        }

        ResourceLocation parsed = ResourceLocation.tryParse(raw);
        return parsed != null ? parsed : DEFAULT_BODY_TEXTURE;
    }

    public void setBodyTextureResource(@Nullable ResourceLocation texture) {
        if (texture == null) {
            this.entityData.set(DATA_BODY_TEXTURE, "");
            return;
        }
        this.entityData.set(DATA_BODY_TEXTURE, texture.toString());
    }

    public VillagerProfession getProfession() {
        String raw = this.entityData.get(DATA_PROFESSION);
        if (raw == null || raw.isEmpty()) {
            return VillagerProfession.NONE;
        }

        ResourceLocation id = ResourceLocation.tryParse(raw);
        if (id == null) {
            return VillagerProfession.NONE;
        }

        VillagerProfession profession = net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION.get(id);
        return profession != null ? profession : VillagerProfession.NONE;
    }

    public void setProfession(@Nullable VillagerProfession profession) {
        if (profession == null || profession == VillagerProfession.NONE) {
            this.entityData.set(DATA_PROFESSION, "");
            return;
        }

        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
        this.entityData.set(DATA_PROFESSION, key != null ? key.toString() : "");
    }

    public int getVillagerLevel() {
        return this.entityData.get(DATA_VILLAGER_LEVEL);
    }

    public void setVillagerLevel(int level) {
        int clamped = Mth.clamp(level, 1, 5);
        this.entityData.set(DATA_VILLAGER_LEVEL, clamped);
    }

    public int getTextureVariant() {
        return this.entityData.get(DATA_TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        int clamped = Mth.clamp(variant, 1, TEXTURE_VARIANTS);
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

        // Assign a stable random look per adult-form entity.
        if (!this.level().isClientSide) {
            setTextureVariant(1 + this.getRandom().nextInt(TEXTURE_VARIANTS));

            // If nothing provided via NBT/commands, pick body texture using the exact historical
            // Cephalari spawn selection logic.
            if (this.entityData.get(DATA_BODY_TEXTURE) == null || this.entityData.get(DATA_BODY_TEXTURE).isEmpty()) {
                setBodyTextureResource(CephalariEntity.pickSpawnBodyTexture(level, this.blockPosition(), this.getRandom()));
            }
        }

        return result;
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_TEXTURE_VARIANT, getTextureVariant());

        tag.putString(NBT_BODY_TEXTURE, getBodyTextureResource().toString());

        VillagerProfession profession = getProfession();
        if (profession != VillagerProfession.NONE) {
            ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
            if (id != null) {
                tag.putString(NBT_PROFESSION, id.toString());
            }
        }

        tag.putInt(NBT_VILLAGER_LEVEL, getVillagerLevel());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(NBT_TEXTURE_VARIANT)) {
            setTextureVariant(tag.getInt(NBT_TEXTURE_VARIANT));
        } else if (tag.contains(NBT_TEXTURE_VARIANT_LEGACY)) {
            // Backward compat for older saves.
            setTextureVariant(tag.getInt(NBT_TEXTURE_VARIANT_LEGACY));
        }

        if (tag.contains(NBT_BODY_TEXTURE)) {
            ResourceLocation parsed = ResourceLocation.tryParse(tag.getString(NBT_BODY_TEXTURE));
            setBodyTextureResource(parsed != null ? parsed : DEFAULT_BODY_TEXTURE);
        }

        if (tag.contains(NBT_PROFESSION)) {
            ResourceLocation profId = ResourceLocation.tryParse(tag.getString(NBT_PROFESSION));
            if (profId != null) {
                VillagerProfession prof = net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION.get(profId);
                setProfession(prof);
            } else {
                setProfession(VillagerProfession.NONE);
            }
        }

        if (tag.contains(NBT_VILLAGER_LEVEL)) {
            setVillagerLevel(tag.getInt(NBT_VILLAGER_LEVEL));
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
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation nav = new GroundPathNavigation(this, level);
        nav.setCanOpenDoors(true);
        nav.setCanPassDoors(true);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (level().isClientSide) {
            return;
        }

        // Hard guarantee: adult-form entities never keep passengers.
        // If an older world/mod/command forces a passenger, migrate appearance once (legacy only),
        // then immediately eject.
        boolean bodyUnset = this.entityData.get(DATA_BODY_TEXTURE) == null || this.entityData.get(DATA_BODY_TEXTURE).isEmpty();
        boolean professionUnset = this.entityData.get(DATA_PROFESSION) == null || this.entityData.get(DATA_PROFESSION).isEmpty();

        for (Entity passenger : new java.util.ArrayList<>(getPassengers())) {
            if (passenger instanceof CephalariEntity cephalari && (bodyUnset || professionUnset)) {
                syncAppearanceFromCephalari(cephalari);
                bodyUnset = this.entityData.get(DATA_BODY_TEXTURE) == null || this.entityData.get(DATA_BODY_TEXTURE).isEmpty();
                professionUnset = this.entityData.get(DATA_PROFESSION) == null || this.entityData.get(DATA_PROFESSION).isEmpty();
            }
            passenger.stopRiding();
        }

        // Some creation paths can bypass finalizeSpawn; ensure we always get a stable body texture.
        if (bodyUnset && level() instanceof ServerLevelAccessor accessor) {
            setBodyTextureResource(CephalariEntity.pickSpawnBodyTexture(accessor, this.blockPosition(), this.getRandom()));
        }
    }

    private void syncAppearanceFromCephalari(CephalariEntity cephalari) {
        if (cephalari == null || !cephalari.isAlive()) {
            return;
        }

        // Body texture
        setBodyTextureResource(cephalari.getBodyTextureResource());

        // Profession + level
        var villagerData = cephalari.getVillagerData();
        if (villagerData != null) {
            setProfession(villagerData.getProfession());
            setVillagerLevel(villagerData.getLevel());
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
        // Adult-form entities never accept passengers.
        return false;
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public void travel(Vec3 travelVector) {
        super.travel(travelVector);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return level().isClientSide ? InteractionResult.SUCCESS : InteractionResult.PASS;
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
        try (InputStream is = CephalariAdultFormEntity.class.getClassLoader().getResourceAsStream(geoResourcePath)) {
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

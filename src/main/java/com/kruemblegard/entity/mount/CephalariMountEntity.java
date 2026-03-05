package com.kruemblegard.entity.mount;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final String geoResourcePath;
    private final Vec3 defaultSeatOffsetBlocks;

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

    public static AttributeSupplier.Builder createBaseAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 24.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    protected abstract RawAnimation getIdleAnimation();

    protected abstract RawAnimation getMoveAnimation();

    protected abstract RawAnimation getManifestAnimation();

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
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

package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Wayfall flavor: Glow Squid are "air swimmers" and should not suffocate out of water.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallGlowSquidAirEvents {
    private WayfallGlowSquidAirEvents() {}

    private static final String TAG_AIR_SQUID = "kruemblegard_wayfall_air_squid";
    private static final String TAG_AIR_SWIM_TICKS = "kruemblegard_wayfall_air_swim_ticks";
    private static final String TAG_AIR_SWIM_TX = "kruemblegard_wayfall_air_target_x";
    private static final String TAG_AIR_SWIM_TY = "kruemblegard_wayfall_air_target_y";
    private static final String TAG_AIR_SWIM_TZ = "kruemblegard_wayfall_air_target_z";

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof GlowSquid squid)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!event.getLevel().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        enableAirSwim(squid);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (!(living instanceof GlowSquid squid)) {
            return;
        }

        if (squid.level().isClientSide) {
            return;
        }

        boolean inWayfall = squid.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL);
        boolean isMarked = squid.getPersistentData().getBoolean(TAG_AIR_SQUID);

        if (!inWayfall) {
            if (isMarked) {
                squid.getPersistentData().remove(TAG_AIR_SQUID);
                squid.setNoGravity(false);
            }
            return;
        }

        enableAirSwim(squid);

        // In water: behave as vanilla.
        if (squid.isInWaterOrBubble()) {
            squid.setNoGravity(false);
            return;
        }

        // In air: float + drift instead of flopping/dying.
        squid.setNoGravity(true);
        squid.setAirSupply(squid.getMaxAirSupply());

        // Air-swim control: choose a destination and let vanilla MoveControl handle turning/accel.
        // This preserves the "swim" feel and avoids the slippery inertial drift of manual velocity steering.
        var data = squid.getPersistentData();
        int ticks = data.getInt(TAG_AIR_SWIM_TICKS);

        boolean shouldPickNew = ticks <= 0 || squid.horizontalCollision || squid.verticalCollision;
        if (shouldPickNew) {
            pickNewAirSwimTarget(squid);
        } else {
            data.putInt(TAG_AIR_SWIM_TICKS, ticks - 1);
        }

        double tx = data.getDouble(TAG_AIR_SWIM_TX);
        double ty = data.getDouble(TAG_AIR_SWIM_TY) + 0.15D; // small buoyancy bias
        double tz = data.getDouble(TAG_AIR_SWIM_TZ);

        squid.getMoveControl().setWantedPosition(tx, ty, tz, 1.00D);
        squid.getLookControl().setLookAt(tx, ty, tz);

        // Critical: GlowSquid water movement doesn't propel when not "in water".
        // Apply our own steering so air feels like water.
        Vec3 to = new Vec3(tx - squid.getX(), ty - squid.getY(), tz - squid.getZ());
        double distSq = to.lengthSqr();
        if (distSq < 1.5 * 1.5) {
            data.putInt(TAG_AIR_SWIM_TICKS, 0);
        }

        if (distSq > 1.0E-6) {
            Vec3 desired = to.normalize().scale(0.15);
            Vec3 v = squid.getDeltaMovement();

            Vec3 steered = v.add(desired.subtract(v).scale(0.14)).scale(0.95);
            double max = 0.17;
            if (steered.lengthSqr() > max * max) {
                steered = steered.normalize().scale(max);
            }

            steered = new Vec3(steered.x, Mth.clamp(steered.y, -0.12, 0.14), steered.z);
            squid.setDeltaMovement(steered);
            squid.hasImpulse = true;
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof GlowSquid squid)) {
            return;
        }

        if (squid.level().isClientSide) {
            return;
        }

        if (!squid.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (event.getSource().is(DamageTypes.DROWN) || event.getSource().is(DamageTypes.DRY_OUT)) {
            squid.setAirSupply(squid.getMaxAirSupply());
            event.setCanceled(true);
        }
    }

    private static void enableAirSwim(GlowSquid squid) {
        if (!squid.getPersistentData().getBoolean(TAG_AIR_SQUID)) {
            squid.getPersistentData().putBoolean(TAG_AIR_SQUID, true);
        }

        squid.setAirSupply(squid.getMaxAirSupply());
    }

    private static void pickNewAirSwimTarget(GlowSquid squid) {
        var data = squid.getPersistentData();
        RandomSource r = squid.getRandom();

        data.putInt(TAG_AIR_SWIM_TICKS, Mth.nextInt(r, 22, 70));

        Level level = squid.level();
        for (int i = 0; i < 12; i++) {
            double dx = Mth.nextDouble(r, -12.0, 12.0);
            double dy = Mth.nextDouble(r, -8.0, 8.0);
            double dz = Mth.nextDouble(r, -12.0, 12.0);

            double tx = squid.getX() + dx;
            double ty = squid.getY() + dy;
            double tz = squid.getZ() + dz;

            BlockPos bp = BlockPos.containing(tx, ty, tz);
            if (!level.isLoaded(bp)) {
                continue;
            }

            if (!level.getBlockState(bp).isAir()) {
                continue;
            }

            if (!level.noCollision(squid, squid.getBoundingBox().move(tx - squid.getX(), ty - squid.getY(), tz - squid.getZ()))) {
                continue;
            }

            data.putDouble(TAG_AIR_SWIM_TX, tx);
            data.putDouble(TAG_AIR_SWIM_TY, ty);
            data.putDouble(TAG_AIR_SWIM_TZ, tz);
            return;
        }

        data.putDouble(TAG_AIR_SWIM_TX, squid.getX() + Mth.nextDouble(r, -5.0, 5.0));
        data.putDouble(TAG_AIR_SWIM_TY, squid.getY() + Mth.nextDouble(r, -3.0, 3.0));
        data.putDouble(TAG_AIR_SWIM_TZ, squid.getZ() + Mth.nextDouble(r, -5.0, 5.0));
    }
}

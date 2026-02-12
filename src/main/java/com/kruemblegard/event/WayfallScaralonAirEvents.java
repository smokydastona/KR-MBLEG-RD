package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Wayfall flavor: Scaralon Beetles can "air swim" in Underway Falls,
 * drifting through open air similarly to Wayfall Glow Squids.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallScaralonAirEvents {
    private WayfallScaralonAirEvents() {}

    private static final String TAG_AIR_SCARALON = "kruemblegard_wayfall_air_scaralon";
    static final String TAG_AIR_SWIM_TICKS = "kruemblegard_wayfall_air_scaralon_swim_ticks";
    static final String TAG_AIR_SWIM_TX = "kruemblegard_wayfall_air_scaralon_target_x";
    static final String TAG_AIR_SWIM_TY = "kruemblegard_wayfall_air_scaralon_target_y";
    static final String TAG_AIR_SWIM_TZ = "kruemblegard_wayfall_air_scaralon_target_z";

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ScaralonBeetleEntity beetle)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!event.getLevel().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        enableAirSwim(beetle);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (!(living instanceof ScaralonBeetleEntity beetle)) {
            return;
        }

        if (beetle.level().isClientSide) {
            return;
        }

        boolean inWayfall = beetle.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL);
        boolean isMarked = beetle.getPersistentData().getBoolean(TAG_AIR_SCARALON);

        if (!inWayfall) {
            if (isMarked) {
                beetle.getPersistentData().remove(TAG_AIR_SCARALON);
                if (!beetle.isFlying()) {
                    beetle.setNoGravity(false);
                }
            }
            return;
        }

        // Don't interfere with mounted flight/controls.
        if (beetle.isVehicle() || beetle.isFlying()) {
            enableAirSwim(beetle);
            return;
        }

        enableAirSwim(beetle);

        // In water or grounded: behave as vanilla.
        if (beetle.isInWaterOrBubble() || beetle.onGround()) {
            beetle.setNoGravity(false);
            return;
        }

        // In air: float + roam.
        beetle.setNoGravity(true);
        beetle.setAirSupply(beetle.getMaxAirSupply());
        beetle.fallDistance = 0.0F;

        WayfallAirSwimPostTick.queue(beetle);

        var data = beetle.getPersistentData();
        LivingEntity target = beetle.getTarget();
        boolean hasTarget = target != null && target.isAlive();

        int ticks = data.getInt(TAG_AIR_SWIM_TICKS);
        boolean shouldPickNew = ticks <= 0 || beetle.horizontalCollision || beetle.verticalCollision;

        if (hasTarget) {
            data.putInt(TAG_AIR_SWIM_TICKS, 4);
            data.putDouble(TAG_AIR_SWIM_TX, target.getX());
            data.putDouble(TAG_AIR_SWIM_TY, target.getY(0.5D));
            data.putDouble(TAG_AIR_SWIM_TZ, target.getZ());
        } else if (shouldPickNew) {
            pickNewAirSwimTarget(beetle);
        } else {
            data.putInt(TAG_AIR_SWIM_TICKS, ticks - 1);
        }

        double tx = data.getDouble(TAG_AIR_SWIM_TX);
        double ty = data.getDouble(TAG_AIR_SWIM_TY) + 0.10D;
        double tz = data.getDouble(TAG_AIR_SWIM_TZ);

        double speed = hasTarget ? 1.15D : 1.00D;
        beetle.getMoveControl().setWantedPosition(tx, ty, tz, speed);
        beetle.getLookControl().setLookAt(tx, ty, tz);
    }

    private static void enableAirSwim(ScaralonBeetleEntity beetle) {
        if (!beetle.getPersistentData().getBoolean(TAG_AIR_SCARALON)) {
            beetle.getPersistentData().putBoolean(TAG_AIR_SCARALON, true);
        }

        beetle.setAirSupply(beetle.getMaxAirSupply());
    }

    private static void pickNewAirSwimTarget(ScaralonBeetleEntity beetle) {
        var data = beetle.getPersistentData();
        RandomSource r = beetle.getRandom();

        data.putInt(TAG_AIR_SWIM_TICKS, Mth.nextInt(r, 25, 80));

        Level level = beetle.level();
        for (int i = 0; i < 14; i++) {
            double dx = Mth.nextDouble(r, -14.0, 14.0);
            double dy = Mth.nextDouble(r, -10.0, 10.0);
            double dz = Mth.nextDouble(r, -14.0, 14.0);

            double tx = beetle.getX() + dx;
            double ty = beetle.getY() + dy;
            double tz = beetle.getZ() + dz;

            BlockPos bp = BlockPos.containing(tx, ty, tz);
            if (!level.isLoaded(bp)) {
                continue;
            }

            if (!level.getBlockState(bp).isAir()) {
                continue;
            }

            if (!level.noCollision(beetle, beetle.getBoundingBox().move(tx - beetle.getX(), ty - beetle.getY(), tz - beetle.getZ()))) {
                continue;
            }

            data.putDouble(TAG_AIR_SWIM_TX, tx);
            data.putDouble(TAG_AIR_SWIM_TY, ty);
            data.putDouble(TAG_AIR_SWIM_TZ, tz);
            return;
        }

        data.putDouble(TAG_AIR_SWIM_TX, beetle.getX() + Mth.nextDouble(r, -6.0, 6.0));
        data.putDouble(TAG_AIR_SWIM_TY, beetle.getY() + Mth.nextDouble(r, -4.0, 4.0));
        data.putDouble(TAG_AIR_SWIM_TZ, beetle.getZ() + Mth.nextDouble(r, -6.0, 6.0));
    }
}

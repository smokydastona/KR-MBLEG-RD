package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.GlowSquid;
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
    private static final String TAG_AIR_SWIM_VX = "kruemblegard_wayfall_air_swim_vx";
    private static final String TAG_AIR_SWIM_VY = "kruemblegard_wayfall_air_swim_vy";
    private static final String TAG_AIR_SWIM_VZ = "kruemblegard_wayfall_air_swim_vz";

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

        // Birdlike roaming: keep a persistent "target swim velocity" and steer toward it.
        // This makes them actively fly around in open air instead of slowly drifting.
        var data = squid.getPersistentData();
        int ticks = data.getInt(TAG_AIR_SWIM_TICKS);

        boolean shouldPickNew = ticks <= 0 || squid.horizontalCollision || squid.verticalCollision;
        if (shouldPickNew) {
            pickNewAirSwimVector(squid);
            ticks = data.getInt(TAG_AIR_SWIM_TICKS);
        } else {
            data.putInt(TAG_AIR_SWIM_TICKS, ticks - 1);
        }

        Vec3 target = new Vec3(
                data.getDouble(TAG_AIR_SWIM_VX),
                data.getDouble(TAG_AIR_SWIM_VY),
                data.getDouble(TAG_AIR_SWIM_VZ)
        );

        // Gentle vertical bias: keep them from slowly sinking.
        target = new Vec3(target.x, target.y + 0.01, target.z);

        Vec3 v = squid.getDeltaMovement();
        // Steer toward the target while keeping a soft "water-like" damping.
        Vec3 steered = v.add(target.subtract(v).scale(0.08)).scale(0.965);

        // Cap speed so they feel floaty, not like rockets.
        double max = 0.16;
        if (steered.lengthSqr() > max * max) {
            steered = steered.normalize().scale(max);
        }

        // Prevent hard falls.
        steered = new Vec3(steered.x, Mth.clamp(steered.y, -0.10, 0.12), steered.z);

        squid.setDeltaMovement(steered);
        squid.hasImpulse = true;
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

    private static void pickNewAirSwimVector(GlowSquid squid) {
        var data = squid.getPersistentData();
        RandomSource r = squid.getRandom();

        // Hold a direction for a short burst.
        data.putInt(TAG_AIR_SWIM_TICKS, Mth.nextInt(r, 30, 110));

        double yaw = r.nextDouble() * (Math.PI * 2.0);
        // Mild pitch: mostly horizontal flight with occasional climbs/dives.
        double pitch = (r.nextDouble() - 0.5) * 0.7; // [-0.35, 0.35]
        double speed = Mth.nextDouble(r, 0.06, 0.14);

        double xz = Math.cos(pitch) * speed;
        double vx = Math.cos(yaw) * xz;
        double vz = Math.sin(yaw) * xz;
        double vy = Math.sin(pitch) * speed;

        data.putDouble(TAG_AIR_SWIM_VX, vx);
        data.putDouble(TAG_AIR_SWIM_VY, vy);
        data.putDouble(TAG_AIR_SWIM_VZ, vz);
    }
}

package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Wayfall flavor: Axolotls are "air swimmers" in Underway Falls.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallAxolotlAirEvents {
    private WayfallAxolotlAirEvents() {}

    private static final String TAG_AIR_AXOLOTL = "kruemblegard_wayfall_air_axolotl";
    private static final String TAG_AIR_SWIM_TICKS = "kruemblegard_wayfall_air_axolotl_swim_ticks";
    private static final String TAG_AIR_SWIM_VX = "kruemblegard_wayfall_air_axolotl_swim_vx";
    private static final String TAG_AIR_SWIM_VY = "kruemblegard_wayfall_air_axolotl_swim_vy";
    private static final String TAG_AIR_SWIM_VZ = "kruemblegard_wayfall_air_axolotl_swim_vz";

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Axolotl axolotl)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!event.getLevel().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        enableAirSwim(axolotl);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (!(living instanceof Axolotl axolotl)) {
            return;
        }

        if (axolotl.level().isClientSide) {
            return;
        }

        boolean inWayfall = axolotl.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL);
        boolean isMarked = axolotl.getPersistentData().getBoolean(TAG_AIR_AXOLOTL);

        if (!inWayfall) {
            if (isMarked) {
                axolotl.getPersistentData().remove(TAG_AIR_AXOLOTL);
                axolotl.setNoGravity(false);
            }
            return;
        }

        enableAirSwim(axolotl);

        // In water: behave as vanilla.
        if (axolotl.isInWaterOrBubble()) {
            axolotl.setNoGravity(false);
            return;
        }

        // In air: float + roam (water-like swimming).
        axolotl.setNoGravity(true);
        axolotl.setAirSupply(axolotl.getMaxAirSupply());

        var data = axolotl.getPersistentData();
        int ticks = data.getInt(TAG_AIR_SWIM_TICKS);

        boolean shouldPickNew = ticks <= 0 || axolotl.horizontalCollision || axolotl.verticalCollision;
        if (shouldPickNew) {
            pickNewAirSwimVector(axolotl);
            ticks = data.getInt(TAG_AIR_SWIM_TICKS);
        } else {
            data.putInt(TAG_AIR_SWIM_TICKS, ticks - 1);
        }

        Vec3 target = new Vec3(
                data.getDouble(TAG_AIR_SWIM_VX),
                data.getDouble(TAG_AIR_SWIM_VY),
                data.getDouble(TAG_AIR_SWIM_VZ)
        );

        // Slight upward bias to avoid slow sinking.
        target = new Vec3(target.x, target.y + 0.008, target.z);

        Vec3 v = axolotl.getDeltaMovement();
        Vec3 steered = v.add(target.subtract(v).scale(0.10)).scale(0.965);

        double max = 0.14;
        if (steered.lengthSqr() > max * max) {
            steered = steered.normalize().scale(max);
        }

        steered = new Vec3(steered.x, Mth.clamp(steered.y, -0.10, 0.12), steered.z);

        axolotl.setDeltaMovement(steered);
        axolotl.hasImpulse = true;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Axolotl axolotl)) {
            return;
        }

        if (axolotl.level().isClientSide) {
            return;
        }

        if (!axolotl.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (event.getSource().is(DamageTypes.DROWN) || event.getSource().is(DamageTypes.DRY_OUT)) {
            axolotl.setAirSupply(axolotl.getMaxAirSupply());
            event.setCanceled(true);
        }
    }

    private static void enableAirSwim(Axolotl axolotl) {
        if (!axolotl.getPersistentData().getBoolean(TAG_AIR_AXOLOTL)) {
            axolotl.getPersistentData().putBoolean(TAG_AIR_AXOLOTL, true);
        }

        axolotl.setAirSupply(axolotl.getMaxAirSupply());
    }

    private static void pickNewAirSwimVector(Axolotl axolotl) {
        var data = axolotl.getPersistentData();
        RandomSource r = axolotl.getRandom();

        data.putInt(TAG_AIR_SWIM_TICKS, Mth.nextInt(r, 25, 90));

        double yaw = r.nextDouble() * (Math.PI * 2.0);
        double pitch = (r.nextDouble() - 0.5) * 0.8; // [-0.4, 0.4]
        double speed = Mth.nextDouble(r, 0.05, 0.12);

        double xz = Math.cos(pitch) * speed;
        double vx = Math.cos(yaw) * xz;
        double vz = Math.sin(yaw) * xz;
        double vy = Math.sin(pitch) * speed;

        data.putDouble(TAG_AIR_SWIM_VX, vx);
        data.putDouble(TAG_AIR_SWIM_VY, vy);
        data.putDouble(TAG_AIR_SWIM_VZ, vz);
    }
}

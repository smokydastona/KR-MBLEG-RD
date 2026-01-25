package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.level.Level;

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
    private static final String TAG_AIR_SWIM_TX = "kruemblegard_wayfall_air_axolotl_target_x";
    private static final String TAG_AIR_SWIM_TY = "kruemblegard_wayfall_air_axolotl_target_y";
    private static final String TAG_AIR_SWIM_TZ = "kruemblegard_wayfall_air_axolotl_target_z";

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
        LivingEntity target = axolotl.getTarget();
        boolean hasTarget = target != null && target.isAlive();

        int ticks = data.getInt(TAG_AIR_SWIM_TICKS);
        boolean shouldPickNew = ticks <= 0 || axolotl.horizontalCollision || axolotl.verticalCollision;

        if (hasTarget) {
            // If the axolotl has a combat target (fish/hostiles), drive toward it.
            // This preserves vanilla targeting/goal selection, but ensures air movement still works.
            data.putInt(TAG_AIR_SWIM_TICKS, 3);
            data.putDouble(TAG_AIR_SWIM_TX, target.getX());
            data.putDouble(TAG_AIR_SWIM_TY, target.getY(0.5D));
            data.putDouble(TAG_AIR_SWIM_TZ, target.getZ());
        } else if (shouldPickNew) {
            pickNewAirSwimTarget(axolotl);
        } else {
            data.putInt(TAG_AIR_SWIM_TICKS, ticks - 1);
        }

        double tx = data.getDouble(TAG_AIR_SWIM_TX);
        double ty = data.getDouble(TAG_AIR_SWIM_TY);
        double tz = data.getDouble(TAG_AIR_SWIM_TZ);

        // Gentle upward bias helps them not slowly sink over time.
        ty += 0.10D;

        double speed = hasTarget ? 1.25D : 1.00D;
        axolotl.getMoveControl().setWantedPosition(tx, ty, tz, speed);
        axolotl.getLookControl().setLookAt(tx, ty, tz);
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

    private static void pickNewAirSwimTarget(Axolotl axolotl) {
        var data = axolotl.getPersistentData();
        RandomSource r = axolotl.getRandom();

        data.putInt(TAG_AIR_SWIM_TICKS, Mth.nextInt(r, 18, 45));

        Level level = axolotl.level();
        for (int i = 0; i < 12; i++) {
            double dx = Mth.nextDouble(r, -10.0, 10.0);
            double dy = Mth.nextDouble(r, -6.0, 6.0);
            double dz = Mth.nextDouble(r, -10.0, 10.0);

            double tx = axolotl.getX() + dx;
            double ty = axolotl.getY() + dy;
            double tz = axolotl.getZ() + dz;

            BlockPos bp = BlockPos.containing(tx, ty, tz);
            if (!level.isLoaded(bp)) {
                continue;
            }

            // Prefer open air to avoid them trying to swim into solid blocks.
            if (!level.getBlockState(bp).isAir()) {
                continue;
            }
            if (!level.noCollision(axolotl, axolotl.getBoundingBox().move(tx - axolotl.getX(), ty - axolotl.getY(), tz - axolotl.getZ()))) {
                continue;
            }

            data.putDouble(TAG_AIR_SWIM_TX, tx);
            data.putDouble(TAG_AIR_SWIM_TY, ty);
            data.putDouble(TAG_AIR_SWIM_TZ, tz);
            return;
        }

        // Fallback: small local drift.
        data.putDouble(TAG_AIR_SWIM_TX, axolotl.getX() + Mth.nextDouble(r, -4.0, 4.0));
        data.putDouble(TAG_AIR_SWIM_TY, axolotl.getY() + Mth.nextDouble(r, -2.0, 2.0));
        data.putDouble(TAG_AIR_SWIM_TZ, axolotl.getZ() + Mth.nextDouble(r, -4.0, 4.0));
    }
}

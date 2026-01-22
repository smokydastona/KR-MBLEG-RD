package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.util.Mth;
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

        Vec3 v = squid.getDeltaMovement();

        // Prevent hard falls and keep a gentle buoyant feel.
        double clampedY = Mth.clamp(v.y, -0.06, 0.06);
        if (clampedY < 0.0) {
            clampedY += 0.02;
        }

        double horizontalSpeed = Math.sqrt(v.x * v.x + v.z * v.z);
        if (horizontalSpeed < 0.02) {
            double angle = squid.getRandom().nextDouble() * (Math.PI * 2.0);
            double speed = 0.05;
            v = new Vec3(Math.cos(angle) * speed, clampedY, Math.sin(angle) * speed);
        } else {
            v = new Vec3(v.x * 0.98, clampedY, v.z * 0.98);
        }

        squid.setDeltaMovement(v);
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
}

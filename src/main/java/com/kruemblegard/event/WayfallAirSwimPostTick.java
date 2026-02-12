package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies Wayfall air-swim propulsion after vanilla entity AI/ticking.
 *
 * In vanilla, water-specialized mobs don't reliably propel themselves when not in water,
 * even if we set MoveControl targets. To make "air behave like water" for these Wayfall
 * flavor mobs, we apply a swim-style movement step at the end of the server tick.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallAirSwimPostTick {

    private static final List<Axolotl> AXOLOTLS = new ArrayList<>();
    private static final List<GlowSquid> SQUIDS = new ArrayList<>();
    private static final List<ScaralonBeetleEntity> SCARALONS = new ArrayList<>();

    private WayfallAirSwimPostTick() {}

    static void queue(Axolotl axolotl) {
        AXOLOTLS.add(axolotl);
    }

    static void queue(GlowSquid squid) {
        SQUIDS.add(squid);
    }

    static void queue(ScaralonBeetleEntity beetle) {
        SCARALONS.add(beetle);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!AXOLOTLS.isEmpty()) {
            for (Axolotl axolotl : AXOLOTLS) {
                if (axolotl == null || !axolotl.isAlive()) {
                    continue;
                }

                if (!axolotl.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
                    continue;
                }

                // In water: vanilla behavior.
                if (axolotl.isInWaterOrBubble()) {
                    continue;
                }

                // Keep them buoyant and breathing.
                axolotl.setNoGravity(true);
                axolotl.setAirSupply(axolotl.getMaxAirSupply());

                var data = axolotl.getPersistentData();
                double tx = data.getDouble(WayfallAxolotlAirEvents.TAG_AIR_SWIM_TX);
                double ty = data.getDouble(WayfallAxolotlAirEvents.TAG_AIR_SWIM_TY) + 0.10D;
                double tz = data.getDouble(WayfallAxolotlAirEvents.TAG_AIR_SWIM_TZ);

                Vec3 to = new Vec3(tx - axolotl.getX(), ty - axolotl.getY(), tz - axolotl.getZ());
                double distSq = to.lengthSqr();
                if (distSq < 1.25 * 1.25) {
                    data.putInt(WayfallAxolotlAirEvents.TAG_AIR_SWIM_TICKS, 0);
                    continue;
                }

                Vec3 step = to.normalize().scale(0.15);
                Vec3 v = axolotl.getDeltaMovement();
                Vec3 steered = v.add(step.subtract(v).scale(0.30)).scale(0.96);
                steered = new Vec3(steered.x, Mth.clamp(steered.y, -0.14, 0.16), steered.z);

                axolotl.setDeltaMovement(steered);
                axolotl.move(MoverType.SELF, steered);
                axolotl.hasImpulse = true;
            }
            AXOLOTLS.clear();
        }

        if (!SQUIDS.isEmpty()) {
            for (GlowSquid squid : SQUIDS) {
                if (squid == null || !squid.isAlive()) {
                    continue;
                }

                if (!squid.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
                    continue;
                }

                if (squid.isInWaterOrBubble()) {
                    continue;
                }

                squid.setNoGravity(true);
                squid.setAirSupply(squid.getMaxAirSupply());

                var data = squid.getPersistentData();
                double tx = data.getDouble(WayfallGlowSquidAirEvents.TAG_AIR_SWIM_TX);
                double ty = data.getDouble(WayfallGlowSquidAirEvents.TAG_AIR_SWIM_TY) + 0.15D;
                double tz = data.getDouble(WayfallGlowSquidAirEvents.TAG_AIR_SWIM_TZ);

                Vec3 to = new Vec3(tx - squid.getX(), ty - squid.getY(), tz - squid.getZ());
                double distSq = to.lengthSqr();
                if (distSq < 1.6 * 1.6) {
                    data.putInt(WayfallGlowSquidAirEvents.TAG_AIR_SWIM_TICKS, 0);
                    continue;
                }

                Vec3 step = to.normalize().scale(0.17);
                Vec3 v = squid.getDeltaMovement();
                Vec3 steered = v.add(step.subtract(v).scale(0.24)).scale(0.97);
                steered = new Vec3(steered.x, Mth.clamp(steered.y, -0.14, 0.16), steered.z);

                squid.setDeltaMovement(steered);
                squid.move(MoverType.SELF, steered);
                squid.hasImpulse = true;
            }
            SQUIDS.clear();
        }

        if (!SCARALONS.isEmpty()) {
            for (ScaralonBeetleEntity beetle : SCARALONS) {
                if (beetle == null || !beetle.isAlive()) {
                    continue;
                }

                if (!beetle.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
                    continue;
                }

                // Do not interfere with mounted flight or ground movement.
                if (beetle.isVehicle() || beetle.isFlying() || beetle.onGround()) {
                    continue;
                }

                if (beetle.isInWaterOrBubble()) {
                    continue;
                }

                beetle.setNoGravity(true);
                beetle.setAirSupply(beetle.getMaxAirSupply());
                beetle.fallDistance = 0.0F;

                var data = beetle.getPersistentData();
                double tx = data.getDouble(WayfallScaralonAirEvents.TAG_AIR_SWIM_TX);
                double ty = data.getDouble(WayfallScaralonAirEvents.TAG_AIR_SWIM_TY) + 0.10D;
                double tz = data.getDouble(WayfallScaralonAirEvents.TAG_AIR_SWIM_TZ);

                Vec3 to = new Vec3(tx - beetle.getX(), ty - beetle.getY(), tz - beetle.getZ());
                double distSq = to.lengthSqr();
                if (distSq < 1.75 * 1.75) {
                    data.putInt(WayfallScaralonAirEvents.TAG_AIR_SWIM_TICKS, 0);
                    continue;
                }

                Vec3 step = to.normalize().scale(0.16);
                Vec3 v = beetle.getDeltaMovement();
                Vec3 steered = v.add(step.subtract(v).scale(0.22)).scale(0.96);
                steered = new Vec3(steered.x, Mth.clamp(steered.y, -0.16, 0.18), steered.z);

                beetle.setDeltaMovement(steered);
                beetle.move(MoverType.SELF, steered);
                beetle.hasImpulse = true;
            }
            SCARALONS.clear();
        }
    }
}

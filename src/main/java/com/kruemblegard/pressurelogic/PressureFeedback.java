package com.kruemblegard.pressurelogic;

import com.kruemblegard.blockentity.CephalariMachineBlockEntity;
import com.kruemblegard.blockentity.PressureConduitBlockEntity;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.pressurelogic.capability.IPressureHandler;
import com.kruemblegard.pressurelogic.capability.PressureCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.common.util.LazyOptional;

public final class PressureFeedback {
    private PressureFeedback() {}

    /**
     * Empty-hand right-click inspection for Pressure-Logic blocks.
     *
     * Server: prints a compact status line.
     * Client: returns SUCCESS so the interaction feels responsive.
     */
    public static InteractionResult tryInspect(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        boolean stableAir = PressureAtmosphere.isStable(level, pos);
        boolean powered = state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED);

        int nearbyPressure = getBestNeighborConduitPressure(level, pos);
        Integer selfPressure = getSelfConduitPressure(level, pos);

        int activeTicks = 0;
        int machineAnimBucket = -1;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CephalariMachineBlockEntity machineBe) {
            activeTicks = machineBe.getActiveTicks();
            // Give the player a tiny hint that "animation" exists without flooding them.
            machineAnimBucket = ((int) Math.floor((machineBe.getAnim() / (float) Math.PI) * 8.0)) & 7;
        }

        String pressureInfo;
        if (selfPressure != null) {
            pressureInfo = "p=" + selfPressure;
        } else {
            pressureInfo = "p~" + nearbyPressure;
        }

        String capInfo = getPressureCapabilityInfo(be, hit.getDirection());

        String sysInfo = ModConfig.PRESSURE_SYSTEM_ENABLED.get() ? "" : " disabled";

        String msg = "Pressure" + sysInfo + ": " +
                (stableAir ? "stable" : "unstable") +
                (powered ? " powered" : "") +
                " " + pressureInfo +
                (capInfo == null ? "" : (" " + capInfo)) +
                (activeTicks > 0 ? (" activeTicks=" + activeTicks) : "") +
                (machineAnimBucket >= 0 ? (" anim=" + machineAnimBucket) : "");

        player.displayClientMessage(Component.literal(msg), true);
        return InteractionResult.CONSUME;
    }

    /**
     * Minimal "it is working" feedback.
     * Emits subtle air/steam particles when powered and/or near pressurized conduits.
     */
    public static void animateWorking(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!ModConfig.PRESSURE_SYSTEM_ENABLED.get()) {
            return;
        }

        boolean stableAir = PressureAtmosphere.isStable(level, pos);
        if (!stableAir) {
            return;
        }

        boolean powered = state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED);
        int nearbyPressure = getBestNeighborConduitPressure(level, pos);
        int selfPressure = getSelfConduitPressure(level, pos) == null ? 0 : getSelfConduitPressure(level, pos);
        int p = Math.max(nearbyPressure, selfPressure);

        if (!powered && p <= 0) {
            return;
        }

        int chance = powered ? 6 : 10;
        if (p >= 50) {
            chance = Math.max(3, chance - 2);
        } else if (p >= 20) {
            chance = Math.max(4, chance - 1);
        }

        if (random.nextInt(chance) != 0) {
            return;
        }

        double x = pos.getX() + 0.2 + (random.nextDouble() * 0.6);
        double y = pos.getY() + 0.8 + (random.nextDouble() * 0.15);
        double z = pos.getZ() + 0.2 + (random.nextDouble() * 0.6);

        double dx = (random.nextDouble() - 0.5) * 0.01;
        double dy = 0.02 + random.nextDouble() * 0.01;
        double dz = (random.nextDouble() - 0.5) * 0.01;

        level.addParticle(ParticleTypes.CLOUD, x, y, z, dx, dy, dz);
    }

    private static int getBestNeighborConduitPressure(Level level, BlockPos pos) {
        int best = 0;
        for (Direction dir : Direction.values()) {
            best = Math.max(best, PressureUtil.getConduitPressureOrState(level, pos.relative(dir)));
        }
        return best;
    }

    private static Integer getSelfConduitPressure(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PressureConduitBlockEntity be) {
            return be.getPressure().get();
        }
        // If it's a conduit but BE isn't present, fall back to the visual state representation.
        int p = PressureUtil.getConduitPressureOrState(level, pos);
        if (p > 0) {
            return p;
        }
        return null;
    }

    private static String getPressureCapabilityInfo(BlockEntity be, Direction side) {
        if (be == null) {
            return null;
        }

        LazyOptional<IPressureHandler> cap = be.getCapability(PressureCapabilities.PRESSURE_HANDLER, side);
        if (!cap.isPresent()) {
            cap = be.getCapability(PressureCapabilities.PRESSURE_HANDLER, null);
        }

        if (!cap.isPresent()) {
            return null;
        }

        IPressureHandler handler = cap.orElse(null);
        if (handler == null) {
            return null;
        }

        return "cap=" + handler.getPressure() + "/" + handler.getMaxPressure();
    }
}

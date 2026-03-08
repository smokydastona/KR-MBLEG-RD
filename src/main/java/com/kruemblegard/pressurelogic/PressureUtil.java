package com.kruemblegard.pressurelogic;

import com.kruemblegard.block.PressureConduitBlock;
import com.kruemblegard.block.PressureValveBlock;
import com.kruemblegard.blockentity.PressureConduitBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class PressureUtil {
    private PressureUtil() {}

    public static int pressureToLevel(int pressure) {
        // 0..100 -> 0..5
        return Mth.clamp((int) Math.round((pressure / 100.0) * 5.0), 0, 5);
    }

    public static int levelToPressure(int level) {
        // 0..5 -> 0..100
        return Mth.clamp((int) Math.round((level / 5.0) * 100.0), 0, 100);
    }

    public static void syncConduitVisualState(Level level, BlockPos pos, int pressure) {
        if (!level.isLoaded(pos)) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof PressureConduitBlock)) {
            return;
        }

        int level5 = pressureToLevel(pressure);
        int currentLevel5 = state.getValue(PressureConduitBlock.PRESSURE_LEVEL);
        if (level5 != currentLevel5) {
            level.setBlock(pos, state.setValue(PressureConduitBlock.PRESSURE_LEVEL, level5), 2);
        }
    }

    public static int approach(int current, int target, int maxStep) {
        if (target > current) {
            return Math.min(target, current + maxStep);
        }
        if (target < current) {
            return Math.max(target, current - maxStep);
        }
        return current;
    }

    public static @Nullable PressureConduitBlockEntity getConduitEntity(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PressureConduitBlockEntity be) {
            return be;
        }
        return null;
    }

    public static int getConduitPressureOrState(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return 0;
        }

        PressureConduitBlockEntity be = getConduitEntity(level, pos);
        if (be != null) {
            return be.getPressure().get();
        }

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PressureConduitBlock) {
            return levelToPressure(state.getValue(PressureConduitBlock.PRESSURE_LEVEL));
        }

        return 0;
    }

    public static void addPressure(Level level, BlockPos conduitPos, int delta) {
        PressureConduitBlockEntity be = getConduitEntity(level, conduitPos);
        if (be == null) {
            return;
        }
        int before = be.getPressure().get();
        be.getPressure().add(delta);
        if (be.getPressure().get() != before) {
            be.setChanged();
        }
    }

    public static void setPressure(Level level, BlockPos conduitPos, int pressure) {
        PressureConduitBlockEntity be = getConduitEntity(level, conduitPos);
        if (be == null) {
            return;
        }
        int before = be.getPressure().get();
        be.getPressure().set(pressure);
        if (be.getPressure().get() != before) {
            be.setChanged();
        }
    }

    /**
     * Returns a list of conduit positions that are connected for fluid flow from {@code pos}.
     * Supports in-line {@link PressureValveBlock} as a 2-block hop.
     */
    public static List<BlockPos> getConnectedConduits(Level level, BlockPos pos) {
        ArrayList<BlockPos> results = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            BlockPos direct = pos.relative(dir);
            if (!level.isLoaded(direct)) {
                continue;
            }
            BlockState directState = level.getBlockState(direct);
            if (directState.getBlock() instanceof PressureConduitBlock) {
                results.add(direct);
                continue;
            }

            // Allow in-line valves to act as a gate between conduits.
            if (directState.getBlock() instanceof PressureValveBlock) {
                if (!directState.getValue(PressureValveBlock.POWERED)) {
                    continue;
                }

                Direction valveFacing = directState.getValue(PressureValveBlock.FACING);
                if (!(dir == valveFacing || dir == valveFacing.getOpposite())) {
                    continue;
                }

                BlockPos otherSide = direct.relative(dir);
                if (!level.isLoaded(otherSide)) {
                    continue;
                }
                BlockState otherState = level.getBlockState(otherSide);
                if (otherState.getBlock() instanceof PressureConduitBlock) {
                    results.add(otherSide);
                }
            }
        }
        return results;
    }

    /**
     * Computes the neighborhood average pressure (including self) for conduit diffusion.
     */
    public static int sampleNeighborAveragePressure(Level level, BlockPos pos) {
        int sum = 0;
        int count = 0;

        int self = getConduitPressureOrState(level, pos);
        sum += self;
        count++;

        for (BlockPos neighbor : getConnectedConduits(level, pos)) {
            sum += getConduitPressureOrState(level, neighbor);
            count++;
        }

        if (count <= 0) {
            return 0;
        }
        return (int) Math.round(sum / (double) count);
    }
}

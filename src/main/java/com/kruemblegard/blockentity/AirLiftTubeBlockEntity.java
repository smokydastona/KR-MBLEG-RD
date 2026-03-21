package com.kruemblegard.blockentity;

import com.kruemblegard.block.AirLiftTubeBlock;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AirLiftTubeBlockEntity extends CephalariMachineBlockEntity {
    private float flow;

    public AirLiftTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AIR_LIFT_TUBE.get(), pos, state);
        this.flow = 0.0F;
    }

    public float getFlow() {
        return flow;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AirLiftTubeBlockEntity be) {
        float control = 0.0F;
        if (state.getValue(AirLiftTubeBlock.POWERED) || state.getValue(AirLiftTubeBlock.TUBE_MODE) == AirLiftTubeBlock.TubeMode.BIDIRECTIONAL) {
            int best = 0;
            for (Direction dir : Direction.values()) {
                best = Math.max(best, PressureUtil.getConduitPressureOrState(level, pos.relative(dir)));
            }
            control = PressureUtil.pressureToLevel(best) / 5.0F;
        }

        float rateMul = switch (state.getValue(AirLiftTubeBlock.FLOW_RATE)) {
            case SOFT -> 0.7F;
            case HIGH -> 1.3F;
            default -> 1.0F;
        };

        be.flow = Mth.lerp(0.20F, be.flow, control);
        be.stepAnim(be.flow <= 0.0F ? 0.0F : (0.02F + be.flow * 0.03F) * rateMul);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AirLiftTubeBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 80) != 0) {
            return;
        }
        if (state.getValue(AirLiftTubeBlock.POWERED)) {
            be.recordActiveSample(80);
        }
    }
}

package com.kruemblegard.block;

import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BuoyancyLiftPlatformBlock extends Block {

    public enum LiftState implements StringRepresentable {
        IDLE("idle"),
        RISING("rising"),
        FALLING("falling");

        private final String name;

        LiftState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final EnumProperty<LiftState> LIFT_STATE = EnumProperty.create("lift_state", LiftState.class);

    public BuoyancyLiftPlatformBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIFT_STATE, LiftState.IDLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIFT_STATE});
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        BlockState state = this.defaultBlockState().setValue(LIFT_STATE, powered ? LiftState.RISING : LiftState.IDLE);
        if (!context.getLevel().isClientSide) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 1);
        }
        return state;
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos fromPos,
            boolean isMoving) {
        if (level.isClientSide) {
            return;
        }

        boolean powered = level.hasNeighborSignal(pos);
        LiftState current = state.getValue(LIFT_STATE);

        if (powered) {
            if (current != LiftState.RISING) {
                level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.RISING), 2);
            }
        } else {
            if (current == LiftState.RISING) {
                level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.FALLING), 2);
            }
        }

        level.scheduleTick(pos, this, 1);

        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        LiftState liftState = state.getValue(LIFT_STATE);

        if (!PressureAtmosphere.isStable(level, pos)) {
            if (liftState != LiftState.IDLE) {
                level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.IDLE), 2);
            }
            return;
        }

        // Pressure input is taken from directly below if it's a conduit.
        int available = 0;
        BlockPos below = pos.below();
        if (level.getBlockState(below).getBlock() instanceof PressureConduitBlock) {
            available = PressureUtil.getConduitPressureOrState(level, below);
        }

        if (liftState == LiftState.RISING) {
            // Need some minimum pressure to lift.
            if (available < 20) {
                level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.IDLE), 2);
                return;
            }

            // Don't try to lift into a blocked space.
            if (!level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
                level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.IDLE), 2);
                return;
            }

            double liftSpeed = Mth.clamp(available / 100.0, 0.2, 1.0) * 0.18; // ~0.036..0.18
            int consume = Math.max(1, (int) Math.round(liftSpeed * 30));
            PressureUtil.addPressure(level, below, -consume);

            AABB box = new AABB(pos).inflate(0.48, 0.2, 0.48).move(0, 1.0, 0);
            List<Entity> entities = level.getEntities((Entity) null, box, Entity::isAlive);
            for (Entity e : entities) {
                Vec3 v = e.getDeltaMovement();
                double vy = Math.max(v.y, liftSpeed);
                e.setDeltaMovement(v.x, vy, v.z);
                e.hurtMarked = true;
            }
        } else if (liftState == LiftState.FALLING) {
            AABB box = new AABB(pos).inflate(0.48, 0.2, 0.48).move(0, 1.0, 0);
            List<Entity> entities = level.getEntities((Entity) null, box, Entity::isAlive);
            for (Entity e : entities) {
                Vec3 v = e.getDeltaMovement();
                // Gentle downward pull, but don't slam.
                double vy = Math.min(v.y, -0.08);
                e.setDeltaMovement(v.x, vy, v.z);
                e.hurtMarked = true;
            }

            // Stop falling once unpowered and stabilized.
            if (!level.hasNeighborSignal(pos)) {
                level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.IDLE), 2);
                return;
            }
        }

        level.scheduleTick(pos, this, 1);
    }
}

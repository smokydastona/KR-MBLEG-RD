package com.kruemblegard.block;

import com.kruemblegard.blockentity.PneumaticSeparatorBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.util.BlockEntityTickerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PneumaticSeparatorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public enum ActiveSide implements StringRepresentable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        ActiveSide(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public enum SeparatorMode implements StringRepresentable {
        NORMAL("normal"),
        TRI("tri"),
        DENSITY("density");

        private final String name;

        SeparatorMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final EnumProperty<ActiveSide> ACTIVE_SIDE = EnumProperty.create("active_side", ActiveSide.class);
    public static final EnumProperty<SeparatorMode> SEPARATOR_MODE = EnumProperty.create("separator_mode", SeparatorMode.class);

    public PneumaticSeparatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(ACTIVE_SIDE, ActiveSide.LEFT)
                .setValue(SEPARATOR_MODE, SeparatorMode.NORMAL));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        int signal = context.getLevel().getBestNeighborSignal(context.getClickedPos());
        boolean powered = signal > 0;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, powered)
                .setValue(ACTIVE_SIDE, ActiveSide.LEFT)
            .setValue(SEPARATOR_MODE, modeFromSignal(signal));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, ACTIVE_SIDE, SEPARATOR_MODE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PneumaticSeparatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.PNEUMATIC_SEPARATOR.get(), PneumaticSeparatorBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.PNEUMATIC_SEPARATOR.get(), PneumaticSeparatorBlockEntity::serverTick);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (level.isClientSide) {
            return;
        }

        int signal = level.getBestNeighborSignal(pos);
        boolean poweredNow = signal > 0;
        BlockState next = state
                .setValue(POWERED, poweredNow)
                .setValue(SEPARATOR_MODE, modeFromSignal(signal));
        if (next != state) {
            level.setBlock(pos, next, Block.UPDATE_CLIENTS);
        }

        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) {
            level.scheduleTick(pos, this, 10);
            return;
        }

        if (!PressureAtmosphere.isStable(level, pos)) {
            level.scheduleTick(pos, this, 10);
            return;
        }

        BlockPos conduitPos = findBestConduit(level, pos);
        if (conduitPos == null) {
            level.scheduleTick(pos, this, 10);
            return;
        }

        int pressure = PressureUtil.getConduitPressureOrState(level, conduitPos);
        if (pressure <= 0) {
            level.scheduleTick(pos, this, 10);
            return;
        }

        int pressureLevel = PressureUtil.pressureToLevel(pressure);
        int costPerItem = Math.max(1, 2 - (pressureLevel >= 4 ? 1 : 0));

        Direction facing = state.getValue(FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        AABB box = new AABB(pos).inflate(0.6, 0.6, 0.6).move(0, 1.0, 0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());

        for (ItemEntity itemEntity : items) {
            if (PressureUtil.getConduitPressureOrState(level, conduitPos) < costPerItem) {
                break;
            }

            ItemStack stack = itemEntity.getItem();

            Direction outDir;
            if (state.getValue(SEPARATOR_MODE) == SeparatorMode.TRI) {
                if (stack.is(net.minecraft.tags.ItemTags.LOGS)) {
                    outDir = left;
                } else if (stack.is(net.minecraft.tags.ItemTags.STONE_CRAFTING_MATERIALS)) {
                    outDir = right;
                } else {
                    outDir = facing;
                }
            } else if (state.getValue(SEPARATOR_MODE) == SeparatorMode.DENSITY) {
                // "Density" heuristic: block-items are treated as heavier.
                if (stack.is(net.minecraft.tags.ItemTags.LOGS)) {
                    outDir = left;
                } else if (stack.getItem() instanceof BlockItem) {
                    outDir = right;
                } else {
                    outDir = left;
                }
            } else {
                outDir = stack.is(net.minecraft.tags.ItemTags.LOGS) ? left : right;
            }

            PressureUtil.addPressure(level, conduitPos, -costPerItem);

            double speed = 0.10 + 0.02 * pressureLevel;
            itemEntity.setDeltaMovement(outDir.getStepX() * speed, 0.05, outDir.getStepZ() * speed);
            itemEntity.hurtMarked = true;

            ActiveSide side = (outDir == left) ? ActiveSide.LEFT : ActiveSide.RIGHT;
            if (outDir != facing && side != state.getValue(ACTIVE_SIDE)) {
                level.setBlock(pos, state.setValue(ACTIVE_SIDE, side), 2);
                state = level.getBlockState(pos);
            }
        }

        level.scheduleTick(pos, this, 10);
    }

    private static SeparatorMode modeFromSignal(int signal) {
        if (signal <= 0) {
            return SeparatorMode.NORMAL;
        }
        if (signal <= 5) {
            return SeparatorMode.NORMAL;
        }
        if (signal <= 10) {
            return SeparatorMode.TRI;
        }
        return SeparatorMode.DENSITY;
    }

    private static @Nullable BlockPos findBestConduit(Level level, BlockPos pos) {
        BlockPos best = null;
        int bestPressure = 0;

        BlockPos below = pos.below();
        int belowPressure = PressureUtil.getConduitPressureOrState(level, below);
        if (belowPressure > 0) {
            best = below;
            bestPressure = belowPressure;
        }

        for (Direction dir : Direction.values()) {
            BlockPos p = pos.relative(dir);
            int pressure = PressureUtil.getConduitPressureOrState(level, p);
            if (pressure > bestPressure) {
                best = p;
                bestPressure = pressure;
            }
        }

        return best;
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
}

package com.kruemblegard.block;

import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.rotationlogic.RotationUtil;
import com.kruemblegard.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PressureLoomBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final IntegerProperty WEAVE_PHASE = IntegerProperty.create("weave_phase", 0, 3);

    public PressureLoomBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(WEAVE_PHASE, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().getBestNeighborSignal(context.getClickedPos()) > 0;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, powered);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, WEAVE_PHASE);
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

        boolean poweredNow = level.hasNeighborSignal(pos);
        if (poweredNow != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, poweredNow), Block.UPDATE_CLIENTS);
        }

        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean powered = state.getValue(POWERED);
        int phase = state.getValue(WEAVE_PHASE);

        if (!powered) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(WEAVE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 20);
            return;
        }

        if (!PressureAtmosphere.isStable(level, pos)) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(WEAVE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        BlockPos conduitPos = findBestConduit(level, pos);
        int pressure = (conduitPos == null) ? 0 : PressureUtil.getConduitPressureOrState(level, conduitPos);
        if (pressure <= 0) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(WEAVE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        // Loom arms are mechanical: require some rotation to weave.
        int rotation = RotationUtil.getRotationLevel(level, pos);
        if (rotation <= 0) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(WEAVE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        int next = (phase + 1) & 3;
        level.setBlock(pos, state.setValue(WEAVE_PHASE, next), 2);

        // Perform work once per weave cycle.
        if (next == 0 && conduitPos != null) {
            tryWeave(level, pos, conduitPos, rotation);
        }

        int pressureLevel = PressureUtil.pressureToLevel(pressure);
        int tickDelay = (pressureLevel >= 3 || rotation >= 3) ? 6 : 10;
        level.scheduleTick(pos, this, tickDelay);
    }

    private static void tryWeave(ServerLevel level, BlockPos pos, BlockPos conduitPos, int rotation) {
        AABB box = new AABB(pos).inflate(0.6, 0.6, 0.6).move(0, 1.0, 0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());
        if (items.isEmpty()) {
            return;
        }

        // Recipe A: Paleweft -> Coral Fiber.
        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            boolean isPaleweft = stack.is(ModItems.PALEWEFT_GRASS_ITEM.get()) || stack.is(ModItems.PALEWEFT_TALL_GRASS_ITEM.get());
            if (!isPaleweft) {
                continue;
            }

            int pressure = PressureUtil.getConduitPressureOrState(level, conduitPos);
            int pressureLevel = PressureUtil.pressureToLevel(pressure);
            int cost = Math.max(2, 6 + (pressureLevel <= 1 ? 2 : 0) - (rotation / 2));
            if (pressure < cost) {
                return;
            }

            int outCount = stack.is(ModItems.PALEWEFT_TALL_GRASS_ITEM.get()) ? 3 : 2;
            PressureUtil.addPressure(level, conduitPos, -cost);

            stack.shrink(1);
            ItemStack outStack = new ItemStack(ModItems.CORAL_FIBER.get(), outCount);
            if (stack.isEmpty()) {
                itemEntity.setItem(outStack);
            } else {
                itemEntity.setItem(stack);
                ItemEntity out = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), outStack);
                out.setDeltaMovement(0, 0.05, 0);
                level.addFreshEntity(out);
            }

            level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
            return;
        }

        // Recipe B: Attuned Stone + Volatile Resin -> Bio-Ceramic.
        ItemEntity stoneEntity = null;
        ItemEntity resinEntity = null;
        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            if (stoneEntity == null && stack.is(ModItems.ATTUNED_STONE_ITEM.get())) {
                stoneEntity = itemEntity;
            } else if (resinEntity == null && stack.is(ModItems.VOLATILE_RESIN.get())) {
                resinEntity = itemEntity;
            }

            if (stoneEntity != null && resinEntity != null) {
                break;
            }
        }

        if (stoneEntity == null || resinEntity == null) {
            return;
        }

        int pressure = PressureUtil.getConduitPressureOrState(level, conduitPos);
        int pressureLevel = PressureUtil.pressureToLevel(pressure);
        int cost = Math.max(4, 12 + (pressureLevel <= 1 ? 2 : 0) - (rotation / 2));
        if (pressure < cost) {
            return;
        }

        PressureUtil.addPressure(level, conduitPos, -cost);

        ItemStack stoneStack = stoneEntity.getItem();
        ItemStack resinStack = resinEntity.getItem();
        stoneStack.shrink(1);
        resinStack.shrink(1);
        stoneEntity.setItem(stoneStack);
        resinEntity.setItem(resinStack);

        if (stoneStack.isEmpty()) {
            stoneEntity.discard();
        }
        if (resinStack.isEmpty()) {
            resinEntity.discard();
        }

        ItemStack outStack = new ItemStack(ModItems.BIO_CERAMIC.get(), 1);
        ItemEntity out = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, outStack);
        out.setDeltaMovement(0, 0.05, 0);
        level.addFreshEntity(out);

        level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 0.8F, 0.9F);
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

package com.kruemblegard.block;

import com.kruemblegard.blockentity.CrystalInfuserBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;

import com.kruemblegard.registry.ModItems;
import com.kruemblegard.util.BlockEntityTickerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrystalInfuserBlock extends Block implements EntityBlock {
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final IntegerProperty INFUSE_PHASE = IntegerProperty.create("infuse_phase", 0, 3);

    public CrystalInfuserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(INFUSE_PHASE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, INFUSE_PHASE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalInfuserBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.CRYSTAL_INFUSER.get(), CrystalInfuserBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.CRYSTAL_INFUSER.get(), CrystalInfuserBlockEntity::serverTick);
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
        int phase = state.getValue(INFUSE_PHASE);

        if (!powered) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(INFUSE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 20);
            return;
        }

        if (!PressureAtmosphere.isStable(level, pos)) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(INFUSE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        BlockPos conduitPos = findBestConduit(level, pos);
        int pressure = (conduitPos == null) ? 0 : PressureUtil.getConduitPressureOrState(level, conduitPos);
        if (pressure <= 0) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(INFUSE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        int next = (phase + 1) & 3;
        level.setBlock(pos, state.setValue(INFUSE_PHASE, next), 2);

        // Work once per cycle.
        if (next == 0 && conduitPos != null) {
            int cost = 12;
            if (PressureUtil.getConduitPressureOrState(level, conduitPos) >= cost) {
                AABB box = new AABB(pos).inflate(0.5, 0.5, 0.5).move(0, 1.0, 0);
                List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());
                for (ItemEntity itemEntity : items) {
                    ItemStack stack = itemEntity.getItem();
                    if (!stack.is(net.minecraft.world.item.Items.AMETHYST_SHARD)) {
                        continue;
                    }

                    PressureUtil.addPressure(level, conduitPos, -cost);

                    // Consume 1 shard, output 1 attuned shard.
                    stack.shrink(1);
                    ItemStack outStack = new ItemStack(ModItems.ATTUNED_RUNE_SHARD.get(), 1);
                    if (stack.isEmpty()) {
                        itemEntity.setItem(outStack);
                    } else {
                        itemEntity.setItem(stack);
                        ItemEntity out = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), outStack);
                        out.setDeltaMovement(0, 0.05, 0);
                        level.addFreshEntity(out);
                    }

                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.7F, 1.2F);
                    break;
                }
            }
        }

        level.scheduleTick(pos, this, 10);
    }

    private static BlockPos findBestConduit(Level level, BlockPos pos) {
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
}

package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MembranePressBlock extends Block {
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final IntegerProperty PRESS_PHASE = IntegerProperty.create("press_phase", 0, 3);

    public MembranePressBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(PRESS_PHASE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, PRESS_PHASE);
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
        int phase = state.getValue(PRESS_PHASE);

        if (powered) {
            int next = (phase + 1) & 3;
            level.setBlock(pos, state.setValue(PRESS_PHASE, next), 2);

            // Simple "press" behavior (scaffolding): volatile pulp -> volatile resin.
            AABB box = new AABB(pos).inflate(0.5, 0.5, 0.5).move(0, 1.0, 0);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());
            for (ItemEntity itemEntity : items) {
                ItemStack stack = itemEntity.getItem();
                if (stack.is(com.kruemblegard.registry.ModItems.VOLATILE_PULP.get())) {
                    itemEntity.setItem(new ItemStack(com.kruemblegard.registry.ModItems.VOLATILE_RESIN.get(), stack.getCount()));
                    level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.6F, 0.8F);
                }
            }
        } else if (phase != 0) {
            level.setBlock(pos, state.setValue(PRESS_PHASE, 0), 2);
        }

        level.scheduleTick(pos, this, 10);
    }
}

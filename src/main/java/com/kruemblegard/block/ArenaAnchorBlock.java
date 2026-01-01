package com.kruemblegard.block;

import com.kruemblegard.blockentity.ArenaAnchorBlockEntity;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ArenaAnchorBlock extends Block implements EntityBlock {
    public ArenaAnchorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArenaAnchorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }

        if (type != ModBlockEntities.ARENA_ANCHOR.get()) {
            return null;
        }

        return (lvl, pos, st, be) -> {
            if (lvl instanceof ServerLevel serverLevel) {
                ArenaAnchorBlockEntity.serverTick(serverLevel, pos, st, (ArenaAnchorBlockEntity) be);
            }
        };
    }
}

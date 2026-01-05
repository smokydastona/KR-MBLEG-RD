package com.kruemblegard.block;

import java.util.function.Supplier;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import org.jetbrains.annotations.Nullable;

public class StrippableRotatedPillarBlock extends RotatedPillarBlock {

    private final Supplier<? extends Block> stripped;

    public StrippableRotatedPillarBlock(Supplier<? extends Block> stripped, Properties properties) {
        super(properties);
        this.stripped = stripped;
    }

    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction == ToolActions.AXE_STRIP) {
            Block strippedBlock = stripped.get();
            if (strippedBlock != null) {
                return strippedBlock.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }

        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}

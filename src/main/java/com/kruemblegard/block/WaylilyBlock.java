package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class WaylilyBlock extends Block implements SimpleWaterloggedBlock {

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");

    public WaylilyBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(PART, Part.UPPER)
                        .setValue(WATERLOGGED, Boolean.FALSE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        // Lily-pad-like placement: must be placed into water, with at least one more water block below
        // for the tail.
        if (level.getFluidState(pos).getType() != Fluids.WATER) {
            return null;
        }

        if (level.getFluidState(pos.below()).getType() != Fluids.WATER) {
            return null;
        }

        return this.defaultBlockState().setValue(PART, Part.UPPER).setValue(WATERLOGGED, Boolean.FALSE);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (level.isClientSide) {
            return;
        }

        if (state.getValue(PART) == Part.UPPER) {
            ensureLower(level, pos);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);

        if (level.isClientSide) {
            return;
        }

        if (state.getValue(PART) == Part.UPPER) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(this) && belowState.getValue(PART) == Part.LOWER) {
                level.destroyBlock(below, false);
            }
        } else {
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.is(this) && aboveState.getValue(PART) == Part.UPPER) {
                level.destroyBlock(above, true);
            }
        }
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            net.minecraft.core.Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(PART) == Part.UPPER) {
            if (direction == net.minecraft.core.Direction.DOWN) {
                // Keep the tail in sync (and self-destruct if it can't exist).
                if (!neighborState.is(this) || neighborState.getValue(PART) != Part.LOWER) {
                    if (!level.isClientSide() && level instanceof Level) {
                        ensureLower((Level) level, pos);
                        neighborState = level.getBlockState(pos.below());
                    }

                    if (!neighborState.is(this) || neighborState.getValue(PART) != Part.LOWER) {
                        return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
                    }
                }
            }
        } else {
            if (direction == net.minecraft.core.Direction.UP) {
                if (!neighborState.is(this) || neighborState.getValue(PART) != Part.UPPER) {
                    return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
                }
            }

            // Tail should remain waterlogged.
            if (state.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(PART) == Part.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            if (below.is(this) && below.getValue(PART) == Part.LOWER) {
                return true;
            }

            // During placement/worldgen validation, the pad position is still water.
            return level.getFluidState(pos).getType() == Fluids.WATER
                    && level.getFluidState(pos.below()).getType() == Fluids.WATER;
        }

        // LOWER
        BlockState above = level.getBlockState(pos.above());
        if (!above.is(this) || above.getValue(PART) != Part.UPPER) {
            return false;
        }

        return level.getFluidState(pos).getType() == Fluids.WATER;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(PART) == Part.LOWER && state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return super.use(state, level, pos, player, hand, hit);
    }

    private void ensureLower(Level level, BlockPos upperPos) {
        BlockPos lowerPos = upperPos.below();

        if (level.getFluidState(lowerPos).getType() != Fluids.WATER) {
            // Not enough water depth; remove the upper pad.
            level.destroyBlock(upperPos, true);
            return;
        }

        BlockState lowerState = level.getBlockState(lowerPos);
        if (lowerState.is(this) && lowerState.getValue(PART) == Part.LOWER) {
            if (!lowerState.getValue(WATERLOGGED)) {
                level.setBlock(lowerPos, lowerState.setValue(WATERLOGGED, Boolean.TRUE), Block.UPDATE_ALL);
            }
            return;
        }

        level.setBlock(
                lowerPos,
                this.defaultBlockState().setValue(PART, Part.LOWER).setValue(WATERLOGGED, Boolean.TRUE),
                Block.UPDATE_ALL
        );
    }

    public enum Part implements StringRepresentable {
        UPPER("upper"),
        LOWER("lower");

        private final String serializedName;

        Part(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }
    }
}

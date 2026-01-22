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

        // Item placement: lily-pad-like behavior.
        // The upper sits on the surface (replacing the surface water block), and the tail hangs below.
        if (level.getFluidState(pos).getType() != Fluids.WATER) {
            return null;
        }

        if (level.getFluidState(pos.below()).getType() != Fluids.WATER) {
            return null;
        }

        // Must have air above so it's truly on the surface.
        if (!level.getBlockState(pos.above()).canBeReplaced()) {
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
            ensureTails(level, pos);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);

        if (level.isClientSide) {
            return;
        }

        Part part = state.getValue(PART);
        if (part == Part.UPPER) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(this) && belowState.getValue(PART) == Part.LOWER) {
                level.destroyBlock(below, false);
            }

            BlockPos below2 = pos.below(2);
            BlockState below2State = level.getBlockState(below2);
            if (below2State.is(this) && below2State.getValue(PART) == Part.LOWER2) {
                level.destroyBlock(below2, false);
            }
        } else if (part == Part.LOWER) {
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.is(this) && aboveState.getValue(PART) == Part.UPPER) {
                level.destroyBlock(above, true);
            }

            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(this) && belowState.getValue(PART) == Part.LOWER2) {
                level.destroyBlock(below, false);
            }
        } else {
            // LOWER2
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.is(this) && aboveState.getValue(PART) == Part.LOWER) {
                level.destroyBlock(above, false);
            }

            BlockPos above2 = pos.above(2);
            BlockState above2State = level.getBlockState(above2);
            if (above2State.is(this) && above2State.getValue(PART) == Part.UPPER) {
                level.destroyBlock(above2, true);
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
        Part part = state.getValue(PART);

        if (part == Part.UPPER) {
            if (direction == net.minecraft.core.Direction.DOWN) {
                // Keep the tail in sync (and self-destruct if it can't exist).
                if (!neighborState.is(this) || neighborState.getValue(PART) != Part.LOWER) {
                    if (!level.isClientSide() && level instanceof Level) {
                        ensureTails((Level) level, pos);
                        neighborState = level.getBlockState(pos.below());
                    }

                    if (!neighborState.is(this) || neighborState.getValue(PART) != Part.LOWER) {
                        return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
                    }
                }
            }
        } else if (part == Part.LOWER) {
            if (direction == net.minecraft.core.Direction.UP) {
                if (!neighborState.is(this) || neighborState.getValue(PART) != Part.UPPER) {
                    return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
                }
            }

            if (direction == net.minecraft.core.Direction.DOWN) {
                if (!level.isClientSide() && level instanceof Level) {
                    // If there is enough depth below, optionally create/remove the second tail.
                    ensureTails((Level) level, pos.above());
                }
            }

            // Tail should remain waterlogged.
            if (state.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
        } else {
            // LOWER2
            if (direction == net.minecraft.core.Direction.UP) {
                if (!neighborState.is(this) || neighborState.getValue(PART) != Part.LOWER) {
                    return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
                }
            }

            if (state.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Part part = state.getValue(PART);

        if (part == Part.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            if (below.is(this) && below.getValue(PART) == Part.LOWER) {
                return true;
            }

            // Upper survives when there is water directly beneath it.
            return level.getFluidState(pos.below()).getType() == Fluids.WATER;
        }

        if (part == Part.LOWER) {
            BlockState above = level.getBlockState(pos.above());
            if (!above.is(this) || above.getValue(PART) != Part.UPPER) {
                return false;
            }

            return level.getFluidState(pos).getType() == Fluids.WATER;
        }

        // LOWER2
        BlockState above = level.getBlockState(pos.above());
        if (!above.is(this) || above.getValue(PART) != Part.LOWER) {
            return false;
        }

        return level.getFluidState(pos).getType() == Fluids.WATER;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        Part part = state.getValue(PART);
        if ((part == Part.LOWER || part == Part.LOWER2) && state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return super.use(state, level, pos, player, hand, hit);
    }

    private void ensureTails(Level level, BlockPos upperPos) {
        BlockPos lowerPos = upperPos.below();

        if (level.getFluidState(lowerPos).getType() != Fluids.WATER) {
            // Not enough water depth for tails; keep the upper, but remove any existing tails.
            BlockState lowerState = level.getBlockState(lowerPos);
            if (lowerState.is(this) && lowerState.getValue(PART) == Part.LOWER) {
                level.destroyBlock(lowerPos, false);
            }

            BlockPos lower2Pos = upperPos.below(2);
            BlockState lower2State = level.getBlockState(lower2Pos);
            if (lower2State.is(this) && lower2State.getValue(PART) == Part.LOWER2) {
                level.destroyBlock(lower2Pos, false);
            }
            return;
        }

        BlockState lowerState = level.getBlockState(lowerPos);
        if (lowerState.is(this) && lowerState.getValue(PART) == Part.LOWER) {
            if (!lowerState.getValue(WATERLOGGED)) {
                level.setBlock(lowerPos, lowerState.setValue(WATERLOGGED, Boolean.TRUE), Block.UPDATE_ALL);
            }
        } else {
            level.setBlock(
                    lowerPos,
                    this.defaultBlockState().setValue(PART, Part.LOWER).setValue(WATERLOGGED, Boolean.TRUE),
                    Block.UPDATE_ALL
            );
        }

        // Optional second tail segment if there is deeper water.
        BlockPos lower2Pos = upperPos.below(2);
        boolean canHaveLower2 = level.getFluidState(lower2Pos).getType() == Fluids.WATER;
        boolean wantsLower2 = canHaveLower2 && level.random.nextBoolean();

        BlockState lower2State = level.getBlockState(lower2Pos);
        if (wantsLower2) {
            if (lower2State.is(this) && lower2State.getValue(PART) == Part.LOWER2) {
                if (!lower2State.getValue(WATERLOGGED)) {
                    level.setBlock(lower2Pos, lower2State.setValue(WATERLOGGED, Boolean.TRUE), Block.UPDATE_ALL);
                }
            } else {
                level.setBlock(
                        lower2Pos,
                        this.defaultBlockState().setValue(PART, Part.LOWER2).setValue(WATERLOGGED, Boolean.TRUE),
                        Block.UPDATE_ALL
                );
            }
        } else {
            if (lower2State.is(this) && lower2State.getValue(PART) == Part.LOWER2) {
                level.destroyBlock(lower2Pos, false);
            }
        }
    }

    public enum Part implements StringRepresentable {
        UPPER("upper"),
        LOWER("lower"),
        LOWER2("lower2");

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

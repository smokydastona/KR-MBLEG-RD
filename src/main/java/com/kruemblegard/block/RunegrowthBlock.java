package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.world.RunegrowthBonemeal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RunegrowthBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {

    public static final EnumProperty<TempBand> TEMP = EnumProperty.create("temp", TempBand.class);

    public RunegrowthBlock(Properties properties) {
        super(properties);

        // SpreadingSnowyDirtBlock sets SNOWY's default; we extend defaults with our biome-temperature variant.
        this.registerDefaultState(this.defaultBlockState().setValue(TEMP, TempBand.TEMPERATE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState placed = super.getStateForPlacement(context);
        if (placed == null) {
            return null;
        }

        return applyVisualState(placed, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos neighborPos
    ) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
        return applyVisualState(updated, level, currentPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TEMP);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Keep the visual state in sync with biome temperature + nearby snow.
        BlockState desired = applyVisualState(state, level, pos);
        if (desired != state) {
            level.setBlock(pos, desired, 2);
            state = desired;
        }

        // Acts like a grass block. If unable to survive, it falls back to Wayfall dirt.
        if (!canRemainRunegrowth(level, pos)) {
            level.setBlock(pos, ModBlocks.FAULT_DUST.get().defaultBlockState(), 2);
            return;
        }

        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            for (int i = 0; i < 4; i++) {
                BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                BlockState targetState = level.getBlockState(targetPos);
                if (isSpreadableWayfallDirt(targetState) && canRemainRunegrowth(level, targetPos)) {
                    BlockState spreadState = applyVisualState(this.defaultBlockState(), level, targetPos);
                    level.setBlock(targetPos, spreadState, 2);
                }
            }
        }
    }

    private static BlockState applyVisualState(BlockState state, LevelReader level, BlockPos pos) {
        return state
                .setValue(SNOWY, isNearSnow(level, pos))
                .setValue(TEMP, TempBand.fromBaseTemperature(level.getBiome(pos).value()));
    }

    public enum TempBand implements StringRepresentable {
        COLD("cold"),
        TEMPERATE("temperate"),
        WARM("warm"),
        HOT("hot");

        private final String name;

        TempBand(String name) {
            this.name = name;
        }

        public static TempBand fromBaseTemperature(Biome biome) {
            // Base temperature bands tuned to Kruemblegard's current Wayfall biome temperature range.
            // Goal: make all 4 variants reachable with typical mod biome values.
            float t = biome.getBaseTemperature();
            if (t < 0.20F) {
                return COLD;
            }
            if (t < 0.45F) {
                return TEMPERATE;
            }
            if (t < 0.60F) {
                return WARM;
            }
            return HOT;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        RunegrowthBonemeal.bonemeal(level, pos, random);
    }

    private static boolean isSpreadableWayfallDirt(BlockState state) {
        // Explicit whitelist to avoid spreading onto podzol-like or moss blocks.
        return state.is(ModBlocks.FAULT_DUST.get());
    }

    private static boolean canRemainRunegrowth(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState above = level.getBlockState(abovePos);

        // If there's a source fluid above (e.g., water), it should die.
        if (!above.getFluidState().isEmpty() && above.getFluidState().isSource()) {
            return false;
        }

        int light = level.getMaxLocalRawBrightness(abovePos);
        int opacity = above.getLightBlock(level, abovePos);
        return !(light < 4 && opacity > 2);
    }

    private static boolean isNearSnow(LevelReader level, BlockPos pos) {
        // Vanilla grass uses SNOWY when snow is directly above.
        // For Runegrowth we also consider immediately-adjacent snow so it can visually “snow up” when placed near snow.
        if (isSnowLike(level.getBlockState(pos.above()))) {
            return true;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (isSnowLike(level.getBlockState(pos.relative(direction)))) {
                return true;
            }
        }

        return false;
    }

    private static boolean isSnowLike(BlockState state) {
        return state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.POWDER_SNOW);
    }
}

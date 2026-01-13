package com.kruemblegard.block;

import com.kruemblegard.util.PaleweftRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PaleweftCornCropBlock extends CropBlock {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    private static final VoxelShape[] SHAPES = new VoxelShape[] {
            box(3.0, 0.0, 3.0, 13.0, 4.0, 13.0),
            box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0),
            box(3.0, 0.0, 3.0, 13.0, 12.0, 13.0),
            box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0)
    };

    public PaleweftCornCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        Item seeds = PaleweftRegistry.getItem(PaleweftRegistry.PALEWEFT_SEEDS_ID);
        return seeds != null ? seeds : Items.WHEAT_SEEDS;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.is(Blocks.FARMLAND)) {
            return true;
        }

        net.minecraft.world.level.block.Block rubbleTilth = PaleweftRegistry.getBlock(PaleweftRegistry.RUBBLE_TILTH_ID);
        return rubbleTilth != null && state.is(rubbleTilth);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[this.getAge(state)];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

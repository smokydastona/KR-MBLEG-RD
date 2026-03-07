package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PressureKilnBlock extends HorizontalDirectionalBlock {
    public enum KilnMode implements StringRepresentable {
        LOW("low"),
        NORMAL("normal"),
        OVERPRESSURE("overpressure");

        private final String name;

        KilnMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final EnumProperty<KilnMode> KILN_MODE = EnumProperty.create("kiln_mode", KilnMode.class);

    public PressureKilnBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(KILN_MODE, KilnMode.NORMAL));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        int signal = context.getLevel().getBestNeighborSignal(context.getClickedPos());
        boolean powered = signal > 0;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, powered)
                .setValue(KILN_MODE, modeFromSignal(signal));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, KILN_MODE);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && state.getValue(POWERED)) {
            level.scheduleTick(pos, this, 20);
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
                .setValue(KILN_MODE, modeFromSignal(signal));

        if (next != state) {
            level.setBlock(pos, next, Block.UPDATE_CLIENTS);
        }

        if (poweredNow) {
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) {
            return;
        }

        int tries = 0;
        AABB box = new AABB(pos).inflate(0.5, 1.0, 0.5).move(0, 1.0, 0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());
        for (ItemEntity itemEntity : items) {
            if (tries++ > 2) {
                break;
            }

            ItemStack stack = itemEntity.getItem();
            var container = new SimpleContainer(stack);
            var recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);
            if (recipe.isEmpty()) {
                continue;
            }

            ItemStack result = recipe.get().assemble(container, level.registryAccess());
            if (result.isEmpty()) {
                continue;
            }

            itemEntity.setItem(result.copy());
            level.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.6F, 1.1F);
        }

        int tickDelay = switch (state.getValue(KILN_MODE)) {
            case LOW -> 40;
            case NORMAL -> 20;
            case OVERPRESSURE -> 10;
        };
        level.scheduleTick(pos, this, tickDelay);
    }

    private static KilnMode modeFromSignal(int signal) {
        if (signal <= 0) return KilnMode.NORMAL;
        if (signal <= 5) return KilnMode.LOW;
        if (signal <= 10) return KilnMode.NORMAL;
        return KilnMode.OVERPRESSURE;
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

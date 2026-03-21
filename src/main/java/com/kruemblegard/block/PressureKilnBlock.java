package com.kruemblegard.block;

import com.kruemblegard.blockentity.PressureKilnBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.rotationlogic.RotationUtil;
import com.kruemblegard.util.BlockEntityTickerUtil;

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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PressureKilnBlock extends HorizontalDirectionalBlock implements EntityBlock {
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
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PressureKilnBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.PRESSURE_KILN.get(), PressureKilnBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.PRESSURE_KILN.get(), PressureKilnBlockEntity::serverTick);
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

        if (!PressureAtmosphere.isStable(level, pos)) {
            level.scheduleTick(pos, this, 20);
            return;
        }

        // Spec: kiln requires a turbine (rotation) and pressure.
        int rotation = RotationUtil.getRotationLevel(level, pos);
        if (rotation <= 0) {
            level.scheduleTick(pos, this, 20);
            return;
        }

        BlockPos conduitPos = findBestConduit(level, pos);
        if (conduitPos == null) {
            level.scheduleTick(pos, this, 20);
            return;
        }

        int pressure = PressureUtil.getConduitPressureOrState(level, conduitPos);
        if (pressure <= 0) {
            level.scheduleTick(pos, this, 20);
            return;
        }

        if (state.getValue(KILN_MODE) == KilnMode.OVERPRESSURE && pressure >= 85) {
            // Overpressure risk: vent violently (no block damage).
            if (random.nextInt(120) == 0) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.5;
                double z = pos.getZ() + 0.5;
                level.explode(null, x, y, z, 1.2F, Level.ExplosionInteraction.NONE);
                PressureUtil.addPressure(level, conduitPos, -40);
                level.scheduleTick(pos, this, 40);
                return;
            }
        }

        int pressureLevel = PressureUtil.pressureToLevel(pressure);
        int baseCost = switch (state.getValue(KILN_MODE)) {
            case LOW -> 6;
            case NORMAL -> 10;
            case OVERPRESSURE -> 14;
        };
        int costPerSmelt = Math.max(2, baseCost + (5 - rotation) - (pressureLevel / 2));

        int tries = 0;
        AABB box = new AABB(pos).inflate(0.5, 1.0, 0.5).move(0, 1.0, 0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());
        for (ItemEntity itemEntity : items) {
            if (tries++ > 2) {
                break;
            }

            if (PressureUtil.getConduitPressureOrState(level, conduitPos) < costPerSmelt) {
                break;
            }

            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) {
                continue;
            }

            ItemStack one = stack.copyWithCount(1);
            var container = new SimpleContainer(one);
            var recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);
            if (recipe.isEmpty()) {
                continue;
            }

            ItemStack result = recipe.get().assemble(container, level.registryAccess());
            if (result.isEmpty()) {
                continue;
            }

            PressureUtil.addPressure(level, conduitPos, -costPerSmelt);

            // Consume 1 input and emit the recipe output.
            stack.shrink(1);
            if (stack.isEmpty()) {
                itemEntity.setItem(result.copy());
            } else {
                itemEntity.setItem(stack);
                ItemEntity out = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy());
                out.setDeltaMovement(0, 0.05, 0);
                level.addFreshEntity(out);
            }

            level.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.6F, 1.1F);
        }

        int baseDelay = switch (state.getValue(KILN_MODE)) {
            case LOW -> 40;
            case NORMAL -> 20;
            case OVERPRESSURE -> 10;
        };
        int speedBonus = Math.min(8, pressureLevel + rotation);
        int tickDelay = Math.max(6, baseDelay - speedBonus);
        level.scheduleTick(pos, this, tickDelay);
    }

    private static KilnMode modeFromSignal(int signal) {
        if (signal <= 0) return KilnMode.NORMAL;
        if (signal <= 5) return KilnMode.LOW;
        if (signal <= 10) return KilnMode.NORMAL;
        return KilnMode.OVERPRESSURE;
    }

    private static @Nullable BlockPos findBestConduit(Level level, BlockPos pos) {
        BlockPos best = null;
        int bestPressure = 0;

        // Prefer below (expected placement).
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

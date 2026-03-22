package com.kruemblegard.block;

import com.kruemblegard.blockentity.ThermoCondenserBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureFeedback;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.util.BlockEntityTickerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.common.ForgeHooks;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThermoCondenserBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final int ACTIVE_TICK_DELAY = 10;
    private static final int IDLE_TICK_DELAY = 20;
    private static final int BURN_CONSUMPTION_PER_CYCLE = 10;

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public ThermoCondenserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        if (!context.getLevel().isClientSide) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, ACTIVE_TICK_DELAY);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThermoCondenserBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.THERMO_CONDENSER.get(), ThermoCondenserBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.THERMO_CONDENSER.get(), ThermoCondenserBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return PressureFeedback.tryInspect(state, level, pos, player, hand, hit);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        PressureFeedback.animateWorking(state, level, pos, random);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, ACTIVE_TICK_DELAY);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof ThermoCondenserBlockEntity be)) {
            level.scheduleTick(pos, this, IDLE_TICK_DELAY);
            return;
        }

        if (!be.isLit()) {
            feedFuel(level, pos, be);
        }

        if (be.isLit()) {
            BlockPos outputConduit = PressureUtil.resolveInlineConduit(level, pos, state.getValue(FACING));
            if (outputConduit != null) {
                int delta = 2 + be.getHeatLevel();
                PressureUtil.addPressure(level, outputConduit, delta);
            }
            be.consumeBurnTime(BURN_CONSUMPTION_PER_CYCLE);
        }

        boolean lit = be.isLit();
        if (state.getValue(LIT) != lit) {
            state = state.setValue(LIT, lit);
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        }

        level.scheduleTick(pos, this, lit ? ACTIVE_TICK_DELAY : IDLE_TICK_DELAY);
    }

    private static void feedFuel(ServerLevel level, BlockPos pos, ThermoCondenserBlockEntity be) {
        AABB box = new AABB(pos).inflate(0.45D, 0.2D, 0.45D).move(0.0D, 1.0D, 0.0D);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, entity -> entity.isAlive() && !entity.getItem().isEmpty());

        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            int burn = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
            if (burn <= 0) {
                continue;
            }

            stack.shrink(1);
            if (stack.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setItem(stack);
            }

            be.startBurn(scaleFuelBurnTime(burn));
            level.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.7F, 0.9F);
            return;
        }
    }

    private static int scaleFuelBurnTime(int vanillaBurnTime) {
        return Math.max(40, vanillaBurnTime / 8);
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
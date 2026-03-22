package com.kruemblegard.block;

import com.kruemblegard.blockentity.MembranePressBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureFeedback;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MembranePressBlock extends Block implements EntityBlock {
    public enum PressMode implements StringRepresentable {
        NORMAL("normal"),
        PRECISION("precision"),
        BULK("bulk");

        private final String name;

        PressMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final IntegerProperty PRESS_PHASE = IntegerProperty.create("press_phase", 0, 3);
    public static final EnumProperty<PressMode> PRESS_MODE = EnumProperty.create("press_mode", PressMode.class);

    public MembranePressBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(PRESS_PHASE, 0)
                .setValue(PRESS_MODE, PressMode.NORMAL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, PRESS_PHASE, PRESS_MODE);
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        // Empty-hand right click: inspect/status. Shift+right click: cycle mode.
        if (!player.getItemInHand(hand).isEmpty() && !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!player.isShiftKeyDown()) {
            return PressureFeedback.tryInspect(state, level, pos, player, hand, hit);
        }

        PressMode next = switch (state.getValue(PRESS_MODE)) {
            case NORMAL -> PressMode.PRECISION;
            case PRECISION -> PressMode.BULK;
            case BULK -> PressMode.NORMAL;
        };
        level.setBlock(pos, state.setValue(PRESS_MODE, next), Block.UPDATE_CLIENTS);
        return InteractionResult.CONSUME;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        PressureFeedback.animateWorking(state, level, pos, random);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MembranePressBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.MEMBRANE_PRESS.get(), MembranePressBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.MEMBRANE_PRESS.get(), MembranePressBlockEntity::serverTick);
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
        PressMode pressMode = state.getValue(PRESS_MODE);

        if (!powered) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(PRESS_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 20);
            return;
        }

        if (!PressureAtmosphere.isStable(level, pos)) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(PRESS_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        BlockPos conduitPos = findBestConduit(level, pos);
        int pressure = (conduitPos == null) ? 0 : PressureUtil.getConduitPressureOrState(level, conduitPos);
        if (pressure <= 0) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(PRESS_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        int rotation = RotationUtil.getRotationLevel(level, pos);
        int pressureLevel = PressureUtil.pressureToLevel(pressure);
        int costPerPress = Math.max(2, 6 + (pressureLevel <= 1 ? 1 : 0) - (rotation / 2));

        int next = (phase + 1) & 3;
        level.setBlock(pos, state.setValue(PRESS_PHASE, next), 2);

        // Perform work once per cycle.
        if (next == 0 && conduitPos != null) {
            AABB box = new AABB(pos).inflate(0.5, 0.5, 0.5).move(0, 1.0, 0);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());

            int maxOps = switch (pressMode) {
                case NORMAL -> 1;
                case PRECISION -> 1;
                case BULK -> 4;
            };

            int ops = 0;
            for (ItemEntity itemEntity : items) {
                if (ops >= maxOps) {
                    break;
                }

                ItemStack stack = itemEntity.getItem();
                if (!stack.is(com.kruemblegard.registry.ModItems.VOLATILE_PULP.get())) {
                    continue;
                }

                // Precision press: only operate when the stack is exactly 1 ("exact thickness").
                if (pressMode == PressMode.PRECISION && stack.getCount() != 1) {
                    continue;
                }

                if (PressureUtil.getConduitPressureOrState(level, conduitPos) < costPerPress) {
                    break;
                }

                PressureUtil.addPressure(level, conduitPos, -costPerPress);

                // Consume 1 input, output 1 resin.
                stack.shrink(1);
                ItemStack outStack = new ItemStack(com.kruemblegard.registry.ModItems.VOLATILE_RESIN.get(), 1);
                if (stack.isEmpty()) {
                    itemEntity.setItem(outStack);
                } else {
                    itemEntity.setItem(stack);
                    ItemEntity out = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), outStack);
                    out.setDeltaMovement(0, 0.05, 0);
                    level.addFreshEntity(out);
                }

                level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.6F, 0.8F);
                ops++;
            }
        }

        // Faster when well supplied.
        int tickDelay = (pressureLevel >= 3 || rotation >= 3) ? 6 : 10;
        if (pressMode == PressMode.PRECISION) {
            tickDelay += 2;
        } else if (pressMode == PressMode.BULK) {
            tickDelay = Math.max(4, tickDelay - 2);
        }
        level.scheduleTick(pos, this, tickDelay);
    }

    private static BlockPos findBestConduit(Level level, BlockPos pos) {
        BlockPos best = null;
        int bestPressure = 0;

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
}

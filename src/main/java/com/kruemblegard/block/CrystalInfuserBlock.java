package com.kruemblegard.block;

import com.kruemblegard.blockentity.CrystalInfuserBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;

import com.kruemblegard.registry.ModItems;
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

public class CrystalInfuserBlock extends Block implements EntityBlock {
    public enum InfuserMode implements StringRepresentable {
        NORMAL("normal"),
        MULTI("multi"),
        DEEP("deep");

        private final String name;

        InfuserMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final IntegerProperty INFUSE_PHASE = IntegerProperty.create("infuse_phase", 0, 3);
    public static final EnumProperty<InfuserMode> INFUSER_MODE = EnumProperty.create("infuser_mode", InfuserMode.class);

    public CrystalInfuserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(INFUSE_PHASE, 0)
                .setValue(INFUSER_MODE, InfuserMode.NORMAL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, INFUSE_PHASE, INFUSER_MODE);
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
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        InfuserMode next = switch (state.getValue(INFUSER_MODE)) {
            case NORMAL -> InfuserMode.MULTI;
            case MULTI -> InfuserMode.DEEP;
            case DEEP -> InfuserMode.NORMAL;
        };
        level.setBlock(pos, state.setValue(INFUSER_MODE, next), Block.UPDATE_CLIENTS);
        return InteractionResult.CONSUME;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalInfuserBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.CRYSTAL_INFUSER.get(), CrystalInfuserBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.CRYSTAL_INFUSER.get(), CrystalInfuserBlockEntity::serverTick);
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
        int phase = state.getValue(INFUSE_PHASE);
        InfuserMode infuserMode = state.getValue(INFUSER_MODE);

        if (!powered) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(INFUSE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 20);
            return;
        }

        if (!PressureAtmosphere.isStable(level, pos)) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(INFUSE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        BlockPos conduitPos = findBestConduit(level, pos);
        int pressure = (conduitPos == null) ? 0 : PressureUtil.getConduitPressureOrState(level, conduitPos);
        if (pressure <= 0) {
            if (phase != 0) {
                level.setBlock(pos, state.setValue(INFUSE_PHASE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        int next = (phase + 1) & 3;
        level.setBlock(pos, state.setValue(INFUSE_PHASE, next), 2);

        // Work once per cycle.
        if (next == 0 && conduitPos != null) {
            int costPerOp = switch (infuserMode) {
                case NORMAL -> 12;
                case MULTI -> 12;
                case DEEP -> 30;
            };
            int minPressure = switch (infuserMode) {
                case NORMAL -> costPerOp;
                case MULTI -> costPerOp;
                case DEEP -> 40;
            };
            int maxOps = switch (infuserMode) {
                case NORMAL -> 1;
                case MULTI -> 2;
                case DEEP -> 1;
            };

            if (PressureUtil.getConduitPressureOrState(level, conduitPos) >= minPressure) {
                AABB box = new AABB(pos).inflate(0.5, 0.5, 0.5).move(0, 1.0, 0);
                List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());
                int ops = 0;

                for (ItemEntity itemEntity : items) {
                    if (ops >= maxOps) {
                        break;
                    }

                    ItemStack stack = itemEntity.getItem();

                    boolean matchesInput = switch (infuserMode) {
                        case NORMAL, MULTI -> stack.is(net.minecraft.world.item.Items.AMETHYST_SHARD);
                        case DEEP -> stack.is(ModItems.FAULT_SHARD.get());
                    };
                    if (!matchesInput) {
                        continue;
                    }

                    if (PressureUtil.getConduitPressureOrState(level, conduitPos) < costPerOp) {
                        break;
                    }
                    PressureUtil.addPressure(level, conduitPos, -costPerOp);

                    ItemStack outStack = switch (infuserMode) {
                        case NORMAL, MULTI -> new ItemStack(ModItems.ATTUNED_RUNE_SHARD.get(), 1);
                        case DEEP -> new ItemStack(ModItems.RUNIC_SCRAP.get(), 1);
                    };

                    // Consume 1 input, output 1 infused product.
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        itemEntity.setItem(outStack);
                    } else {
                        itemEntity.setItem(stack);
                        ItemEntity out = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), outStack);
                        out.setDeltaMovement(0, 0.05, 0);
                        level.addFreshEntity(out);
                    }

                    float pitch = (infuserMode == InfuserMode.DEEP) ? 0.8F : 1.2F;
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.7F, pitch);
                    ops++;
                }
            }
        }

        int tickDelay = 10;
        if (infuserMode == InfuserMode.DEEP) {
            tickDelay = 12;
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

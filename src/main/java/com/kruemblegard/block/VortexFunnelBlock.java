package com.kruemblegard.block;

import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VortexFunnelBlock extends HorizontalDirectionalBlock {
    public enum VortexMode implements StringRepresentable {
        GENTLE("gentle"),
        NORMAL("normal"),
        HARSH("harsh");

        private final String name;

        VortexMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final EnumProperty<VortexMode> VORTEX_MODE = EnumProperty.create("vortex_mode", VortexMode.class);
    public static final BooleanProperty DIRECTIONAL = BooleanProperty.create("directional");

    public VortexFunnelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(VORTEX_MODE, VortexMode.NORMAL)
                .setValue(DIRECTIONAL, false));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        boolean directional = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, powered)
                .setValue(DIRECTIONAL, directional)
                .setValue(VORTEX_MODE, VortexMode.NORMAL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, VORTEX_MODE, DIRECTIONAL);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && state.getValue(POWERED)) {
            level.scheduleTick(pos, this, 5);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (level.isClientSide) {
            return;
        }

        boolean poweredNow = level.hasNeighborSignal(pos);
        boolean wasPowered = state.getValue(POWERED);
        if (poweredNow != wasPowered) {
            level.setBlock(pos, state.setValue(POWERED, poweredNow), Block.UPDATE_CLIENTS);
        }

        if (poweredNow && !wasPowered) {
            level.scheduleTick(pos, this, 5);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) {
            return;
        }

        if (!PressureAtmosphere.isStable(level, pos)) {
            level.scheduleTick(pos, this, 10);
            return;
        }

        VortexMode mode = state.getValue(VORTEX_MODE);
        Direction facing = state.getValue(FACING);
        boolean directional = state.getValue(DIRECTIONAL);

        BlockPos conduitPos = findBestAdjacentConduit(level, pos);
        if (conduitPos == null) {
            level.scheduleTick(pos, this, 10);
            return;
        }

        int availablePressure = PressureUtil.getConduitPressureOrState(level, conduitPos);
        int costPerTick = switch (mode) {
            case GENTLE -> 1;
            case NORMAL -> 2;
            case HARSH -> 3;
        };

        if (availablePressure < costPerTick) {
            level.scheduleTick(pos, this, 10);
            return;
        }

        int pLevel = PressureUtil.pressureToLevel(availablePressure);
        double pressureScale = pLevel / 5.0;

        double range = 2.5 + (2.0 * pressureScale);
        Vec3 center = Vec3.atCenterOf(pos);
        AABB box = new AABB(pos).inflate(range);

        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());
        for (ItemEntity entity : itemEntities) {
            Vec3 delta = center.subtract(entity.position());
            double dist = Math.max(0.2, delta.length());

            if (directional) {
                // Only pull from the "front" side.
                Vec3 toEntity = entity.position().subtract(center);
                Vec3 front = new Vec3(facing.getStepX(), 0, facing.getStepZ());
                if (front.lengthSqr() > 0 && toEntity.dot(front) < 0) {
                    continue;
                }
            }

            double strength = switch (mode) {
                case GENTLE -> 0.04;
                case NORMAL -> 0.07;
                case HARSH -> 0.10;
            };

            strength *= (0.35 + (0.65 * pressureScale));

            Vec3 pull = delta.scale(strength / dist);
            entity.setDeltaMovement(entity.getDeltaMovement().add(pull.x, pull.y * 0.02, pull.z));
            entity.hurtMarked = true;

        }

        List<LivingEntity> livingEntities = level.getEntitiesOfClass(
            LivingEntity.class,
            box,
            e -> e.isAlive() && !e.isSpectator() && !(e instanceof Player)
        );
        for (LivingEntity entity : livingEntities) {
            Vec3 delta = center.subtract(entity.position());
            double dist = Math.max(0.2, delta.length());

            if (directional) {
                // Only pull from the "front" side.
                Vec3 toEntity = entity.position().subtract(center);
                Vec3 front = new Vec3(facing.getStepX(), 0, facing.getStepZ());
                if (front.lengthSqr() > 0 && toEntity.dot(front) < 0) {
                    continue;
                }
            }

            double strength = switch (mode) {
                case GENTLE -> 0.04;
                case NORMAL -> 0.07;
                case HARSH -> 0.10;
            };

            strength *= (0.35 + (0.65 * pressureScale));

            Vec3 pull = delta.scale(strength / dist);
            entity.setDeltaMovement(entity.getDeltaMovement().add(pull.x, pull.y * 0.02, pull.z));
            entity.hurtMarked = true;

            if (mode == VortexMode.HARSH && random.nextInt(20) == 0) {
                entity.hurt(level.damageSources().magic(), 1.0F);
            }
        }

        PressureUtil.addPressure(level, conduitPos, -costPerTick);

        level.scheduleTick(pos, this, 5);
    }

    private static @Nullable BlockPos findBestAdjacentConduit(Level level, BlockPos pos) {
        // Prefer below, then direct neighbors.
        BlockPos below = pos.below();
        if (PressureUtil.getConduitEntity(level, below) != null) {
            return below;
        }

        for (Direction dir : Direction.values()) {
            BlockPos p = pos.relative(dir);
            if (PressureUtil.getConduitEntity(level, p) != null) {
                return p;
            }
        }

        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            boolean next = !state.getValue(DIRECTIONAL);
            level.setBlock(pos, state.setValue(DIRECTIONAL, next), Block.UPDATE_CLIENTS);
            return InteractionResult.CONSUME;
        }

        VortexMode next = switch (state.getValue(VORTEX_MODE)) {
            case GENTLE -> VortexMode.NORMAL;
            case NORMAL -> VortexMode.HARSH;
            case HARSH -> VortexMode.GENTLE;
        };
        level.setBlock(pos, state.setValue(VORTEX_MODE, next), Block.UPDATE_CLIENTS);
        return InteractionResult.CONSUME;
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

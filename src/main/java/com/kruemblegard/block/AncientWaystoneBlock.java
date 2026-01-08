package com.kruemblegard.block;

import com.kruemblegard.compat.WaystonesCompat;
import com.kruemblegard.config.ModConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class AncientWaystoneBlock extends HorizontalDirectionalBlock {
    public AncientWaystoneBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Client: return SUCCESS so the hand animates immediately; actual work happens on server.
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!ModConfig.WAYSTONE_ENABLED.get()) {
            player.displayClientMessage(Component.translatable("message.kruemblegard.waystones_disabled"), true);
            return InteractionResult.SUCCESS;
        }

        if (!WaystonesCompat.isWaystonesLoaded()) {
            player.displayClientMessage(Component.translatable("message.kruemblegard.waystones_missing"), true);
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            InteractionResult result = WaystonesCompat.tryConvertAndDelegateUse(level, pos, serverPlayer, hand, hit);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }

        player.displayClientMessage(Component.translatable("message.kruemblegard.waystones_open_failed"), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // This is called client-side for visual ambience.
        if (random.nextInt(3) != 0) return;

        level.addParticle(
                ParticleTypes.END_ROD,
                pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.25,
                pos.getY() + 1.05 + random.nextDouble() * 0.35,
                pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.25,
                0.0,
                0.01,
                0.0
        );
    }
}

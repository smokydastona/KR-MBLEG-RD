package com.kruemblegard.block;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.compat.WaystonesCompat;

import net.blay09.mods.waystones.block.WaystoneBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AncientWaystoneBlock extends WaystoneBlock {
    public AncientWaystoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.CONSUME;
        }

        if (!WaystonesCompat.isWaystonesLoaded()) {
            serverPlayer.displayClientMessage(Component.translatable("message.kruemblegard.waystones_missing"), true);
            return InteractionResult.CONSUME;
        }

        // If Waystones doesn't recognize our custom Waystone block for its BlockEntityType valid blocks,
        // the BlockEntity can be missing and interaction will misbehave.
        if (level.getBlockEntity(pos) == null) {
            InteractionResult delegated = WaystonesCompat.tryConvertAndDelegateUse(
                    level,
                    pos,
                    serverPlayer,
                    hand,
                    hit,
                    new ResourceLocation(Kruemblegard.MOD_ID, "ancient_waystone")
            );

            if (delegated != InteractionResult.PASS) {
                return delegated;
            }

            serverPlayer.displayClientMessage(Component.translatable("message.kruemblegard.waystones_open_failed"), true);
            return InteractionResult.CONSUME;
        }

        return super.use(state, level, pos, player, hand, hit);
    }
}

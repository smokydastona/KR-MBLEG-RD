package com.kruemblegard.block;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

public class WayfallPortalBlock extends Block {
    public WayfallPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, Level level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }

        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (serverPlayer.isOnPortalCooldown()) {
            return;
        }

        ServerLevel target = getTargetLevel(serverPlayer);
        if (target == null) {
            return;
        }

        serverPlayer.setPortalCooldown();
        teleport(serverPlayer, target);
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

        ServerLevel target = getTargetLevel(serverPlayer);
        if (target == null) {
            Kruemblegard.LOGGER.warn("Wayfall portal used, but target dimension was not available.");
            return InteractionResult.CONSUME;
        }

        teleport(serverPlayer, target);
        return InteractionResult.CONSUME;
    }

    private static ServerLevel getTargetLevel(ServerPlayer player) {
        if (player.getServer() == null) {
            return null;
        }

        if (player.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return null;
        }

        return player.getServer().getLevel(ModWorldgenKeys.Levels.WAYFALL);
    }

    private static void teleport(ServerPlayer player, ServerLevel target) {
        BlockPos spawn = target.getSharedSpawnPos();
        BlockPos top = target.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn);
        Vec3 dest = new Vec3(top.getX() + 0.5D, top.getY() + 1.0D, top.getZ() + 0.5D);

        player.changeDimension(target, new ITeleporter() {
            @Override
            public net.minecraft.world.level.portal.PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld,
                                                                           java.util.function.Function<ServerLevel, net.minecraft.world.level.portal.PortalInfo> defaultPortalInfo) {
                return new net.minecraft.world.level.portal.PortalInfo(dest, Vec3.ZERO, entity.getYRot(), entity.getXRot());
            }
        });
    }
}

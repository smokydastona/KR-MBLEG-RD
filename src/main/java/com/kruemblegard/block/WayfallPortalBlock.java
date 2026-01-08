package com.kruemblegard.block;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
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
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
        BlockPos landing = findLandingOrCreatePlatform(target, spawn);
        Vec3 dest = new Vec3(landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D);

        player.changeDimension(target, new ITeleporter() {
            @Override
            public net.minecraft.world.level.portal.PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld,
                                                                           java.util.function.Function<ServerLevel, net.minecraft.world.level.portal.PortalInfo> defaultPortalInfo) {
                return new net.minecraft.world.level.portal.PortalInfo(dest, Vec3.ZERO, entity.getYRot(), entity.getXRot());
            }
        });
    }

    private static BlockPos findLandingOrCreatePlatform(ServerLevel level, BlockPos spawn) {
        // 1) Try to find nearby solid ground using heightmap sampling.
        BlockPos best = null;
        int bestDistSq = Integer.MAX_VALUE;

        // Keep it cheap: sample a grid around spawn.
        // Wayfall islands can be sparse; a small radius can easily miss everything.
        final int searchRadius = 512;
        final int step = 16;

        for (int dx = -searchRadius; dx <= searchRadius; dx += step) {
            for (int dz = -searchRadius; dz <= searchRadius; dz += step) {
                BlockPos probe = spawn.offset(dx, 0, dz);
                BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, probe);

                // Heightmap can still resolve to the bottom when the column is empty.
                if (top.getY() <= level.getMinBuildHeight() + 1) {
                    continue;
                }

                // Ensure there's actually something to stand on.
                if (level.getBlockState(top).isAir()) {
                    continue;
                }

                BlockPos feet = top.above();
                if (!level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()) {
                    continue;
                }
                if (!level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()) {
                    continue;
                }

                int distSq = dx * dx + dz * dz;
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                    best = feet;
                }
            }
        }

        if (best != null) {
            return best;
        }

        // 2) Nothing nearby: create a small platform at a safe Y.
        int platformY = Math.min(level.getMaxBuildHeight() - 8, Math.max(level.getMinBuildHeight() + 8, 160));
        BlockPos center = new BlockPos(spawn.getX(), platformY, spawn.getZ());

        BlockState block = ModBlocks.FRACTURED_WAYROCK.get().defaultBlockState();

        // Ensure chunk is loaded before writing blocks.
        level.getChunk(center);

        int half = 4;
        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                BlockPos p = center.offset(dx, 0, dz);
                level.setBlockAndUpdate(p, block);
                level.setBlockAndUpdate(p.below(), block);
            }
        }

        return center.above();
    }
}

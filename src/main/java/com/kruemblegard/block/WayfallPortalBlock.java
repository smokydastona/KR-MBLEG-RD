package com.kruemblegard.block;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
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

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Match vanilla portal constraints.
        if (entity.isPassenger() || entity.isVehicle() || !entity.canChangeDimensions()) {
            return;
        }

        if (entity.isOnPortalCooldown()) {
            return;
        }

        ServerLevel target = getTargetLevel(serverLevel);
        if (target == null) {
            return;
        }

        entity.setPortalCooldown();
        teleport(entity, target);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.CONSUME;
        }

        if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
            return InteractionResult.CONSUME;
        }

        ServerLevel target = getTargetLevel(serverLevel);
        if (target == null) {
            Kruemblegard.LOGGER.warn("Wayfall portal used, but target dimension was not available.");
            return InteractionResult.CONSUME;
        }

        teleport(serverPlayer, target);
        return InteractionResult.CONSUME;
    }

    private static ServerLevel getTargetLevel(ServerLevel fromLevel) {
        if (fromLevel.getServer() == null) {
            return null;
        }

        // Portal is one-way: never re-trigger inside Wayfall.
        if (fromLevel.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return null;
        }

        return fromLevel.getServer().getLevel(ModWorldgenKeys.Levels.WAYFALL);
    }

    private static void teleport(Entity entity, ServerLevel target) {
        entity.changeDimension(target, new ITeleporter() {
            @Override
            public net.minecraft.world.level.portal.PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld,
                                                                           java.util.function.Function<ServerLevel, net.minecraft.world.level.portal.PortalInfo> defaultPortalInfo) {
                BlockPos spawn = destWorld.getSharedSpawnPos();
                BlockPos landing = findLandingOrCreatePlatform(destWorld, spawn, entity);
                Vec3 dest = new Vec3(landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D);
                return new net.minecraft.world.level.portal.PortalInfo(dest, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
            }
        });
    }

    private static BlockPos findLandingOrCreatePlatform(ServerLevel level, BlockPos spawn, Entity entity) {
        // 1) Try to find nearby solid ground using heightmap sampling.
        BlockPos best = null;
        int bestDistSq = Integer.MAX_VALUE;

        // Two-pass scan: a finer local sweep first, then a coarser wide sweep.
        best = findBestLanding(level, spawn, entity, 128, 8, bestDistSq);
        if (best != null) {
            return best;
        }
        best = findBestLanding(level, spawn, entity, 512, 16, bestDistSq);
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

        // Clear headroom at the landing point to avoid suffocating in pre-existing blocks.
        BlockPos landing = center.above();
        level.setBlockAndUpdate(landing, Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(landing.above(), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(landing.above(2), Blocks.AIR.defaultBlockState());
        return landing;
    }

    private static BlockPos findBestLanding(ServerLevel level, BlockPos spawn, Entity entity, int searchRadius, int step, int currentBestDistSq) {
        BlockPos best = null;
        int bestDistSq = currentBestDistSq;

        for (int dx = -searchRadius; dx <= searchRadius; dx += step) {
            for (int dz = -searchRadius; dz <= searchRadius; dz += step) {
                BlockPos probe = spawn.offset(dx, 0, dz);
                // getHeightmapPos returns the first air block above the top-most motion-blocking block.
                // Treat this as the entity's feet position, and validate the block below as ground.
                BlockPos feet = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, probe);

                // Heightmap can still resolve to the bottom when the column is empty.
                if (feet.getY() <= level.getMinBuildHeight() + 1) {
                    continue;
                }

                BlockPos ground = feet.below();
                if (ground.getY() <= level.getMinBuildHeight()) {
                    continue;
                }

                BlockState groundState = level.getBlockState(ground);
                if (groundState.isAir()) {
                    continue;
                }

                // Ensure the entity can actually stand here.
                if (!groundState.isFaceSturdy(level, ground, Direction.UP)) {
                    continue;
                }

                // Quick block-local headroom check.
                if (!level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()) {
                    continue;
                }
                if (!level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()) {
                    continue;
                }

                // Full collision check (accounts for entity width and nearby walls).
                Vec3 dest = new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D);
                var aabb = entity.getDimensions(entity.getPose()).makeBoundingBox(dest);
                if (!level.noCollision(entity, aabb)) {
                    continue;
                }

                int distSq = dx * dx + dz * dz;
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                    best = feet;
                }
            }
        }

        return best;
    }
}

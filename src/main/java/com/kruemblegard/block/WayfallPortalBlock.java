package com.kruemblegard.block;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.world.WayfallOriginMonument;
import com.kruemblegard.world.WayfallSpawnPlatform;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw,
                                      java.util.function.Function<Boolean, Entity> repositionEntity) {
                // End-portal style: always enter Wayfall at its spawn landing, never at the source coords.
                Entity placed = repositionEntity.apply(false);

                BlockPos landing = WayfallSpawnPlatform.ensureSpawnLanding(destWorld);
                WayfallOriginMonument.ensurePlaced(destWorld);
                double x = landing.getX() + 0.5D;
                double y = landing.getY();
                double z = landing.getZ() + 0.5D;

                placed.fallDistance = 0;
                placed.setDeltaMovement(Vec3.ZERO);
                placed.moveTo(x, y, z, placed.getYRot(), placed.getXRot());
                return placed;
            }
        });
    }
}

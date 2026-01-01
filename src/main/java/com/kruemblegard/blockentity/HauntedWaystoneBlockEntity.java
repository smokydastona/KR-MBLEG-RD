package com.kruemblegard.blockentity;

import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.init.ModCriteria;
import com.kruemblegard.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class HauntedWaystoneBlockEntity extends BlockEntity {

    private boolean activated;

    public HauntedWaystoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAUNTED_WAYSTONE.get(), pos, state);
    }

    public void activate(Player player) {
        if (activated) return;
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        activated = true;
        setChanged();

        if (player instanceof ServerPlayer sp) {
            ModCriteria.HAUNTED_WAYSTONE_CLICKED.trigger(sp);
        }

        serverLevel.playSound(
                null,
                worldPosition,
            ModSounds.KRUEMBLEGARD_RISE.get(),
                SoundSource.HOSTILE,
                2.0F,
                1.0F
        );

        // Remove the haunted waystone. The anchor placed below becomes the persistent controller.
        serverLevel.removeBlock(worldPosition, false);

        BlockPos anchorPos = worldPosition.below();

        serverLevel.setBlock(anchorPos, ModBlocks.ARENA_ANCHOR.get().defaultBlockState(), 3);
        if (!serverLevel.getBlockState(anchorPos).is(ModBlocks.ARENA_ANCHOR.get())) {
            // Fallback: if we couldn't place below (weird terrain/mod interference), place at the original position.
            anchorPos = worldPosition;
            serverLevel.setBlock(anchorPos, ModBlocks.ARENA_ANCHOR.get().defaultBlockState(), 3);
        }

        BlockEntity maybeAnchorBe = serverLevel.getBlockEntity(anchorPos);
        if (maybeAnchorBe == null) {
            // Ensure the BE is created immediately so we can start the state machine this tick.
            maybeAnchorBe = serverLevel.getChunkAt(anchorPos)
                    .getBlockEntity(anchorPos, LevelChunk.EntityCreationType.IMMEDIATE);
        }

        if (maybeAnchorBe instanceof ArenaAnchorBlockEntity anchor) {
            anchor.start(worldPosition, player.getUUID());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Activated", activated);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        activated = tag.getBoolean("Activated");
    }
}

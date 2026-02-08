package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LostPillagerShipCombatEvents {
    private LostPillagerShipCombatEvents() {}

    private static final ResourceLocation LOST_PILLAGER_SHIP_ID = ModWorldgenKeys.Structures.LOST_PILLAGER_SHIP.location();

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractArrow arrow)) {
            return;
        }

        Entity owner = arrow.getOwner();
        if (!(owner instanceof Pillager)) {
            return;
        }

        if (!isInsideLostPillagerShip(level, arrow.blockPosition())) {
            return;
        }

        // Pillagers use crossbows by default (no Flame enchant). To guarantee "fire damage arrows",
        // ignite their fired projectiles when they're coming from this structure.
        arrow.setSecondsOnFire(6);
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Structure ship = structureRegistry.get(LOST_PILLAGER_SHIP_ID);
        if (ship == null) {
            return;
        }

        ChunkPos chunkPos = chunk.getPos();
        StructureManager structureManager = level.structureManager();

        List<StructureStart> starts = structureManager.startsForStructure(chunkPos, s -> s == ship);
        if (starts.isEmpty()) {
            return;
        }

        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMaxX = chunkPos.getMaxBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int chunkMaxZ = chunkPos.getMaxBlockZ();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (StructureStart start : starts) {
            if (!start.isValid()) {
                continue;
            }

            for (StructurePiece piece : start.getPieces()) {
                BoundingBox bbox = piece.getBoundingBox();
                if (bbox == null) {
                    continue;
                }

                int minX = Math.max(bbox.minX(), chunkMinX);
                int maxX = Math.min(bbox.maxX(), chunkMaxX);
                int minZ = Math.max(bbox.minZ(), chunkMinZ);
                int maxZ = Math.min(bbox.maxZ(), chunkMaxZ);
                if (minX > maxX || minZ > maxZ) {
                    continue;
                }

                int minY = Math.max(bbox.minY(), level.getMinBuildHeight());
                int maxY = Math.min(bbox.maxY(), level.getMaxBuildHeight() - 1);
                if (minY > maxY) {
                    continue;
                }

                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        for (int x = minX; x <= maxX; x++) {
                            mutablePos.set(x, y, z);

                            if (!chunk.getBlockState(mutablePos).is(Blocks.SPAWNER)) {
                                continue;
                            }

                            BlockEntity be = chunk.getBlockEntity(mutablePos);
                            if (be instanceof SpawnerBlockEntity spawner) {
                                fixSpawnerIfNeeded(level, spawner);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void fixSpawnerIfNeeded(ServerLevel level, SpawnerBlockEntity spawner) {
        CompoundTag tag = spawner.saveWithFullMetadata();

        CompoundTag spawnData = tag.getCompound("SpawnData");
        CompoundTag entity = spawnData.getCompound("entity");
        boolean missingId = !entity.contains("id", Tag.TAG_STRING) || entity.getString("id").isEmpty();
        if (!missingId) {
            return;
        }

        entity.putString("id", "minecraft:vindicator");

        ListTag handItems = new ListTag();
        handItems.add(itemStackTag("minecraft:iron_axe"));
        handItems.add(new CompoundTag());
        entity.put("HandItems", handItems);

        spawnData.put("entity", entity);
        tag.put("SpawnData", spawnData);

        ListTag potentials = new ListTag();
        potentials.add(spawnPotentialTag(4, "minecraft:iron_axe"));
        potentials.add(spawnPotentialTag(1, "minecraft:diamond_axe"));
        tag.put("SpawnPotentials", potentials);

        spawner.load(tag);
        spawner.setChanged();
        level.sendBlockUpdated(spawner.getBlockPos(), spawner.getBlockState(), spawner.getBlockState(), 3);
    }

    private static CompoundTag spawnPotentialTag(int weight, String axeId) {
        CompoundTag entry = new CompoundTag();
        entry.putInt("weight", weight);

        CompoundTag data = new CompoundTag();
        CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:vindicator");

        ListTag handItems = new ListTag();
        handItems.add(itemStackTag(axeId));
        handItems.add(new CompoundTag());
        entity.put("HandItems", handItems);

        data.put("entity", entity);
        entry.put("data", data);
        return entry;
    }

    private static CompoundTag itemStackTag(String itemId) {
        CompoundTag item = new CompoundTag();
        item.putString("id", itemId);
        item.putByte("Count", (byte) 1);
        return item;
    }

    private static boolean isInsideLostPillagerShip(ServerLevel level, net.minecraft.core.BlockPos pos) {
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Structure ship = structureRegistry.get(LOST_PILLAGER_SHIP_ID);
        if (ship == null) {
            return false;
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        StructureManager structureManager = level.structureManager();

        List<StructureStart> starts = structureManager.startsForStructure(chunkPos, s -> s == ship);
        if (starts.isEmpty()) {
            return false;
        }

        for (StructureStart start : starts) {
            if (!start.isValid()) {
                continue;
            }

            for (StructurePiece piece : start.getPieces()) {
                BoundingBox bbox = piece.getBoundingBox();
                if (bbox != null && bbox.isInside(pos)) {
                    return true;
                }
            }
        }

        return false;
    }
}

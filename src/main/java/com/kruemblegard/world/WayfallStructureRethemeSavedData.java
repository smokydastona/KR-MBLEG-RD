package com.kruemblegard.world;

import com.kruemblegard.Kruemblegard;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Tracks one-time retheming passes applied to vanilla structures in Wayfall.
 *
 * <p>Keyed by structure-id + start-chunk coordinate; each entry stores the set of
 * chunk positions already processed for that structure instance.</p>
 */
public final class WayfallStructureRethemeSavedData extends SavedData {
    private static final String DATA_NAME = Kruemblegard.MOD_ID + "_wayfall_structure_retheme";

    private static final String TAG_ENTRIES = "entries";
    private static final String TAG_KEY = "key";
    private static final String TAG_CHUNKS = "chunks";

    private final Map<String, LongSet> processedChunksByStructureStart = new HashMap<>();

    private WayfallStructureRethemeSavedData() {}

    public static WayfallStructureRethemeSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WayfallStructureRethemeSavedData::load,
                WayfallStructureRethemeSavedData::new,
                DATA_NAME
        );
    }

    public static WayfallStructureRethemeSavedData load(CompoundTag tag) {
        WayfallStructureRethemeSavedData data = new WayfallStructureRethemeSavedData();
        ListTag entries = tag.getList(TAG_ENTRIES, Tag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            String key = entry.getString(TAG_KEY);
            if (key == null || key.isBlank()) {
                continue;
            }

            long[] chunks = entry.getLongArray(TAG_CHUNKS);
            LongSet set = new LongOpenHashSet(chunks);
            data.processedChunksByStructureStart.put(key, set);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag entries = new ListTag();

        for (Map.Entry<String, LongSet> e : processedChunksByStructureStart.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putString(TAG_KEY, e.getKey());
            entry.put(TAG_CHUNKS, new LongArrayTag(e.getValue().toLongArray()));
            entries.add(entry);
        }

        tag.put(TAG_ENTRIES, entries);
        return tag;
    }

    public boolean isChunkProcessed(String structureStartKey, long chunkPosLong) {
        LongSet set = processedChunksByStructureStart.get(structureStartKey);
        return set != null && set.contains(chunkPosLong);
    }

    public void markChunkProcessed(String structureStartKey, long chunkPosLong) {
        LongSet set = processedChunksByStructureStart.computeIfAbsent(structureStartKey, k -> new LongOpenHashSet());
        if (set.add(chunkPosLong)) {
            setDirty();
        }
    }
}

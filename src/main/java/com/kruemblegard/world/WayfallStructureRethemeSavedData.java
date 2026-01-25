package com.kruemblegard.world;

import com.kruemblegard.Kruemblegard;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.LinkedHashMap;
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

    /**
     * Hard caps to prevent this SavedData from growing without bound when a player uses /tp to
     * rapidly explore huge areas (which can otherwise make autosaves and "Save and Quit" hang).
     */
    private static final int MAX_STRUCTURE_START_KEYS = 1024;
    private static final int MAX_CHUNKS_TRACKED_PER_STRUCTURE = 16;

    private static final String TAG_ENTRIES = "entries";
    private static final String TAG_KEY = "key";
    private static final String TAG_FULLY_PROCESSED = "fully_processed";
    private static final String TAG_CHUNKS = "chunks";

    private static final class Entry {
        private boolean fullyProcessed;
        private final LongSet processedChunks;

        private Entry(boolean fullyProcessed, LongSet processedChunks) {
            this.fullyProcessed = fullyProcessed;
            this.processedChunks = processedChunks;
        }
    }

    // accessOrder=true gives us a simple LRU so we can evict old entries.
    private final Map<String, Entry> processedByStructureStart = new LinkedHashMap<>(64, 0.75f, true);

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

            boolean fullyProcessed = entry.getBoolean(TAG_FULLY_PROCESSED);
            long[] chunks = entry.getLongArray(TAG_CHUNKS);
            LongSet set = new LongOpenHashSet(chunks);
            data.processedByStructureStart.put(key, new Entry(fullyProcessed, set));
        }

        data.evictIfNeeded();

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag entries = new ListTag();

        for (Map.Entry<String, Entry> e : processedByStructureStart.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putString(TAG_KEY, e.getKey());
            Entry v = e.getValue();
            entry.putBoolean(TAG_FULLY_PROCESSED, v.fullyProcessed);
            entry.put(TAG_CHUNKS, new LongArrayTag(v.processedChunks.toLongArray()));
            entries.add(entry);
        }

        tag.put(TAG_ENTRIES, entries);
        return tag;
    }

    public boolean isChunkProcessed(String structureStartKey, long chunkPosLong) {
        Entry entry = processedByStructureStart.get(structureStartKey);
        if (entry == null) {
            return false;
        }

        if (entry.fullyProcessed) {
            return true;
        }

        return entry.processedChunks.contains(chunkPosLong);
    }

    public void markChunkProcessed(String structureStartKey, long chunkPosLong) {
        Entry entry = processedByStructureStart.get(structureStartKey);
        if (entry == null) {
            entry = new Entry(false, new LongOpenHashSet());
            processedByStructureStart.put(structureStartKey, entry);
        }

        if (entry.fullyProcessed) {
            return;
        }

        if (entry.processedChunks.add(chunkPosLong)) {
            // If a structure touches lots of chunks, stop tracking per-chunk and treat it as fully processed.
            if (entry.processedChunks.size() >= MAX_CHUNKS_TRACKED_PER_STRUCTURE) {
                entry.fullyProcessed = true;
                entry.processedChunks.clear();
            }

            evictIfNeeded();
            setDirty();
        }
    }

    private void evictIfNeeded() {
        if (processedByStructureStart.size() <= MAX_STRUCTURE_START_KEYS) {
            return;
        }

        // Evict least-recently-used entries to keep the SavedData bounded.
        var it = processedByStructureStart.entrySet().iterator();
        while (processedByStructureStart.size() > MAX_STRUCTURE_START_KEYS && it.hasNext()) {
            it.next();
            it.remove();
        }
    }
}

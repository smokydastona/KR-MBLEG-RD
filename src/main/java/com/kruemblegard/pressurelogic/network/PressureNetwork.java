package com.kruemblegard.pressurelogic.network;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

/**
 * Server-side pressure network representation.
 *
 * Notes:
 * - This is intentionally lightweight and conservative.
 * - Nodes are tracked by BlockPos#asLong.
 * - The manager is the source of truth for node->network membership; this class
 *   treats nodes as best-effort and skips stale nodes.
 */
final class PressureNetwork {
    final long id;
    final long anchorPosLong;

    final LongOpenHashSet nodes;
    final LongArrayList nodeList;

    private int cursor;

    PressureNetwork(long id, long anchorPosLong, LongOpenHashSet nodes) {
        this.id = id;
        this.anchorPosLong = anchorPosLong;
        this.nodes = nodes;

        this.nodeList = new LongArrayList(nodes.size());
        for (var it = nodes.iterator(); it.hasNext();) {
            this.nodeList.add(it.nextLong());
        }

        this.cursor = 0;
    }

    boolean isEmpty() {
        return nodes.isEmpty();
    }

    void removeNode(long posLong) {
        nodes.remove(posLong);
    }

    long nextNode() {
        if (nodeList.isEmpty()) {
            return 0L;
        }
        int idx = cursor++;
        if (idx < 0) {
            idx = 0;
            cursor = 1;
        }
        return nodeList.getLong(idx % nodeList.size());
    }
}

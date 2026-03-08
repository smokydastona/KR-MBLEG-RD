package com.kruemblegard.pressurelogic.network;

import net.minecraft.core.BlockPos;

/**
 * Read-only snapshot of a conduit network. Intended for debugging/inspection.
 */
public record PressureNetworkSnapshot(
    long networkId,
    BlockPos anchor,
    int nodeCount,
    int minPressure,
    int maxPressure,
    int avgPressure
) {}

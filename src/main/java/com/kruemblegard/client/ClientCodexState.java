package com.kruemblegard.client;

/**
 * Client-only mirror of small bits of server-truth player state.
 *
 * <p>This keeps client logic from guessing when server-owned state matters.</p>
 */
public final class ClientCodexState {
    private ClientCodexState() {}

    private static volatile boolean givenCrumblingCodex = false;

    public static boolean hasBeenGivenCrumblingCodex() {
        return givenCrumblingCodex;
    }

    public static void setGivenCrumblingCodex(boolean value) {
        givenCrumblingCodex = value;
    }
}

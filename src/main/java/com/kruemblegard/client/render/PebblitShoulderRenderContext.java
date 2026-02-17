package com.kruemblegard.client.render;

/**
 * Thread-local render flag used to avoid double-rendering Pebblits.
 *
 * Pebblits that are perched as a passenger on a player are rendered via a player layer,
 * and skipped during normal entity rendering.
 */
public final class PebblitShoulderRenderContext {

    private static final ThreadLocal<Boolean> IN_SHOULDER_LAYER = ThreadLocal.withInitial(() -> false);

    private PebblitShoulderRenderContext() {}

    public static boolean isInShoulderLayer() {
        return IN_SHOULDER_LAYER.get();
    }

    public static void withInShoulderLayer(Runnable runnable) {
        boolean previous = IN_SHOULDER_LAYER.get();
        IN_SHOULDER_LAYER.set(true);
        try {
            runnable.run();
        } finally {
            IN_SHOULDER_LAYER.set(previous);
        }
    }
}

package com.kruemblegard.pressurelogic;

/**
 * Per-side interaction mode for a pressure conduit.
 *
 * This currently gates capability exposure and whether external callers are allowed to write pressure.
 * It does not (yet) enforce directional conduit-to-conduit diffusion.
 */
public enum PressurePortMode {
    INPUT,
    OUTPUT,
    BOTH,
    DISABLED;

    public PressurePortMode next() {
        return switch (this) {
            case INPUT -> OUTPUT;
            case OUTPUT -> BOTH;
            case BOTH -> DISABLED;
            case DISABLED -> INPUT;
        };
    }
}

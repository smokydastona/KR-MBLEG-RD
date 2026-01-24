package com.kruemblegard.world;

import com.kruemblegard.worldgen.ModWorldgenKeys;
import net.minecraft.server.level.ServerLevel;
public final class WayfallOriginMonument {
    private WayfallOriginMonument() {}

    /**
     * Origin monument feature removed.
     *
     * This method is intentionally a no-op.
     */
    public static void ensurePlaced(ServerLevel wayfall) {
        // Keep the dimension check to ensure this is never used as a generic hook.
        if (wayfall != null && wayfall.dimension() != ModWorldgenKeys.Levels.WAYFALL) {
            return;
        }
    }
}

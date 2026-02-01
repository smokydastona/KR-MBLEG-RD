package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Central registration point for custom WoodTypes.
 *
 * WoodType is used by signs/hanging signs for correct rendering + sounds.
 */
public final class ModWoodTypes {
    private ModWoodTypes() {}

    public static final WoodType WAYROOT = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":wayroot", BlockSetType.OAK));
    public static final WoodType FALLBARK = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":fallbark", BlockSetType.OAK));
    public static final WoodType ECHOWOOD = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":echowood", BlockSetType.OAK));
    public static final WoodType CAIRN_TREE = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":cairn_tree", BlockSetType.OAK));
    public static final WoodType WAYGLASS = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":wayglass", BlockSetType.OAK));
    public static final WoodType SPLINTERSPORE = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":splinterspore", BlockSetType.OAK));
    public static final WoodType HOLLOWWAY_TREE = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":hollowway_tree", BlockSetType.OAK));
    public static final WoodType DRIFTWILLOW = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":driftwillow", BlockSetType.OAK));
    public static final WoodType MONUMENT_OAK = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":monument_oak", BlockSetType.OAK));
    public static final WoodType WAYTORCH_TREE = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":waytorch_tree", BlockSetType.OAK));
    public static final WoodType FAULTWOOD = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":faultwood", BlockSetType.OAK));

    public static final WoodType ASHBLOOM = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":ashbloom", BlockSetType.OAK));
    public static final WoodType GLIMMERPINE = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":glimmerpine", BlockSetType.OAK));
    public static final WoodType DRIFTWOOD = WoodType.register(new WoodType(Kruemblegard.MOD_ID + ":driftwood", BlockSetType.OAK));

    /**
     * No-op method used to force classloading during common setup.
     */
    public static void register() {
        // Intentionally empty.
    }
}

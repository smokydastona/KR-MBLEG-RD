package com.kruemblegard.entity.vehicle;

import com.kruemblegard.Kruemblegard;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.minecraftforge.registries.ForgeRegistries;

public enum KruemblegardBoatType {
    WAYROOT("wayroot"),
    FALLBARK("fallbark"),
    ECHOWOOD("echowood"),
    CAIRN_TREE("cairn_tree"),
    WAYGLASS("wayglass"),
    SPLINTERSPORE("splinterspore"),
    HOLLOWWAY_TREE("hollowway_tree"),
    DRIFTWILLOW("driftwillow"),
    MONUMENT_OAK("monument_oak"),
    WAYTORCH_TREE("waytorch_tree"),
    FAULTWOOD("faultwood"),
    ASHBLOOM("ashbloom"),
    GLIMMERPINE("glimmerpine"),
    DRIFTWOOD("driftwood");

    private final String id;

    KruemblegardBoatType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public ResourceLocation boatTexture() {
        // Safe default: render like vanilla oak until per-wood textures exist.
        return new ResourceLocation("minecraft", "textures/entity/boat/oak.png");
    }

    public ResourceLocation chestBoatTexture() {
        // Safe default: render like vanilla oak until per-wood textures exist.
        return new ResourceLocation("minecraft", "textures/entity/chest_boat/oak.png");
    }

    public Item boatItem() {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(Kruemblegard.MOD_ID, id + "_boat"));
    }

    public Item chestBoatItem() {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(Kruemblegard.MOD_ID, id + "_chest_boat"));
    }

    public static KruemblegardBoatType fromId(String id) {
        for (var type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return WAYROOT;
    }
}

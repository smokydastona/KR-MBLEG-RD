package com.kruemblegard.compat;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;

import net.blay09.mods.waystones.api.WaystoneStyle;
import net.blay09.mods.waystones.api.WaystoneStyles;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.Field;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class WaystonesVariantInit {
    private static final String WAYSTONES_MODID = "waystones";

    private WaystonesVariantInit() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (!ModList.get().isLoaded(WAYSTONES_MODID)) {
                return;
            }

            registerAncientWaystoneStyle();
            extendWaystonesBlockEntityType();
        });
    }

    private static void registerAncientWaystoneStyle() {
        try {
            WaystoneStyles.register(new WaystoneStyle(new ResourceLocation(Kruemblegard.MOD_ID, "ancient_waystone")));
        } catch (Throwable t) {
            Kruemblegard.LOGGER.warn("Failed to register Waystones style for kruemblegard:ancient_waystone", t);
        }
    }

    private static void extendWaystonesBlockEntityType() {
        try {
            Block ancientWaystone = ModBlocks.ANCIENT_WAYSTONE.get();
            BlockEntityType<?> waystoneType = ModBlockEntities.waystone.get();

            if (tryAddValidBlock(waystoneType, ancientWaystone)) {
                return;
            }

            Kruemblegard.LOGGER.warn("Failed to extend Waystones waystone BlockEntityType valid blocks (field not found)");
        } catch (Throwable t) {
            Kruemblegard.LOGGER.warn("Failed to extend Waystones waystone BlockEntityType valid blocks", t);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean tryAddValidBlock(BlockEntityType<?> type, Block block) throws IllegalAccessException {
        for (Field field : BlockEntityType.class.getDeclaredFields()) {
            if (!Set.class.isAssignableFrom(field.getType())) {
                continue;
            }

            field.setAccessible(true);
            Object value = field.get(type);
            if (!(value instanceof Set set)) {
                continue;
            }

            // Heuristic: the only Set on BlockEntityType should be the valid blocks set.
            if (!set.isEmpty()) {
                Object first = set.iterator().next();
                if (!(first instanceof Block)) {
                    continue;
                }
            }

            return ((Set) set).add(block);
        }

        return false;
    }
}

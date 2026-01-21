package com.kruemblegard.compat;

import com.kruemblegard.Kruemblegard;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class WaystonesCompat {
    private static final String WAYSTONES_MODID = "waystones";

    private WaystonesCompat() {
    }

    public static boolean isWaystonesLoaded() {
        return ModList.get().isLoaded(WAYSTONES_MODID);
    }

    /**
     * Attempts to convert the block at {@code pos} into a real Waystones waystone and then delegates the same use-click
     * to the new blockstate so the player gets the normal Waystones activation/selection menu.
     */
    public static InteractionResult tryConvertAndDelegateUse(Level level, BlockPos pos, net.minecraft.server.level.ServerPlayer player, InteractionHand hand, BlockHitResult hit) {
        return tryConvertAndDelegateUse(level, pos, player, hand, hit, null);
    }

    /**
     * Same as {@link #tryConvertAndDelegateUse(Level, BlockPos, net.minecraft.server.level.ServerPlayer, InteractionHand, BlockHitResult)}
     * but attempts to use a specific WaystoneStyle (by id) if available.
     */
    public static InteractionResult tryConvertAndDelegateUse(Level level, BlockPos pos, net.minecraft.server.level.ServerPlayer player,
                                                            InteractionHand hand, BlockHitResult hit, ResourceLocation styleId) {
        try {
            Class<?> waystonesApiClass = Class.forName("net.blay09.mods.waystones.api.WaystonesAPI");
            Class<?> waystoneStylesClass = Class.forName("net.blay09.mods.waystones.api.WaystoneStyles");
            Class<?> waystoneStyleClass = Class.forName("net.blay09.mods.waystones.api.WaystoneStyle");

            Object style = null;
            if (styleId != null) {
                style = tryResolveStyle(waystoneStylesClass, styleId);
            }

            if (style == null) {
                Field defaultStyleField = waystoneStylesClass.getField("DEFAULT");
                style = defaultStyleField.get(null);
            }

            Method placeWaystoneMethod = waystonesApiClass.getMethod("placeWaystone", Level.class, BlockPos.class, waystoneStyleClass);
            placeWaystoneMethod.invoke(null, level, pos, style);

            BlockState newState = level.getBlockState(pos);
            return newState.use(level, player, hand, hit);
        } catch (Throwable t) {
            Kruemblegard.LOGGER.warn("Waystones integration failed for Ancient Waystone at {}", pos, t);
            return InteractionResult.PASS;
        }
    }

    private static Object tryResolveStyle(Class<?> waystoneStylesClass, ResourceLocation styleId) {
        try {
            for (String methodName : new String[]{"get", "getStyle", "getById", "byId", "getValue"}) {
                try {
                    Method m = waystoneStylesClass.getMethod(methodName, ResourceLocation.class);
                    Object value = m.invoke(null, styleId);
                    if (value != null) {
                        return value;
                    }
                } catch (NoSuchMethodException ignored) {
                    // Try next candidate
                }
            }
        } catch (Throwable ignored) {
            // Fall back to DEFAULT.
        }

        return null;
    }
}

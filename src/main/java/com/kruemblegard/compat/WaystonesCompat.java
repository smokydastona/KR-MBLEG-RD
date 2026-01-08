package com.kruemblegard.compat;

import com.kruemblegard.Kruemblegard;

import net.minecraft.core.BlockPos;
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
        try {
            Class<?> waystonesApiClass = Class.forName("net.blay09.mods.waystones.api.WaystonesAPI");
            Class<?> waystoneStylesClass = Class.forName("net.blay09.mods.waystones.api.WaystoneStyles");
            Class<?> waystoneStyleClass = Class.forName("net.blay09.mods.waystones.api.WaystoneStyle");

            Field defaultStyleField = waystoneStylesClass.getField("DEFAULT");
            Object defaultStyle = defaultStyleField.get(null);

            Method placeWaystoneMethod = waystonesApiClass.getMethod("placeWaystone", Level.class, BlockPos.class, waystoneStyleClass);
            placeWaystoneMethod.invoke(null, level, pos, defaultStyle);

            BlockState newState = level.getBlockState(pos);
            return newState.use(level, player, hand, hit);
        } catch (Throwable t) {
            Kruemblegard.LOGGER.warn("Waystones integration failed for Ancient Waystone at {}", pos, t);
            return InteractionResult.PASS;
        }
    }
}

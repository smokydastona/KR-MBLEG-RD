package com.kruemblegard.registry;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LeavesBlock;

import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Method;

public final class ModFlammability {
    private ModFlammability() {
    }

    public static void registerFlammables() {
        if (!(Blocks.FIRE instanceof FireBlock fire)) {
            return;
        }

        final int leavesEncouragement = 30;
        final int leavesFlammability = 60;

        final int logsEncouragement = 5;
        final int logsFlammability = 5;

        final int woodEncouragement = 5;
        final int woodFlammability = 20;

        final int saplingEncouragement = 60;
        final int saplingFlammability = 100;

        Method setFlammable;
        try {
            setFlammable = FireBlock.class.getDeclaredMethod("setFlammable", Block.class, int.class, int.class);
            setFlammable.setAccessible(true);
        } catch (ReflectiveOperationException ex) {
            return;
        }

        for (RegistryObject<Block> entry : ModBlocks.BLOCKS.getEntries()) {
            ResourceLocation id = entry.getId();
            if (id == null || !id.getNamespace().equals("kruemblegard")) {
                continue;
            }

            String path = id.getPath();
            Block block = entry.get();

            try {
                if (block instanceof LeavesBlock || path.endsWith("_leaves")) {
                    setFlammable.invoke(fire, block, leavesEncouragement, leavesFlammability);
                } else if (path.endsWith("_log") || path.endsWith("_wood")) {
                    setFlammable.invoke(fire, block, logsEncouragement, logsFlammability);
                } else if (path.endsWith("_sapling")) {
                    setFlammable.invoke(fire, block, saplingEncouragement, saplingFlammability);
                } else if (
                        path.endsWith("_planks")
                                || path.endsWith("_slab")
                                || path.endsWith("_stairs")
                                || path.endsWith("_fence")
                                || path.endsWith("_fence_gate")
                                || path.endsWith("_door")
                                || path.endsWith("_trapdoor")
                                || path.endsWith("_pressure_plate")
                                || path.endsWith("_button")
                                || path.endsWith("_sign")
                                || path.endsWith("_wall_sign")
                                || path.endsWith("_hanging_sign")
                                || path.endsWith("_wall_hanging_sign")
                ) {
                    setFlammable.invoke(fire, block, woodEncouragement, woodFlammability);
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }
}

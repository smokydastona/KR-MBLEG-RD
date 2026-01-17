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
                    setFlammable.invoke(fire, block, 30, 60);
                } else if (path.endsWith("_log") || path.endsWith("_wood")) {
                    setFlammable.invoke(fire, block, 5, 5);
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }
}

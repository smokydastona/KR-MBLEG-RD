package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModItems;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.EntityBlock;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kruemblegard.MODID);

    private enum Category {
        BUILDING,
        NATURAL,
        MISC
    }

    public static final RegistryObject<CreativeModeTab> BUILDING_BLOCKS = TABS.register(
            "kruemblegard",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.kruemblegard"))
                    .icon(() -> new ItemStack(ModItems.ATTUNED_STONE_ITEM.get()))
                    .displayItems((parameters, output) -> displayCategory(output, Category.BUILDING))
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> NATURAL_BLOCKS = TABS.register(
            "kruemblegard_natural_blocks",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.kruemblegard.natural_blocks"))
                    .icon(() -> new ItemStack(ModItems.ASHMOSS_ITEM.get()))
                    .displayItems((parameters, output) -> displayCategory(output, Category.NATURAL))
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> MISCELLANEOUS = TABS.register(
            "kruemblegard_miscellaneous",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.kruemblegard.miscellaneous"))
                    .icon(() -> new ItemStack(ModItems.ANCIENT_WAYSTONE_ITEM.get()))
                    .displayItems((parameters, output) -> displayCategory(output, Category.MISC))
                    .build()
    );

    private static void displayCategory(CreativeModeTab.Output output, Category category) {
        var entries = new ArrayList<ItemEntry>();

        // NOTE: Some items (e.g., auto-generated BlockItems) are registered outside ModItems.ITEMS,
        // so we scan the actual item registry by namespace.
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id == null || !Kruemblegard.MODID.equals(id.getNamespace())) {
                continue;
            }

            // Internal/placeholder items that shouldn't appear in creative.
            if ("menu_tab".equals(id.getPath())) {
                continue;
            }

            // Internal block-items for head/body plants should not appear in the creative menu.
            // Pyrokelp is represented by the head item; the body (pyrokelp_plant) is not meant to be obtainable.
            if ("pyrokelp_plant".equals(id.getPath())) {
                continue;
            }

            Category resolved = categorize(item, id);
            if (resolved != category) {
                continue;
            }

            entries.add(new ItemEntry(id, item));
        }

        entries.sort(ItemEntry.COMPARATOR);
        for (var entry : entries) {
            output.accept(entry.item);
        }
    }

    private static Category categorize(Item item, ResourceLocation id) {
        if (!(item instanceof BlockItem blockItem)) {
            return Category.MISC;
        }

        Block block = blockItem.getBlock();
        String path = id.getPath();

        if (isMiscBlock(block, path)) {
            return Category.MISC;
        }

        if (isBuildingBlock(path)) {
            return Category.BUILDING;
        }

        if (isNaturalBlock(block, path)) {
            return Category.NATURAL;
        }

        // Default: if it's a block-item and not clearly natural/functional, treat it as a builder-placed block.
        return Category.BUILDING;
    }

    private static boolean isMiscBlock(Block block, String path) {
        // Block-entity blocks are almost always interactive/functional.
        if (block instanceof EntityBlock) {
            return true;
        }

        // Name-based overrides for special logic blocks.
        return path.contains("portal")
                || path.contains("waystone")
                || path.contains("controller")
                || path.contains("altar")
                || path.contains("spawner");
    }

    private static boolean isBuildingBlock(String path) {
        // Structural variants builders place intentionally.
        if (endsWithAny(path,
                "_stairs",
                "_slab",
                "_wall",
                "_fence",
                "_fence_gate",
                "_door",
                "_trapdoor",
                "_pressure_plate",
                "_button",
                "_sign",
                "_hanging_sign")) {
            return true;
        }

        // Refined/crafted materials.
        return path.contains("planks")
                || path.contains("bricks")
                || path.contains("tiles")
                || path.contains("polished")
                || path.contains("cut_")
                || path.contains("chiseled")
                || path.contains("smooth");
    }

    private static boolean isNaturalBlock(Block block, String path) {
        // Wood + foliage.
        if ((block instanceof RotatedPillarBlock)
                && (path.endsWith("_log") || path.endsWith("_wood") || path.startsWith("stripped_"))) {
            return true;
        }
        if (block instanceof LeavesBlock || path.endsWith("_leaves")) {
            return true;
        }
        if (path.endsWith("_sapling")) {
            return true;
        }

        // Plants/fungi/vines.
        if (block instanceof BushBlock
                || block instanceof VineBlock
                || block instanceof GrowingPlantHeadBlock) {
            return true;
        }
        if (path.contains("fungus")
                || path.contains("mushroom")
                || path.contains("vine")
                || path.contains("kelp")
                || path.contains("seagrass")) {
            return true;
        }

        // Terrain-like materials.
        return endsWithAny(path, "_ore")
                || path.contains("dirt")
                || path.contains("grass")
                || path.contains("moss")
                || path.contains("sand")
                || path.contains("gravel")
                || path.contains("loam")
                || path.contains("dust")
                || path.contains("rubble")
                || path.contains("scarstone")
                || path.contains("wayrock")
                || path.contains("crushstone")
                || path.contains("ashfall_");
    }

    private static boolean endsWithAny(String value, String... suffixes) {
        for (String suffix : suffixes) {
            if (value.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private static final class ItemEntry {
        private static final List<String> VARIANT_SUFFIX_ORDER = List.of(
                "",              // base block/item
                "_slab",
                "_stairs",
                "_wall",
                "_fence",
                "_fence_gate",
                "_door",
                "_trapdoor",
                "_pressure_plate",
                "_button",
                "_sign",
                "_hanging_sign"
        );

        private static final Comparator<ItemEntry> COMPARATOR = (a, b) -> {
            var ak = a.sortKey();
            var bk = b.sortKey();
            int cmp = ak.family.compareTo(bk.family);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(ak.strippedPenalty, bk.strippedPenalty);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(ak.variantOrder, bk.variantOrder);
            if (cmp != 0) return cmp;
            return a.id.getPath().compareTo(b.id.getPath());
        };

        private final ResourceLocation id;
        private final Item item;

        private ItemEntry(ResourceLocation id, Item item) {
            this.id = id;
            this.item = item;
        }

        private SortKey sortKey() {
            String path = id.getPath();

            int strippedPenalty = 0;
            if (path.startsWith("stripped_")) {
                strippedPenalty = 1;
                path = path.substring("stripped_".length());
            }

            String family = path;
            int variantOrder = 0;

            for (int i = 1; i < VARIANT_SUFFIX_ORDER.size(); i++) {
                String suffix = VARIANT_SUFFIX_ORDER.get(i);
                if (path.endsWith(suffix)) {
                    family = path.substring(0, path.length() - suffix.length());
                    variantOrder = i;
                    break;
                }
            }

            return new SortKey(family, strippedPenalty, variantOrder);
        }

        private record SortKey(String family, int strippedPenalty, int variantOrder) {}
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}

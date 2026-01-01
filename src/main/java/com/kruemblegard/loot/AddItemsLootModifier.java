package com.kruemblegard.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class AddItemsLootModifier extends LootModifier {

    public static final Supplier<MapCodec<AddItemsLootModifier>> CODEC = Suppliers.memoize(
        () -> RecordCodecBuilder.mapCodec(
            instance -> codecStart(instance)
                .and(ItemStack.CODEC.listOf().fieldOf("items").forGetter(m -> m.items))
                .apply(instance, AddItemsLootModifier::new)
        )
    );

    private final List<ItemStack> items;

    public AddItemsLootModifier(LootItemCondition[] conditionsIn, List<ItemStack> items) {
        super(conditionsIn);
        this.items = items;
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(LootTable table, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                generatedLoot.add(stack.copy());
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}

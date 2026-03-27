package com.kruemblegard.loot;

import com.google.common.base.Suppliers;
import com.kruemblegard.registry.ModEnchantments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public final class AddTelekinesisLootModifier extends LootModifier {
    public static final Supplier<Codec<AddTelekinesisLootModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .and(Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(m -> m.chance))
                    .and(BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("items").forGetter(m -> m.items))
                    .apply(instance, AddTelekinesisLootModifier::new))
    );

    private final float chance;
    private final List<Item> items;

    public AddTelekinesisLootModifier(LootItemCondition[] conditionsIn, float chance, List<Item> items) {
        super(conditionsIn);
        this.chance = chance;
        this.items = items;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (items.isEmpty() || context.getRandom().nextFloat() > chance) {
            return generatedLoot;
        }

        Item item = items.get(context.getRandom().nextInt(items.size()));
        generatedLoot.add(createTelekinesisStack(item));
        return generatedLoot;
    }

    private static ItemStack createTelekinesisStack(Item item) {
        if (item == Items.BOOK || item == Items.ENCHANTED_BOOK) {
            return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ModEnchantments.TELEKINESIS.get(), 1));
        }

        ItemStack stack = new ItemStack(item);
        stack.enchant(ModEnchantments.TELEKINESIS.get(), 1);
        return stack;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
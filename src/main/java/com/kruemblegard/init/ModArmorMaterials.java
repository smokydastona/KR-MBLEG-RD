package com.kruemblegard.init;

import com.kruemblegard.registry.ModItems;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public final class ModArmorMaterials {
    private static final int[] DIAMOND_DURABILITY_MULTIPLIERS = new int[] {13, 15, 16, 11};
    private static final int[] SCARSTEEL_DEFENSE_VALUES = new int[] {3, 6, 8, 3};

    private ModArmorMaterials() {}

    public static final ArmorMaterial SCARSTEEL = new ArmorMaterial() {
        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return DIAMOND_DURABILITY_MULTIPLIERS[indexFor(type)] * 33;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return SCARSTEEL_DEFENSE_VALUES[indexFor(type)];
        }

        @Override
        public int getEnchantmentValue() {
            return 10;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_DIAMOND;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.SCARSTEEL_INGOT.get());
        }

        @Override
        public String getName() {
            return "diamond";
        }

        @Override
        public float getToughness() {
            return 2.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }

        private int indexFor(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 0;
                case CHESTPLATE -> 1;
                case LEGGINGS -> 2;
                case BOOTS -> 3;
            };
        }
    };
}
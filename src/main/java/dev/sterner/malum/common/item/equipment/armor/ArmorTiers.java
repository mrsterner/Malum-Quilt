package dev.sterner.malum.common.item.equipment.armor;

import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;


public class ArmorTiers
{
    public enum ArmorTierEnum implements ArmorMaterial {
        SPIRIT_HUNTER("malum:spirit_hunter", 16, new int[]{1, 3, 4, 2}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, MalumObjects.SPIRIT_FABRIC, 0),
        SOUL_STAINED_STEEL("malum:soul_stained_steel", 22, new int[]{2, 6, 7, 3}, 11,  SoundEvents.ITEM_ARMOR_EQUIP_IRON, MalumObjects.SOUL_STAINED_STEEL_INGOT, 2),
        SOUL_STAINED_STRONGHOLD("malum:soul_stained_stronghold", 36, new int[]{4, 7, 9, 5}, 13,  SoundEvents.ITEM_ARMOR_EQUIP_IRON, MalumObjects.SOUL_STAINED_STEEL_INGOT, 5);
        private final String name;
        private final int durabilityMultiplier;
        private final int[] damageReduction;
        private final int enchantability;
        private final SoundEvent equipSound;
        private final Item repairItem;
        private final float toughness;
        private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

        ArmorTierEnum(String name, int durabilityMultiplier, int[] damageReduction, int enchantability, SoundEvent equipSound, Item repairItem, float toughness)
        {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.damageReduction = damageReduction;
            this.enchantability = enchantability;
            this.equipSound = equipSound;
            this.repairItem = repairItem;
            this.toughness = toughness;
        }

        @Override
        public int getDurability(ArmorItem.Type type) {
            return durabilityMultiplier * MAX_DAMAGE_ARRAY[type.getEquipmentSlot().getEntitySlotId()];
        }

        @Override
        public int getProtection(ArmorItem.Type type) {
            return damageReduction[type.getEquipmentSlot().getEntitySlotId()];
        }

        @Override
        public int getEnchantability() {
            return enchantability;
        }

        @Override
        public SoundEvent getEquipSound() {
            return equipSound;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(repairItem);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public float getToughness() {
            return toughness;
        }

        @Override
        public float getKnockbackResistance() {
            if (this.equals(SOUL_STAINED_STRONGHOLD)) {
                return 0.1f;
            }
            return 0;
        }
    }
}

package dev.sterner.malum.common.item;


import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class ItemTiers {
	public enum ItemTierEnum implements ToolMaterial {
		SOUL_STAINED_STEEL(1250, 7.5f, 2.5f, 3, 16, Items.IRON_INGOT),
		TYRVING(850, 8f, 1f, 3, 12, Items.IRON_INGOT);//TODO
		private final int maxUses;
		private final float efficiency;
		private final float attackDamage;
		private final int harvestLevel;
		private final int enchantability;
		private final Item repairItem;

		ItemTierEnum(int maxUses, float efficiency, float attackDamage, int harvestLevel, int enchantability, Item repairItem)
		{
			this.maxUses = maxUses;
			this.efficiency = efficiency;
			this.attackDamage = attackDamage;
			this.harvestLevel = harvestLevel;
			this.enchantability = enchantability;
			this.repairItem = repairItem;
		}

		@Override
		public int getDurability() {
			return maxUses;
		}

		@Override
		public float getMiningSpeedMultiplier() {
			return efficiency;
		}

		@Override
		public float getAttackDamage() {
			return attackDamage;
		}

		@Override
		public int getMiningLevel() {
			return harvestLevel;
		}

		@Override
		public int getEnchantability() {
			return enchantability;
		}


		@Override
		public Ingredient getRepairIngredient()
		{
			return Ingredient.ofItems(repairItem);
		}
	}
}

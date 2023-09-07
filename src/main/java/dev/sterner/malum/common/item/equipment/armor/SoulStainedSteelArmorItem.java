package dev.sterner.malum.common.item.equipment.armor;

import com.google.common.collect.ImmutableMultimap;
import dev.sterner.lodestone.systems.item.LodestoneArmorItem;
import dev.sterner.malum.common.registry.MalumAttributeRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;

import java.util.UUID;

public class SoulStainedSteelArmorItem extends LodestoneArmorItem {
    public SoulStainedSteelArmorItem(ArmorItem.Type type, Settings builder) {
        super(ArmorTiers.ArmorTierEnum.SOUL_STAINED_STEEL, type, builder);
    }

	@Override
	public ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> createExtraAttributes(Type type) {
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = new ImmutableMultimap.Builder<>();
		UUID uuid = MODIFIERS.get(type);
		builder.put(MalumAttributeRegistry.SOUL_WARD_CAP, new EntityAttributeModifier(uuid, "Soul Ward Cap", 3f, EntityAttributeModifier.Operation.ADDITION));
		return builder;
	}
}

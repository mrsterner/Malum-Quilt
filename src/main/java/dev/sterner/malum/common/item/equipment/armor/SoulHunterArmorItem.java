package dev.sterner.malum.common.item.equipment.armor;

import com.google.common.collect.ImmutableMultimap;
import dev.sterner.lodestone.setup.LodestoneAttributeRegistry;
import dev.sterner.lodestone.systems.item.LodestoneArmorItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;

import java.util.UUID;

public class SoulHunterArmorItem extends LodestoneArmorItem {
    public SoulHunterArmorItem(ArmorItem.Type type, Settings builder) {
        super(ArmorTiers.ArmorTierEnum.SPIRIT_HUNTER, type, builder);
    }

    @Override
    public ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> createExtraAttributes(ArmorItem.Type type) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = new ImmutableMultimap.Builder<>();
        UUID uuid = MODIFIERS.get(type);
        builder.put(LodestoneAttributeRegistry.MAGIC_PROFICIENCY, new EntityAttributeModifier(uuid, "Magic Proficiency", 2f, EntityAttributeModifier.Operation.ADDITION));
        return builder;
    }
}

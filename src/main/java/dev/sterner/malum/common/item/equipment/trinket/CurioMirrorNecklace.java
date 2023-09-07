package dev.sterner.malum.common.item.equipment.trinket;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.sterner.lodestone.setup.LodestoneAttributeRegistry;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.sterner.malum.api.interfaces.item.SpiritCollectActivity;
import dev.sterner.malum.common.registry.MalumAttributeRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class CurioMirrorNecklace extends TrinketItem implements SpiritCollectActivity {
    public CurioMirrorNecklace(Settings settings) {
        super(settings);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<EntityAttribute, EntityAttributeModifier> map = HashMultimap.create();
        map.put(MalumAttributeRegistry.ARCANE_RESONANCE, new EntityAttributeModifier(uuid, "Arcane Resonance", 1f, EntityAttributeModifier.Operation.ADDITION));
        map.put(LodestoneAttributeRegistry.MAGIC_RESISTANCE, new EntityAttributeModifier(uuid, "Magic Resistance", 1f, EntityAttributeModifier.Operation.ADDITION));
        return map;
    }
}

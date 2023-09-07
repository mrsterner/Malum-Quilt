package dev.sterner.malum.common.item.equipment.trinket;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.sterner.malum.api.interfaces.item.SpiritCollectActivity;
import dev.sterner.malum.common.registry.MalumAttributeRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class CurioWaterNecklace extends TrinketItem implements SpiritCollectActivity {
    public CurioWaterNecklace(Settings settings) {
        super(settings);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<EntityAttribute, EntityAttributeModifier> map = HashMultimap.create();
        map.put(MalumAttributeRegistry.SWIM_SPEED, new EntityAttributeModifier(uuid, "Swim Speed", 0.2f, EntityAttributeModifier.Operation.ADDITION) {
            @Override
            public double getValue() {
                if (entity != null) {
                    if (entity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                        return 0.45f;
                    }
                }
                return super.getValue();
            }
        });
        return map;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.tick(stack, slot, entity);
		if (entity.getWorld().getTime() % 20L == 0) {
			if (entity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
				entity.heal(2);
			}
		}
		if (entity.getWorld().getTime() % 40L == 0 && entity.isSwimming() && entity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
			EntityAttributeInstance attribute = entity.getAttributeInstance(MalumAttributeRegistry.SWIM_SPEED);
			if (attribute != null) {
				attribute.onUpdate();
			}
		}
    }
}

package dev.sterner.malum.common.item.equipment.trinket;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.sterner.lodestone.helpers.EntityHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

import static dev.sterner.malum.common.registry.MalumTagRegistry.GROSS_FOODS;

public class CurioVoraciousRing extends TrinketItem {
    public CurioVoraciousRing(Settings settings) {
        super(settings);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<EntityAttribute, EntityAttributeModifier> map = HashMultimap.create();
        map.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uuid, "Curio armor toughness boost", 1, EntityAttributeModifier.Operation.ADDITION));
        map.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "Curio armor boost", 1, EntityAttributeModifier.Operation.ADDITION));
        return map;
    }

	public static int accelerateEating(LivingEntity livingEntity, int duration) {
		if (TrinketsApi.getTrinketComponent(livingEntity).map(trinketComponent -> trinketComponent.isEquipped(MalumObjects.RING_OF_DESPERATE_VORACITY)).orElse(false)) {
			if (livingEntity.getMainHandStack().isIn(GROSS_FOODS)) {
				duration = (int)(duration * 0.5f);
			}
		}
		return duration;
	}

    public static void finishEating(LivingEntity livingEntity, ItemStack itemStack) {
        if (livingEntity instanceof PlayerEntity player) {
            if (TrinketsApi.getTrinketComponent(livingEntity).map(trinketComponent -> trinketComponent.isEquipped(MalumObjects.RING_OF_DESPERATE_VORACITY)).orElse(false)) {
                if (itemStack.isIn(GROSS_FOODS)) {
                    StatusEffectInstance gluttony = player.getStatusEffect(MalumStatusEffectRegistry.GLUTTONY);
                    if (gluttony != null) {
                        player.getHungerManager().add(1, 0.25f*(gluttony.amplifier+1));
                    }
                    player.getHungerManager().add(2, 1f);
					StatusEffectInstance hunger = player.getStatusEffect(StatusEffects.HUNGER);
                    if (hunger != null) {
                        EntityHelper.shortenEffect(hunger, player, 150);
                    }
                }
            }
        }
    }


}

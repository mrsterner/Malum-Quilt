package dev.sterner.malum.common.item.equipment.trinket;

import dev.sterner.lodestone.helpers.EntityHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.sterner.malum.api.interfaces.item.SpiritCollectActivity;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.Collection;

public class CurioAlchemicalRing extends TrinketItem implements SpiritCollectActivity {
    public CurioAlchemicalRing(Settings settings) {
        super(settings);
    }

    public static void onPotionApplied(LivingEntity livingEntity) {
        var v = TrinketsApi.getTrinketComponent(livingEntity);
        if(v.isPresent()){
            if (v.get().isEquipped(MalumObjects.RING_OF_ALCHEMICAL_MASTERY)) {
                Collection<StatusEffectInstance> effect = livingEntity.getStatusEffects();

                effect.forEach(statusEffectInstance -> {
                    StatusEffect type = statusEffectInstance.getEffectType();
                    float multiplier = MalumStatusEffectRegistry.ALCHEMICAL_PROFICIENCY_MAP.getOrDefault(Registries.STATUS_EFFECT.getKey(type), 1f);
                    if (type.isBeneficial()) {
                        EntityHelper.extendEffect(statusEffectInstance, livingEntity, (int) (statusEffectInstance.getDuration()*0.25f*multiplier));
                    }
                    else if (type.getCategory() == StatusEffectCategory.HARMFUL) {
                        EntityHelper.shortenEffect(statusEffectInstance, livingEntity, (int) (statusEffectInstance.getDuration()*0.33f*multiplier));
                    }
                });

            }
        }
    }

    @Override
    public void collect(ItemStack spirit, LivingEntity livingEntity, SlotReference slot, ItemStack trinket, double arcaneResonance) {
        livingEntity.getActiveStatusEffects().forEach((statusEffect, statusEffectInstance) -> {
            float multiplier = MalumStatusEffectRegistry.ALCHEMICAL_PROFICIENCY_MAP.getOrDefault(Registries.STATUS_EFFECT.getKey(statusEffect), 1f);
            if (statusEffect.isBeneficial()) {
                int base = 40 +(int)(arcaneResonance*20);
                EntityHelper.extendEffect(statusEffectInstance, livingEntity, (int) (base*multiplier), 1200);
            }
            else if (statusEffect.getCategory().equals(StatusEffectCategory.HARMFUL)) {
                int base = 60 +(int)(arcaneResonance*30);
                EntityHelper.shortenEffect(statusEffectInstance, livingEntity, (int) (base*multiplier));
            }
        });
    }
}

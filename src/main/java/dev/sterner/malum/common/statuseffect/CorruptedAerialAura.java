package dev.sterner.malum.common.statuseffect;

import com.sammy.lodestone.helpers.ColorHelper;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CorruptedAerialAura extends StatusEffect {
    public CorruptedAerialAura() {
        super(StatusEffectCategory.BENEFICIAL, ColorHelper.getColor(MalumSpiritTypeRegistry.AERIAL_SPIRIT.getColor()));
       // addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(), "e2306a3e-4ffc-45dc-b9c6-30acb18efab3", -0.30f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public static void onEntityJump(LivingEntity livingEntity) {
        var effectInstance = livingEntity.getStatusEffect(MalumStatusEffectRegistry.AETHERS_CHARM);
        if (effectInstance != null) {
            livingEntity.setVelocity(livingEntity.getVelocity().add(0, effectInstance.getAmplifier() * 0.15f, 0));
        }
    }

    public static void onEntityFall(LivingEntity livingEntity) {
        var effectInstance = livingEntity.getStatusEffect(MalumStatusEffectRegistry.AETHERS_CHARM);
        if (effectInstance != null) {
            livingEntity.fallDistance = livingEntity.fallDistance / (6 + effectInstance.getAmplifier());
        }
    }

    @Override
    public double adjustModifierAmount(int amplifier, EntityAttributeModifier modifier) {
        return super.adjustModifierAmount(Math.min(1, amplifier), modifier);
    }
}

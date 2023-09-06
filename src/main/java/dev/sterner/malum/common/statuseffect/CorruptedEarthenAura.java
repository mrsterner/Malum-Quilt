package dev.sterner.malum.common.statuseffect;

import com.sammy.lodestone.helpers.ColorHelper;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CorruptedEarthenAura extends StatusEffect {
    public CorruptedEarthenAura() {
        super(StatusEffectCategory.BENEFICIAL, ColorHelper.getColor(MalumSpiritTypeRegistry.EARTHEN_SPIRIT.getColor()));
        addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "e2a25284-a8b1-41a5-9472-90cc83793d44", 1, EntityAttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

    }
}

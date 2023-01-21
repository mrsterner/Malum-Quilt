package dev.sterner.malum.common.statuseffect;

import com.sammy.lodestone.helpers.ColorHelper;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class AerialAura extends StatusEffect {
    public AerialAura() {
        super(StatusEffectType.BENEFICIAL, ColorHelper.getColor(MalumSpiritTypeRegistry.AERIAL_SPIRIT.getColor()));
        addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "e3f9c028-d6cc-4cf2-86a6-d5b5efd86be6", 0.2f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

    }
}

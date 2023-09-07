package dev.sterner.malum.common.statuseffect;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CorruptedAqueousAura extends StatusEffect {
    public CorruptedAqueousAura() {
        super(StatusEffectCategory.BENEFICIAL, ColorHelper.getColor(MalumSpiritTypeRegistry.AQUEOUS_SPIRIT.getColor()));
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

    }
}

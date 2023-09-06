package dev.sterner.malum.common.statuseffect;

import com.sammy.lodestone.helpers.ColorHelper;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class InfernalAura extends StatusEffect {
    public InfernalAura() {
        super(StatusEffectCategory.BENEFICIAL, ColorHelper.getColor(MalumSpiritTypeRegistry.INFERNAL_SPIRIT.getColor()));
        addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "0a74b987-a6ec-4b9f-815e-a589bf435b93", 0.2f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public static void increaseDigSpeed(PlayerEntity player) {
        if (player.hasStatusEffect(MalumStatusEffectRegistry.MINERS_RAGE)) {
            //event.setNewSpeed(event.getOriginalSpeed() * (1 + 0.2f *player.getEffect(MalumMobEffectRegistry.MINERS_RAGE.get()).getAmplifier()));
        }
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

    }
}

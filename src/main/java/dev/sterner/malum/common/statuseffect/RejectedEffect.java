package dev.sterner.malum.common.statuseffect;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.malum.common.component.MalumComponents;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class RejectedEffect extends StatusEffect {
	public RejectedEffect() {
		super(StatusEffectCategory.NEUTRAL, ColorHelper.getColor(20, 14, 22));
		addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "248da214-2292-4637-a040-5597fb65db58", -0.2f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		MalumComponents.TOUCH_OF_DARKNESS_COMPONENT.maybeGet(entity).ifPresent(l -> {
			l.afflict(20);
			if (entity.getWorld().getTime() % 60L == 0) {
				entity.damage(MalumDamageSourceRegistry.create(entity.getWorld(), MalumDamageSourceRegistry.VOODOO), 1);
			}
		});
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}
}

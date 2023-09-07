package dev.sterner.malum.common.statuseffect;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.lodestone.helpers.EntityHelper;
import dev.sterner.lodestone.setup.LodestoneAttributeRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

import java.util.Optional;

import static dev.sterner.malum.common.registry.MalumTagRegistry.GROSS_FOODS;

public class GluttonyEffect extends StatusEffect {
    public GluttonyEffect() {
        super(StatusEffectCategory.BENEFICIAL, ColorHelper.getColor(88, 86, 60));
        addAttributeModifier(LodestoneAttributeRegistry.MAGIC_PROFICIENCY, "4d82fd0a-24b6-45f5-8d7a-983f99fd6783", 2f, EntityAttributeModifier.Operation.ADDITION);
    }

    public static boolean canApplyPotion(LivingEntity livingEntity) {
		Optional<StatusEffectInstance> optional =  livingEntity.getStatusEffects().stream().filter(s -> s.getEffectType().equals(StatusEffects.HUNGER)).findAny();
		return optional.isEmpty() || !livingEntity.hasStatusEffect(MalumStatusEffectRegistry.GLUTTONY);
	}

    public static void finishEating(ItemStack itemStack, LivingEntity livingEntity) {
        if (itemStack.isIn(GROSS_FOODS)) {
            var effect = livingEntity.getStatusEffect(MalumStatusEffectRegistry.GLUTTONY);
            if (effect != null) {
                EntityHelper.extendEffect(effect, livingEntity, 200, 1000);
                World world = livingEntity.getWorld();
                world.playSound(null, livingEntity.getBlockPos(), MalumSoundRegistry.HUNGRY_BELT_FEEDS, SoundCategory.PLAYERS, 1.7f, 1.2f + world.random.nextFloat() * 0.5f);
            }
        }
    }



    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            player.addExhaustion(0.004f * (amplifier + 1));
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}

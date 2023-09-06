package dev.sterner.malum.common.statuseffect;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.sammy.lodestone.helpers.ColorHelper;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;


public class AqueousAura extends StatusEffect {
    public AqueousAura() {
        super(StatusEffectCategory.BENEFICIAL, ColorHelper.getColor(MalumSpiritTypeRegistry.AQUEOUS_SPIRIT.getColor()));
        addAttributeModifier(ReachEntityAttributes.REACH, "738bd9e4-23d8-46b0-b8ba-45a2016eec74", 1f, EntityAttributeModifier.Operation.ADDITION);
    }

    public static Box growBoundingBox(PlayerEntity player, Box original) {
        StatusEffectInstance effect = player.getStatusEffect(MalumStatusEffectRegistry.POSEIDONS_GRASP);
        if (effect != null) {
            original = original.expand((effect.getAmplifier() + 1) * 1.5f);
        }
        return original;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

    }
}

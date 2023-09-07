package dev.sterner.malum.common.statuseffect;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.malum.common.item.tools.MalumScytheItem;
import dev.sterner.malum.common.registry.MalumAttributeRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class WickedIntentEffect extends StatusEffect {
    public WickedIntentEffect() {
        super(StatusEffectCategory.BENEFICIAL, ColorHelper.getColor(88, 86, 60));
        addAttributeModifier(MalumAttributeRegistry.SCYTHE_PROFICIENCY, "0cd21cec-758c-456b-9955-06713e732303", 8f, EntityAttributeModifier.Operation.ADDITION);
    }

    public static void removeWickedIntent(DamageSource damageSource) {
        if (damageSource.isOf(DamageTypes.MAGIC) || (damageSource.isOf(DamageTypes.THORNS))) {
            return;
        }
        if (damageSource.getSource() instanceof LivingEntity livingEntity) {
            if (MalumScytheItem.getScytheItemStack(damageSource, livingEntity).isEmpty()) {
                return;
            }
            var effect = livingEntity.getStatusEffect(MalumStatusEffectRegistry.WICKED_INTENT);
            if (effect != null) {
                World world = livingEntity.getWorld();
                if (livingEntity instanceof PlayerEntity player) {
                    if (player.getItemCooldownManager().isCoolingDown(MalumObjects.NECKLACE_OF_THE_HIDDEN_BLADE)) {
                        return;
                    }
                    int pTicks = (effect.getAmplifier()) > 4 ? 160 : 40;
                    player.getItemCooldownManager().set(MalumObjects.NECKLACE_OF_THE_HIDDEN_BLADE, pTicks);
                }
                livingEntity.removeStatusEffect(effect.getEffectType());
                world.playSound(null, livingEntity.getBlockPos(), MalumSoundRegistry.HIDDEN_BLADE_STRIKES, SoundCategory.PLAYERS, 2.5f, 1 + world.random.nextFloat() * 0.15f);
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}

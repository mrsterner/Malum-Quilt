package dev.sterner.malum.common.event;


import dev.sterner.malum.api.event.ExplosionEvent;
import dev.sterner.malum.api.event.LivingEntityEvent;
import dev.sterner.malum.api.event.SoulwardDamageAbsorbDamageEvent;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import dev.sterner.malum.common.registry.MalumTagRegistry;
import dev.sterner.malum.common.util.TrinketsHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;

public class MalumTrinketEvents {

	public static void init(){
		LivingEntityEvent.ON_DAMAGE_EVENT.register(MalumTrinketEvents::waterNecklace);
		LivingEntityEvent.ON_DAMAGE_EVENT.register(MalumTrinketEvents::takeDamageEvent);
		SoulwardDamageAbsorbDamageEvent.ON_ABSORB_DAMAGE_EVENT.register(MalumTrinketEvents::onSoulwardAbsorbDamage);
		ExplosionEvent.DETONATE.register(MalumTrinketEvents::processExplosion);
	}

	private static float waterNecklace(LivingEntity livingEntity, DamageSource damageSource, float amount) {
		if(TrinketsHelper.hasTrinket(livingEntity, MalumObjects.NECKLACE_OF_TIDAL_AFFINITY)){
			if (livingEntity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
				amount = amount * 0.5f;
			}
		}
		return amount;
	}

	private static float takeDamageEvent(LivingEntity livingEntity, DamageSource damageSource, float amount) {
		if(livingEntity instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(MalumObjects.NECKLACE_OF_THE_HIDDEN_BLADE)){
			return amount;
		}
		int amplifier = (int) Math.ceil(amount / 4f);
		if (amplifier >= 6) {
			amplifier *= amplifier / 6f;
		}
		StatusEffect effect = MalumStatusEffectRegistry.WICKED_INTENT;
		livingEntity.addStatusEffect(new StatusEffectInstance(effect, 40, amplifier - 1));
		return amount;
	}

	public static void onSoulwardAbsorbDamage(LivingEntity wardedEntity,DamageSource source, float soulwardLost, float damageAbsorbed) {
		if (source.getAttacker() != null) {
			if (!source.isOf(DamageTypes.THORNS)) {
                source.getAttacker().damage(MalumDamageSourceRegistry.create(wardedEntity.getWorld(), MalumDamageSourceRegistry.MAGEBANE, wardedEntity), damageAbsorbed);
            }
		}
	}

	private static void processExplosion(World world, Explosion explosion, List<Entity> entities, double v) {
		LivingEntity exploder = explosion.getCausingEntity();
		if (exploder != null && TrinketsHelper.hasTrinket(exploder, MalumObjects.BELT_OF_THE_PROSPECTOR)) {
			entities.removeIf(e -> e instanceof ItemEntity itemEntity && itemEntity.getStack().isIn(MalumTagRegistry.PROSPECTORS_TREASURE));
		}
	}
}

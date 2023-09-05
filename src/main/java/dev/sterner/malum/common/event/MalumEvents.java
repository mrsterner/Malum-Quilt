package dev.sterner.malum.common.event;

import dev.sterner.malum.api.event.LivingEntityEvent;
import dev.sterner.malum.common.item.tools.MalumScytheItem;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumParticleRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.util.TrinketsHelper;
import dev.sterner.malum.common.util.handler.EsotericReapingHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class MalumEvents {
	public static void init(){
		LivingEntityEvent.ON_DAMAGE_EVENT.register(MalumEvents::scytheSweep);
		LivingEntityDeathCallback.EVENT.register(EsotericReapingHandler::tryCreateReapingDrops);
	}

	private static float scytheSweep(LivingEntity target, DamageSource damageSource, float v) {
		Entity entity = damageSource.getAttacker();
		if(entity instanceof LivingEntity attacker && attacker.getMainHandStack().getItem() instanceof MalumScytheItem){
			boolean canSweep = !TrinketsHelper.hasTrinket(attacker, MalumObjects.NECKLACE_OF_THE_NARROW_EDGE) && !TrinketsHelper.hasTrinket(attacker, MalumObjects.NECKLACE_OF_THE_HIDDEN_BLADE);
			if (attacker instanceof PlayerEntity player) {
				SoundEvent sound;
				if (canSweep) {
					MalumScytheItem.spawnSweepParticles(player, MalumParticleRegistry.SCYTHE_SWEEP_ATTACK_PARTICLE);
					sound = SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP;
				} else {
					MalumScytheItem.spawnSweepParticles(player, MalumParticleRegistry.SCYTHE_CUT_ATTACK_PARTICLE);
					sound = MalumSoundRegistry.SCYTHE_CUT;
				}
				attacker.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), sound, attacker.getSoundCategory(), 1, 1);
			}

			if (!canSweep || damageSource.isMagic() || damageSource.getName().equals(MalumDamageSourceRegistry.SCYTHE_SWEEP_IDENTIFIER)) {
				return v;
			}
			int world = EnchantmentHelper.getEquipmentLevel(Enchantments.SWEEPING, attacker);
			float damage = v * (0.5f + EnchantmentHelper.getSweepingMultiplier(attacker));
			target.getWorld().getOtherEntities(attacker, target.getBoundingBox().expand(1 + world * 0.25f)).forEach(e -> {
				if (e instanceof LivingEntity livingEntity) {
					if (livingEntity.isAlive()) {
						livingEntity.damage(MalumDamageSourceRegistry.scytheSweepDamage(attacker), damage);
						livingEntity.takeKnockback(0.4F, MathHelper.sin(attacker.getYaw() * ((float) Math.PI / 180F)), (-MathHelper.cos(attacker.getYaw() * ((float) Math.PI / 180F))));
					}
				}
			});
		}
		return v;
	}
}

package dev.sterner.malum.common.event;

import dev.sterner.malum.api.event.LivingEntityEvent;
import dev.sterner.malum.common.component.SoulDataComponent;
import dev.sterner.malum.common.item.tools.MalumScytheItem;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumParticleRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.util.TrinketsHelper;
import dev.sterner.malum.common.util.handler.EsotericReapingHandler;
import dev.sterner.malum.common.util.handler.SoulHarvestHandler;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class MalumEvents {
	public static void init(){
		LivingEntityEvent.ON_DAMAGE_EVENT.register(MalumEvents::scytheSweep);
		LivingEntityEvent.ON_TARGETING_EVENT.register(SoulDataComponent::setTaget);
		LivingEntityEvents.TICK.register(SoulDataComponent::tick);
		LivingEntityDamageEvents.HURT.register(SoulDataComponent::hurt);
		LivingEntityEvent.ADDED_EVENT.register(SoulDataComponent::added);
		LivingEntityEvents.NATURAL_SPAWN.register(SoulDataComponent::spawn);
		ServerLivingEntityEvents.AFTER_DEATH.register(EsotericReapingHandler::tryCreateReapingDrops);
		LivingEntityEvent.TICK_EVENT.register(SoulHarvestHandler::playerTick);

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

			if (!canSweep || damageSource.isOf(DamageTypes.MAGIC) || damageSource.isOf(MalumDamageSourceRegistry.SCYTHE_SWEEP)) {
				return v;
			}
			int world = EnchantmentHelper.getEquipmentLevel(Enchantments.SWEEPING, attacker);
			float damage = v * (0.5f + EnchantmentHelper.getSweepingMultiplier(attacker));
			target.getWorld().getOtherEntities(attacker, target.getBoundingBox().expand(1 + world * 0.25f)).forEach(e -> {
				if (e instanceof LivingEntity livingEntity) {
					if (livingEntity.isAlive()) {
						livingEntity.damage(MalumDamageSourceRegistry.create(livingEntity.getWorld(), MalumDamageSourceRegistry.SCYTHE_SWEEP, attacker), damage);
						livingEntity.takeKnockback(0.4F, MathHelper.sin(attacker.getYaw() * ((float) Math.PI / 180F)), (-MathHelper.cos(attacker.getYaw() * ((float) Math.PI / 180F))));
					}
				}
			});
		}
		return v;
	}
}

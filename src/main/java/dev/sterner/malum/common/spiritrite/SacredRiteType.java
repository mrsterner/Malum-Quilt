package dev.sterner.malum.common.spiritrite;

import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.entity.MajorEntityEffectParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.entity.MinorEntityEffectParticlePacket;
import dev.sterner.malum.common.spiritrite.effect.EntityAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.HashMap;
import java.util.Map;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.ARCANE_SPIRIT;
import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.SACRED_SPIRIT;

public class SacredRiteType extends MalumRiteType{
	public static final Map<Class<? extends AnimalEntity>, SacredRiteEntityActor<?>> ACTORS = Util.make(new HashMap<>(), m -> {
		m.put(SheepEntity.class, new SacredRiteEntityActor<>(SheepEntity.class) {
			@Override
			public void act(TotemBaseBlockEntity totemBaseBlockEntity, SheepEntity sheep) {
				if (sheep.getRandom().nextInt(sheep.isBaby() ? 5 : 25) == 0) {
					BlockPos blockpos = sheep.getBlockPos();
					if (EatGrassGoal.GRASS_PREDICATE.test(sheep.getWorld().getBlockState(blockpos)) || sheep.getWorld().getBlockState(blockpos.down()).isOf(Blocks.GRASS_BLOCK)) {
						EatGrassGoal goal = sheep.eatGrassGoal;
						goal.start();
						PlayerLookup.tracking(sheep).forEach(track -> MajorEntityEffectParticlePacket.send(track, SACRED_SPIRIT.getColor(), sheep.getX(), sheep.getY() + sheep.getHeight() / 2f, sheep.getZ()));
					}
				}
			}
		});
		m.put(BeeEntity.class, new SacredRiteEntityActor<>(BeeEntity.class) {
			@Override
			public void act(TotemBaseBlockEntity totemBaseBlockEntity, BeeEntity bee) {
				BeeEntity.PollinateGoal goal = bee.pollinateGoal;
				if (goal.canBeeStart()) {
					goal.pollinationTicks += 40;
					goal.tick();
					PlayerLookup.tracking(bee).forEach(track -> MinorEntityEffectParticlePacket.send(track, SACRED_SPIRIT.getColor(), bee.getX(), bee.getY() + bee.getHeight() / 2f, bee.getZ()));
				}
			}
		});

		m.put(ChickenEntity.class, new SacredRiteEntityActor<>(ChickenEntity.class) {
			@Override
			public void act(TotemBaseBlockEntity totemBaseBlockEntity, ChickenEntity chicken) {
				chicken.eggLayTime -= 80;
				PlayerLookup.tracking(chicken).forEach(track -> MinorEntityEffectParticlePacket.send(track, SACRED_SPIRIT.getColor(), chicken.getX(), chicken.getY() + chicken.getHeight() / 2f, chicken.getZ()));
			}
		});
	});

	public SacredRiteType() {
		super("sacred_rite", ARCANE_SPIRIT, SACRED_SPIRIT, SACRED_SPIRIT);
	}

	@Override
	public MalumRiteEffect getNaturalRiteEffect() {
		return new EntityAffectingRiteEffect() {
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				getNearbyEntities(totemBase, LivingEntity.class, e -> !(e instanceof HostileEntity)).forEach(e -> {
					if (e.getHealth() < e.getMaxHealth() * 0.75f) {
						e.heal(2);
						PlayerLookup.tracking(e).forEach(track -> MinorEntityEffectParticlePacket.send(track, getEffectSpirit().getColor(), e.getX(), e.getY() + e.getHeight() / 2f, e.getZ()));
					}
				});
			}
		};
	}

	@Override
	public MalumRiteEffect getCorruptedEffect() {
		return new EntityAffectingRiteEffect() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				getNearbyEntities(totemBase, AnimalEntity.class).forEach(e -> {
					if (e.age < 0) {
						if (totemBase.getWorld().random.nextFloat() <= 0.04f) {
							PlayerLookup.tracking(e).forEach(track -> MajorEntityEffectParticlePacket.send(track, getEffectSpirit().getColor(), e.getX(), e.getY() + e.getHeight() / 2f, e.getZ()));
							e.growUp(25);
						}
					}
					if (ACTORS.containsKey(e.getClass())) {
						SacredRiteEntityActor<? extends AnimalEntity> sacredRiteEntityActor = ACTORS.get(e.getClass());
						sacredRiteEntityActor.tryAct(totemBase, e);
					}
				});
			}
		};
	}

	public static abstract class SacredRiteEntityActor<T extends AnimalEntity> {
		public final Class<T> targetClass;

		public SacredRiteEntityActor(Class<T> targetClass) {
			this.targetClass = targetClass;
		}

		@SuppressWarnings("unchecked")
		public final void tryAct(TotemBaseBlockEntity totemBaseBlockEntity, AnimalEntity animal) {
			if (targetClass.isInstance(animal)) {
				act(totemBaseBlockEntity, (T) animal);
			}
		}

		public abstract void act(TotemBaseBlockEntity totemBaseBlockEntity, T entity);
	}
}

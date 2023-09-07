package dev.sterner.malum.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public final class LivingEntityEvent {

	public static final Event<Tick> TICK_EVENT = EventFactory.createArrayBacked(Tick.class,
			(listeners) -> (livingEntity) -> {
				for (Tick event : listeners) {
					event.react(livingEntity);
				}
			}
	);

	public static final Event<Targeting> ON_TARGETING_EVENT = EventFactory.createArrayBacked(Targeting.class,
			(listeners) -> (mobEntity, target) -> {
				for (Targeting event : listeners) {
					return event.react(mobEntity, target);
				}
				return false;
			}
	);

	public static final Event<Added> ADDED_EVENT = EventFactory.createArrayBacked(Added.class,
			(listeners) -> (mobEntity, existing) -> {
				for (Added event : listeners) {
					event.react(mobEntity, existing);
				}
			}
	);

	public static final Event<OnDamage> ON_DAMAGE_EVENT = EventFactory.createArrayBacked(OnDamage.class,
			(listeners) -> (hurtEntity, damageSource, amount) -> {
				for (OnDamage event : listeners) {
					return event.react(hurtEntity, damageSource, amount);
				}
				return amount;
			}
	);

	@FunctionalInterface
	public interface OnDamage {
		float react(LivingEntity hurtEntity, DamageSource damageSource, float amount);
	}

	@FunctionalInterface
	public interface Tick {
		void react(LivingEntity livingEntity);
	}

	@FunctionalInterface
	public interface Targeting {
		boolean react(MobEntity mobEntity, @Nullable LivingEntity target);
	}

	@FunctionalInterface
	public interface Added {
		void react(LivingEntity livingEntity, boolean existing);
	}

}

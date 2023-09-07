package dev.sterner.malum.common.spiritrite.effect;

import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.entity.MajorEntityEffectParticlePacket;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.function.Predicate;

public class AuraRiteEffect extends EntityAffectingRiteEffect {

	public final Class<? extends LivingEntity> targetClass;
	public final StatusEffect effect;
	public final MalumSpiritType spirit;

	public AuraRiteEffect(Class<? extends LivingEntity> targetClass, StatusEffect effect, MalumSpiritType spirit) {
		super();
		this.targetClass = targetClass;
		this.effect = effect;
		this.spirit = spirit;
	}

	@Override
	public int getRiteEffectRadius() {
		return BASE_RADIUS*4;
	}

	@Override
	public void riteEffect(TotemBaseBlockEntity totemBase) {
		getNearbyEntities(totemBase, getEntityClass()).filter(getEntityPredicate()).forEach(e -> {
			if (!e.hasStatusEffect(effect)) {
				PlayerLookup.tracking(e).forEach(track -> MajorEntityEffectParticlePacket.send(track, spirit.getColor(), e.getX(), e.getY()+ e.getHeight() / 2f, e.getZ()));
			}
			e.addStatusEffect(new StatusEffectInstance(effect, getEffectDuration(), getEffectAmplifier(), true, true));
		});
	}

	public Class<? extends LivingEntity> getEntityClass() {
		return targetClass;
	}

	public Predicate<LivingEntity> getEntityPredicate() {
		return e -> !(e instanceof HostileEntity);
	}

	public int getEffectDuration() {
		return 300;
	}

	public int getEffectAmplifier() {
		return 1;
	}
}

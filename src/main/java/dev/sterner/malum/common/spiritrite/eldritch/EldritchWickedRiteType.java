package dev.sterner.malum.common.spiritrite.eldritch;


import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.entity.MajorEntityEffectParticlePacket;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import dev.sterner.malum.common.spiritrite.effect.EntityAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.List;
import java.util.stream.Collectors;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.*;

public class EldritchWickedRiteType extends MalumRiteType {
	public EldritchWickedRiteType() {
		super("greater_wicked_rite", ELDRITCH_SPIRIT, ARCANE_SPIRIT, WICKED_SPIRIT, WICKED_SPIRIT);
	}

	@Override
	public MalumRiteEffect getNaturalRiteEffect() {
		return new EntityAffectingRiteEffect() {
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				getNearbyEntities(totemBase, LivingEntity.class).forEach(e -> {
					var v = MalumDamageSourceRegistry.create(e.getWorld(), MalumDamageSourceRegistry.VOODOO);
					if (e.getHealth() <= 2.5f && !e.isInvulnerableTo(v)) {
						PlayerLookup.tracking(e).forEach(track -> MajorEntityEffectParticlePacket.send(track, getEffectSpirit().getColor(), e.getX(), e.getY()+ e.getHeight() / 2f, e.getZ()));
						e.damage(v, 10f);
					}
				});
			}
		};
	}

	@Override
	public MalumRiteEffect getCorruptedEffect() {
		return new EntityAffectingRiteEffect() {
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				List<AnimalEntity> entities = getNearbyEntities(totemBase, AnimalEntity.class, e -> !e.isInLove() && e.age > 0 && !e.isInvulnerableTo(MalumDamageSourceRegistry.create(e.getWorld(), MalumDamageSourceRegistry.VOODOO))).collect(Collectors.toList());
				if (entities.size() < 30) {
					return;
				}
				int maxKills = entities.size() - 30;
				entities.removeIf(AnimalEntity::isInLove);
				for (AnimalEntity entity : entities) {
					entity.damage(MalumDamageSourceRegistry.create(entity.getWorld(), MalumDamageSourceRegistry.VOODOO), entity.getMaxHealth());
					PlayerLookup.tracking(entity).forEach(track -> MajorEntityEffectParticlePacket.send(track, WICKED_SPIRIT.getColor(), entity.getX(), entity.getY() + entity.getHeight() / 2f, entity.getZ()));
					if (maxKills-- <= 0) {
						return;
					}
				}
			}
		};
	}
}

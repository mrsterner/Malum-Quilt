package dev.sterner.malum.common.spiritrite;


import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.entity.MajorEntityEffectParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.entity.MinorEntityEffectParticlePacket;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import dev.sterner.malum.common.spiritrite.effect.EntityAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.ARCANE_SPIRIT;
import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.WICKED_SPIRIT;

public class WickedRiteType extends MalumRiteType {
	public WickedRiteType() {
		super("wicked_rite", ARCANE_SPIRIT, WICKED_SPIRIT, WICKED_SPIRIT);
	}
	@Override
	public MalumRiteEffect getNaturalRiteEffect() {
		return new EntityAffectingRiteEffect() {
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				getNearbyEntities(totemBase, LivingEntity.class, e -> !(e instanceof PlayerEntity)).forEach(e -> {
					var v = MalumDamageSourceRegistry.create(e.getWorld(), MalumDamageSourceRegistry.VOODOO);
					if (e.getHealth() > 2.5f && !e.isInvulnerableTo(v)) {
						PlayerLookup.tracking(e).forEach(track -> MinorEntityEffectParticlePacket.send(track, getEffectSpirit().getColor(), e.getX(), e.getY() + e.getHeight() / 2f, e.getZ()));
						e.damage(v, 2);
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
				getNearbyEntities(totemBase, HostileEntity.class).forEach(e -> {
					PlayerLookup.tracking(e).forEach(track -> MajorEntityEffectParticlePacket.send(track, getEffectSpirit().getColor(), e.getX(), e.getY() + e.getHeight() / 2f, e.getZ()));
					e.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 1));
					e.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 1));
					e.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 1));
				});
			}
		};
	}
}

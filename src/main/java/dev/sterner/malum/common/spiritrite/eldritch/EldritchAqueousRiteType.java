package dev.sterner.malum.common.spiritrite.eldritch;

import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.entity.MajorEntityEffectParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.entity.MinorEntityEffectParticlePacket;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import dev.sterner.malum.common.spiritrite.effect.BlockAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.EntityAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.Set;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.*;

public class EldritchAqueousRiteType extends MalumRiteType {
	public EldritchAqueousRiteType() {
		super("greater_aqueous_rite", ELDRITCH_SPIRIT, ARCANE_SPIRIT, AQUEOUS_SPIRIT, AQUEOUS_SPIRIT);
	}

	@Override
	public MalumRiteEffect getNaturalRiteEffect() {
		return new BlockAffectingRiteEffect() {

			@SuppressWarnings("ConstantConditions")
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				World world = totemBase.getWorld();
				getNearbyBlocks(totemBase, PointedDripstoneBlock.class, getRiteEffectRadius()*4).forEach(p -> {
					if (world.random.nextFloat() < 0.1f && world instanceof ServerWorld serverWorld) {
						for (int i = 0; i < 4 + world.random.nextInt(2); i++) {
							world.getBlockState(p).randomTick(serverWorld, p, world.random);
						}
						PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(p).getPos()).forEach(track -> MinorEntityEffectParticlePacket.send(track, AQUEOUS_SPIRIT.getColor(), p.getX() + 0.5f, p.getY() + 0.5f, p.getZ() + 0.5f));
					}
				});
			}

			@SuppressWarnings("ConstantConditions")
			@Override
			public boolean canAffectBlock(TotemBaseBlockEntity totemBase, Set<Block> filters, BlockState state, BlockPos pos) {
				return super.canAffectBlock(totemBase, filters, state, pos) && PointedDripstoneBlock.isHeldByPointedDripstone(state, totemBase.getWorld(), pos);
			}
		};
	}

	@Override
	public MalumRiteEffect getCorruptedEffect() {
		return new EntityAffectingRiteEffect() {
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				getNearbyEntities(totemBase, ZombieEntity.class).filter(z -> !(z instanceof DrownedEntity)).forEach(e -> {
					if (!e.isConvertingInWater()) {
						e.setTicksUntilWaterConversion(100);
						PlayerLookup.tracking(e).forEach(track -> MajorEntityEffectParticlePacket.send(track, AQUEOUS_SPIRIT.getColor(), e.getX(), e.getY() + e.getHeight() / 2f, e.getZ()));
					}
				});
			}
		};
	}
}

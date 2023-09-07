package dev.sterner.malum.common.spiritrite;

import dev.sterner.malum.common.block.BlockTagRegistry;
import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.block.BlockSparkleParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.block.FireBlockExtinguishSparkleParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.entity.MajorEntityEffectParticlePacket;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import dev.sterner.malum.common.spiritrite.effect.AuraRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.ARCANE_SPIRIT;
import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.INFERNAL_SPIRIT;

public class InfernalRiteType extends MalumRiteType {
	public InfernalRiteType() {
		super("infernal_rite", ARCANE_SPIRIT, INFERNAL_SPIRIT, INFERNAL_SPIRIT);
	}

	@Override
	public MalumRiteEffect getNaturalRiteEffect() {
		return new AuraRiteEffect(LivingEntity.class, MalumStatusEffectRegistry.MINERS_RAGE, INFERNAL_SPIRIT);
	}

	@Override
	public MalumRiteEffect getCorruptedEffect() {
		return new MalumRiteEffect() {
			@Override
			public int getRiteEffectRadius() {
				return BASE_RADIUS * 4;
			}

			@SuppressWarnings("ConstantConditions")
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				World world = totemBase.getWorld();
				getNearbyEntities(totemBase, LivingEntity.class).filter(e -> !(e instanceof HostileEntity)).forEach(e -> {
					if (e.isOnFire()) {
						world.playSound(null, e.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
						PlayerLookup.tracking(e).forEach(track -> MajorEntityEffectParticlePacket.send(track, getEffectSpirit().getColor(), e.getX(), e.getY() + e.getHeight() / 2f, e.getZ()));

						e.addStatusEffect(new StatusEffectInstance(MalumStatusEffectRegistry.IFRITS_EMBRACE, 400, 1));
						e.extinguish();
					}
				});


				getNearbyBlocks(totemBase, AbstractFireBlock.class).forEach(p -> {
					BlockState state = totemBase.getWorld().getBlockState(p);
					if (!state.isIn(BlockTagRegistry.ENDLESS_FLAME)) {
						world.playSound(null, p, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
						if(world instanceof ServerWorld serverWorld){
							PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(p).getPos()).forEach(track -> FireBlockExtinguishSparkleParticlePacket.send(track, INFERNAL_SPIRIT.getColor(), p));
							PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(p).getPos()).forEach(track -> BlockSparkleParticlePacket.send(track, ARCANE_SPIRIT.getColor(), p));
						}
						totemBase.getWorld().removeBlock(p, false);
					}
				});
			}
		};
	}
}

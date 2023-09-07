package dev.sterner.malum.common.spiritrite.eldritch;


import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.block.BlockSparkleParticlePacket;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import dev.sterner.malum.common.spiritrite.effect.BlockAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.Set;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.*;

public class EldritchEarthenRiteType extends MalumRiteType {
	public EldritchEarthenRiteType() {
		super("greater_earthen_rite", ELDRITCH_SPIRIT, ARCANE_SPIRIT, EARTHEN_SPIRIT, EARTHEN_SPIRIT);
	}

	@Override
	public MalumRiteEffect getNaturalRiteEffect() {
		return new BlockAffectingRiteEffect() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				World world = totemBase.getWorld();
				getBlocksUnderBase(totemBase, Block.class).forEach(p -> {
					BlockState state = world.getBlockState(p);
					boolean canBreak = !state.isAir() && state.getHardness(world, p) != -1;
					if (canBreak) {
						world.breakBlock(p, true);
						if(world instanceof ServerWorld serverWorld){
							if (state.getBlock() instanceof InfestedBlock infestedBlock) {
								infestedBlock.onStacksDropped(state, serverWorld, p, ItemStack.EMPTY, true);
							}
							PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(p).getPos()).forEach(track -> BlockSparkleParticlePacket.send(track, EARTHEN_SPIRIT.getColor(), p));
						}

					}
				});
			}

			@Override
			public boolean canAffectBlock(TotemBaseBlockEntity totemBase, Set<Block> filters, BlockState state, BlockPos pos) {
				return super.canAffectBlock(totemBase, filters, state, pos) && !state.isAir();
			}
		};
	}

	@Override
	public MalumRiteEffect getCorruptedEffect() {
		return new BlockAffectingRiteEffect() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				World world = totemBase.getWorld();
				getBlocksUnderBase(totemBase, Block.class).forEach(p -> {
					BlockState state = world.getBlockState(p);
					boolean canBreak = state.isAir() || state.isReplaceable();
					if (canBreak) {
						BlockState cobblestone = Blocks.COBBLESTONE.getDefaultState();
						world.setBlockState(p, cobblestone);
						world.syncWorldEvent(2001, p, Block.getRawIdFromState(cobblestone));
						if(world instanceof ServerWorld serverWorld){
							PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(p).getPos()).forEach(track -> BlockSparkleParticlePacket.send(track, EARTHEN_SPIRIT.getColor(), p));
						}
					}
				});
			}
		};
	}
}

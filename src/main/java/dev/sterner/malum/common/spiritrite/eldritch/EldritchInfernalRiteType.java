package dev.sterner.malum.common.spiritrite.eldritch;

import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.block.BlockSparkleParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.block.MinorBlockSparkleParticlePacket;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import dev.sterner.malum.common.spiritrite.effect.BlockAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.Optional;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.*;

public class EldritchInfernalRiteType extends MalumRiteType {
	public EldritchInfernalRiteType() {
		super("greater_infernal_rite", ELDRITCH_SPIRIT, ARCANE_SPIRIT, INFERNAL_SPIRIT, INFERNAL_SPIRIT);
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
					Optional<SmeltingRecipe> optional = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(new ItemStack(state.getBlock().asItem(), 1)), world);
					if (optional.isPresent()) {
						SmeltingRecipe recipe = optional.get();
						ItemStack output = recipe.getOutput(world.getRegistryManager());
						if (output.getItem() instanceof BlockItem) {
							Block block = ((BlockItem) output.getItem()).getBlock();
							BlockState newState = block.getDefaultState();
							world.setBlockState(p, newState);
							world.syncWorldEvent(2001, p, Block.getRawIdFromState(newState));
							if(world instanceof ServerWorld serverWorld){
								PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(p).getPos()).forEach(track -> BlockSparkleParticlePacket.send(track, INFERNAL_SPIRIT.getColor(), p));
							}
						}
					}
				});
			}
		};
	}

	@Override
	public MalumRiteEffect getCorruptedEffect() {
		return new MalumRiteEffect() {
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				World world = totemBase.getWorld();
				getNearbyBlocks(totemBase, AbstractFurnaceBlock.class).map(b -> world.getBlockEntity(b)).filter(e -> e instanceof AbstractFurnaceBlockEntity).map(e -> (AbstractFurnaceBlockEntity) e).forEach(f -> {
					if (f.isBurning()) {
						BlockPos blockPos = f.getPos();
						if(world instanceof ServerWorld serverWorld){
							PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(blockPos).getPos()).forEach(track -> MinorBlockSparkleParticlePacket.send(track, INFERNAL_SPIRIT.getColor(), blockPos));
						}
						f.cookTime = Math.min(f.cookTime + 5, f.cookTimeTotal - 1);
					}
				});
			}

			@Override
			public int getRiteEffectTickRate() {
				return BASE_TICK_RATE;
			}

			@Override
			public int getRiteEffectRadius() {
				return BASE_RADIUS*2;
			}
		};
	}
}

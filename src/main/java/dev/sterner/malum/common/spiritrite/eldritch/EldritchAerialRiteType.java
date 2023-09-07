package dev.sterner.malum.common.spiritrite.eldritch;

import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.block.BlockDownwardSparkleParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.entity.MinorEntityEffectParticlePacket;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.registry.MalumTagRegistry;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import dev.sterner.malum.common.spiritrite.effect.BlockAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.EntityAffectingRiteEffect;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.*;

public class EldritchAerialRiteType extends MalumRiteType {
	public EldritchAerialRiteType() {
		super("greater_aerial_rite", ELDRITCH_SPIRIT, ARCANE_SPIRIT, AERIAL_SPIRIT, AERIAL_SPIRIT);
	}

	@Override
	public MalumRiteEffect getNaturalRiteEffect() {
		return new BlockAffectingRiteEffect() {
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				World world = totemBase.getWorld();
				if (world instanceof ServerWorld serverWorld) {
					BlockPos pos = totemBase.getPos();
					getBlocksUnderBase(totemBase, Block.class).forEach(p -> {
						BlockState stateBelow = world.getBlockState(p.down());
						if (FallingBlock.canFallThrough(stateBelow) || !stateBelow.isOpaque() || stateBelow.isIn(BlockTags.SLABS)) {
							BlockState state = world.getBlockState(p);
							if (!state.isAir() && world.getBlockEntity(p) == null && canSilkTouch(serverWorld, pos, state)) {
								FallingBlockEntity.spawnFromBlock(world, p, state);
								world.playSound(null, p, MalumSoundRegistry.AERIAL_FALL, SoundCategory.BLOCKS, 0.5f, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
								PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> BlockDownwardSparkleParticlePacket.send(track, AERIAL_SPIRIT.getColor(), p));
							}
						}
					});
				}
			}
		};
	}

	private static final List<Item> TOOLS = List.of(Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE);

	// From Botania, modified slightly
	private static ItemStack getToolForState(BlockState state) {
		if (!state.isToolRequired()) {
			return new ItemStack(Items.NETHERITE_PICKAXE);
		} else {
			for (Item item : TOOLS) {
				ItemStack stack = new ItemStack(item);
				if (stack.isSuitableFor(state)) {
					return stack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	private static boolean canSilkTouch(ServerWorld world, BlockPos pos, BlockState state) {
		if (state.isIn(MalumTagRegistry.GREATER_AERIAL_WHITELIST)) {
			return true;
		}

		ItemStack harvestToolStack = getToolForState(state);
		if (harvestToolStack.isEmpty()) {
			return false;
		}
		harvestToolStack.addEnchantment(Enchantments.SILK_TOUCH, 1);
		List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, null, null, harvestToolStack);
		Item blockItem = state.getBlock().asItem();
		return drops.stream().anyMatch(s -> s.getItem() == blockItem);
	}

	@Override
	public MalumRiteEffect getCorruptedEffect() {
		return new EntityAffectingRiteEffect() {
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				getNearbyEntities(totemBase, ServerPlayerEntity.class).forEach(p -> {
					ServerStatHandler stats = p.getStatHandler();
					Stat<Identifier> sleepStat = Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST);
					int value = stats.getStat(sleepStat);
					stats.setStat(p, sleepStat, Math.max(0, value-500));
					PlayerLookup.tracking(p).forEach(track -> MinorEntityEffectParticlePacket.send(track, AERIAL_SPIRIT.getColor(), p.getX(), p.getY()+ p.getHeight() / 2f, p.getZ()));
				});
			}
		};
	}
}

package dev.sterner.malum.common.spiritrite;

import dev.sterner.lodestone.handlers.WorldEventHandler;
import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import dev.sterner.malum.common.block.blight.BlightedSoilBlock;
import dev.sterner.malum.common.blockentity.spirit_altar.IAltarProvider;
import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.block.BlockSparkleParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.block.blight.BlightMistParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.block.blight.BlightTransformItemParticlePacket;
import dev.sterner.malum.common.recipe.SpiritTransmutationRecipe;
import dev.sterner.malum.common.spiritrite.effect.MalumRiteEffect;
import dev.sterner.malum.common.event.world.TotemCreatedBlightEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.List;
import java.util.Set;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.ARCANE_SPIRIT;

public class ArcaneRiteType extends MalumRiteType {
	public ArcaneRiteType() {
		super("arcane_rite", "Undirected Rite", "Unchained Rite", ARCANE_SPIRIT, ARCANE_SPIRIT, ARCANE_SPIRIT, ARCANE_SPIRIT, ARCANE_SPIRIT);
	}

	@Override
	public MalumRiteEffect getNaturalRiteEffect() {
		return new MalumRiteEffect() {
			@Override
			public boolean isOneAndDone() {
				return true;
			}

			@SuppressWarnings("ConstantConditions")
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				WorldEventHandler.addWorldEvent(totemBase.getWorld(), new TotemCreatedBlightEvent().setPosition(totemBase.getPos()).setBlightData(1, 4, 4));
			}
		};
	}

	@Override
	public MalumRiteEffect getCorruptedEffect() {
		return new MalumRiteEffect() {

			@Override
			public int getRiteEffectRadius() {
				return (BASE_RADIUS * 2)+1;
			}

			@Override
			public int getRiteEffectTickRate() {
				return BASE_TICK_RATE * 5;
			}

			@Override
			public boolean canAffectBlock(TotemBaseBlockEntity totemBase, Set<Block> filters, BlockState state, BlockPos pos) {
				//The rite looks for blighted soil, then tries to transmute anything above it.
				//We wanna check for filters on the block above the blight, not the actual blight.
				BlockPos actualPos = pos.up();
				return super.canAffectBlock(totemBase, filters, totemBase.getWorld().getBlockState(actualPos), actualPos);
			}


			@SuppressWarnings("ConstantConditions")
			@Override
			public void riteEffect(TotemBaseBlockEntity totemBase) {
				World world = totemBase.getWorld();
				BlockPos pos = totemBase.getPos();
				List<BlockPos> nearbyBlocks = getNearbyBlocks(totemBase, BlightedSoilBlock.class).toList();
				List<ItemEntity> nearbyItems = getNearbyEntities(totemBase, ItemEntity.class, e -> e.getWorld().getBlockState(e.getBlockPos()).getBlock() instanceof BlightedSoilBlock).toList();
				for (ItemEntity itemEntity : nearbyItems) {
					if (!totemBase.cachedFilterInstances.isEmpty() && !totemBase.cachedFilterInstances.stream().map(e -> e.inventory.getStack(0)).anyMatch(i -> i.getItem().equals(itemEntity.getStack().getItem()))) {
						continue;
					}
					var recipe = SpiritTransmutationRecipe.getRecipe(world, itemEntity.getStack());
					if (recipe != null) {
						Vec3d itemPos = itemEntity.getPos();
						world.spawnEntity(new ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, recipe.output.copy()));
						if(world instanceof ServerWorld serverWorld){
							PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(itemEntity.getBlockPos()).getPos()).forEach(track -> BlightTransformItemParticlePacket.send(track, List.of(ARCANE_SPIRIT.identifier),  itemPos.getX(), itemPos.getY(), itemPos.getZ()));
						}
						itemEntity.getStack().decrement(1);
					}
				}
				for (BlockPos p : nearbyBlocks) {
					BlockPos posToTransmute = p.up();
					BlockState stateToTransmute = world.getBlockState(posToTransmute);
					if (world.getBlockEntity(posToTransmute) instanceof IAltarProvider iAltarProvider) {
						LodestoneBlockEntityInventory inventoryForAltar = iAltarProvider.getInventoryForAltar();
						ItemStack stack = inventoryForAltar.getStack(0);
						var recipe = SpiritTransmutationRecipe.getRecipe(world, stack);
						if (recipe != null) {
							Vec3d itemPos = iAltarProvider.getItemPosForAltar();
							world.spawnEntity(new ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, recipe.output.copy()));
							if(world instanceof ServerWorld serverWorld){
								PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(p).getPos()).forEach(track -> BlightTransformItemParticlePacket.send(track,List.of(ARCANE_SPIRIT.identifier), itemPos.getX(), itemPos.getY(), itemPos.getZ()));
							}
							inventoryForAltar.getStack(0).decrement(1);
							BlockHelper.updateAndNotifyState(world, p);
						}
					}
					ItemStack stack = stateToTransmute.getBlock().asItem().getDefaultStack();
					var recipe = SpiritTransmutationRecipe.getRecipe(world, stack);
					if (recipe != null) {
						ItemStack output = recipe.output.copy();
						if (output.getItem() instanceof BlockItem blockItem) {
							Block block = blockItem.getBlock();
							BlockEntity entity = world.getBlockEntity(posToTransmute);
							BlockState newState = BlockHelper.setBlockStateWithExistingProperties(world, posToTransmute, block.getDefaultState(), 3);
							world.syncWorldEvent(2001, posToTransmute, Block.getRawIdFromState(newState));
							if(world instanceof ServerWorld serverWorld){
								PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(posToTransmute).getPos()).forEach(track -> BlockSparkleParticlePacket.send(track, ARCANE_SPIRIT.getColor(), posToTransmute));
								PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(posToTransmute).getPos()).forEach(track -> BlightMistParticlePacket.send(track, posToTransmute));
							}
							if (block instanceof BlockEntityProvider entityBlock) {
								if (entity != null) {
									BlockEntity newEntity = entityBlock.createBlockEntity(pos, newState);
									if (newEntity != null) {
										if (newEntity.getClass().equals(entity.getClass())) {
											world.addBlockEntity(entity);
										}
									}
								}
							}
						}
					}
				}
			}
		};
	}
}

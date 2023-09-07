package dev.sterner.malum.common.spiritrite.effect;


import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.registry.MalumTagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MalumRiteEffect {
	public static final int BASE_RADIUS = 2;
	public static final int BASE_TICK_RATE = 20;

	protected MalumRiteEffect() {
	}

	public abstract void riteEffect(TotemBaseBlockEntity totemBase);

	public boolean isOneAndDone() {
		return false;
	}

	public BlockPos getRiteEffectCenter(TotemBaseBlockEntity totemBase) {
		return totemBase.getPos();
	}

	public int getRiteEffectRadius() {
		return BASE_RADIUS;
	}

	public int getRiteEffectTickRate() {
		return BASE_TICK_RATE;
	}

	public <T extends Entity> Stream<T> getNearbyEntities(TotemBaseBlockEntity totemBase, Class<T> clazz) {
		return new ArrayList<>(totemBase.getWorld().getNonSpectatingEntities(clazz, new Box(getRiteEffectCenter(totemBase)).expand(getRiteEffectRadius()))).stream();
	}

	public <T extends Entity> Stream<T> getNearbyEntities(TotemBaseBlockEntity totemBase, Class<T> clazz, Predicate<T> predicate) {
		return totemBase.getWorld().getNonSpectatingEntities(clazz, new Box(getRiteEffectCenter(totemBase)).expand(getRiteEffectRadius())).stream().filter(predicate);
	}

	public Stream<BlockPos> getNearbyBlocks(TotemBaseBlockEntity totemBase, Class<?> clazz) {
		Set<Block> blockFilters = getBlockFilters(totemBase);
		return BlockHelper.getBlocksStream(getRiteEffectCenter(totemBase), getRiteEffectRadius(), p -> canAffectBlock(totemBase, blockFilters, clazz, totemBase.getWorld().getBlockState(p), p));
	}

	public Stream<BlockPos> getNearbyBlocks(TotemBaseBlockEntity totemBase, Class<?> clazz, int height) {
		Set<Block> blockFilters = getBlockFilters(totemBase);
		int horizontal = getRiteEffectRadius();
		return BlockHelper.getBlocksStream(getRiteEffectCenter(totemBase), horizontal, height, horizontal, p -> canAffectBlock(totemBase, blockFilters, clazz, totemBase.getWorld().getBlockState(p), p));
	}

	public Stream<BlockPos> getBlocksUnderBase(TotemBaseBlockEntity totemBase, Class<?> clazz) {
		Set<Block> blockFilters = getBlockFilters(totemBase);
		int horizontal = getRiteEffectRadius();
		return BlockHelper.getPlaneOfBlocksStream(getRiteEffectCenter(totemBase).down(), horizontal, p -> canAffectBlock(totemBase, blockFilters, clazz, totemBase.getWorld().getBlockState(p), p));
	}

	public Set<Block> getBlockFilters(TotemBaseBlockEntity totemBase) {
		return totemBase.cachedFilterInstances.stream().map(e -> e.inventory.getStack(0)).filter(e -> e.getItem() instanceof BlockItem).map(e -> ((BlockItem) e.getItem()).getBlock()).collect(Collectors.toCollection(HashSet::new));
	}

	public boolean canAffectBlock(TotemBaseBlockEntity totemBase, Set<Block> filters, Class<?> clazz, BlockState state, BlockPos pos) {
		return clazz.isInstance(state.getBlock()) && canAffectBlock(totemBase, filters, state, pos);
	}

	public boolean canAffectBlock(TotemBaseBlockEntity totemBase, Set<Block> filters, BlockState state, BlockPos pos) {
		return (filters.isEmpty() || filters.contains(state.getBlock())) && !state.isIn(MalumTagRegistry.RITE_IMMUNE);
	}
}

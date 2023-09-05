package dev.sterner.malum.common.block;

import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class BuddingNaturalQuartzBlock extends NaturalQuartzBlock {
	public BuddingNaturalQuartzBlock(Settings settings) {
		super(settings);
	}


	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(5) == 0) {
			Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			Block block = null;
			if (canGrowIn(blockState)) {
				block = MalumObjects.SMALL_QUARTZ_BUD;
			} else if (blockState.isOf(MalumObjects.SMALL_QUARTZ_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
				block = MalumObjects.MEDIUM_QUARTZ_BUD;
			} else if (blockState.isOf(MalumObjects.MEDIUM_QUARTZ_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
				block = MalumObjects.LARGE_QUARTZ_BUD;
			} else if (blockState.isOf(MalumObjects.LARGE_QUARTZ_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
				block = MalumObjects.NATURAL_QUARTZ_CLUSTER;
			}

			if (block != null) {
				BlockState blockState2 = block.getDefaultState()
						.with(AmethystClusterBlock.FACING, direction)
						.with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
				world.setBlockState(blockPos, blockState2);
			}
		}
	}

	public static boolean canGrowIn(BlockState state) {
		return state.isAir() || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() == 8;
	}
}

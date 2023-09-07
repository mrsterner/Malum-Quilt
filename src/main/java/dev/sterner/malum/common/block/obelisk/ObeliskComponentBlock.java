package dev.sterner.malum.common.block.obelisk;

import dev.sterner.lodestone.systems.multiblock.MultiBlockComponentBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ObeliskComponentBlock extends MultiBlockComponentBlock {
	public static final VoxelShape SHAPE = makeShape();
	private final Item cloneStack;
	public ObeliskComponentBlock(Settings properties, Item cloneStack) {
		super(properties);
		this.cloneStack = cloneStack;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return super.getPickStack(world, pos, state);
	}

	public static VoxelShape makeShape(){
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 0.5625, 0.8125), BooleanBiFunction.OR);

		return shape;
	}
}

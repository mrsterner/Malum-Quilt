package dev.sterner.malum.common.block.obelisk;

import dev.sterner.lodestone.systems.block.WaterLoggedEntityBlock;
import dev.sterner.malum.common.blockentity.obelisk.ObeliskCoreBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ObeliskCoreBlock<T extends ObeliskCoreBlockEntity> extends WaterLoggedEntityBlock<T> {
	public static final VoxelShape SHAPE = makeShape();

	public ObeliskCoreBlock(Settings properties) {
		super(properties);
	}


	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}


	public static VoxelShape makeShape() {
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0, 0.625, 1, 0.3125, 1), BooleanBiFunction.OR);
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0.1875, 0.1875, 0.8125, 1, 0.8125), BooleanBiFunction.OR);
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0, 0, 1, 0.3125, 0.375), BooleanBiFunction.OR);
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0.625, 0.375, 0.3125, 1), BooleanBiFunction.OR);
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0, 0.375, 0.3125, 0.375), BooleanBiFunction.OR);
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.1875, 0.9375), BooleanBiFunction.OR);

		return shape;
	}
}

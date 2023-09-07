package dev.sterner.malum.common.block.storage;

import dev.sterner.lodestone.systems.block.WaterLoggedEntityBlock;
import dev.sterner.malum.common.blockentity.storage.ItemPedestalBlockEntity;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class ItemPedestalBlock<T extends ItemPedestalBlockEntity> extends WaterLoggedEntityBlock<T> {
    public static final VoxelShape SHAPE = Stream.of(
        Block.createCuboidShape(4, 0, 4, 12, 4, 12),
        Block.createCuboidShape(5, 4, 5, 11, 10, 11),
        Block.createCuboidShape(3, 10, 3, 13, 13, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public ItemPedestalBlock(Settings settings) {
        super(settings);
    }

	@Override
	public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		setBlockEntity((BlockEntityType<T>) MalumBlockEntityRegistry.ITEM_PEDESTAL);
		return super.createBlockEntity(pos, state);
	}

	@Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ItemPedestalBlockEntity pedestal) {
            return ScreenHandler.calculateComparatorOutput(pedestal);
        }
        return 0;
    }

	@Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}

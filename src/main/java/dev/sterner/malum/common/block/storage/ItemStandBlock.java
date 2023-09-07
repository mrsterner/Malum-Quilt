package dev.sterner.malum.common.block.storage;

import dev.sterner.lodestone.systems.block.WaterLoggedEntityBlock;
import dev.sterner.malum.common.blockentity.storage.ItemStandBlockEntity;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("deprecation")
public class ItemStandBlock<T extends ItemStandBlockEntity> extends WaterLoggedEntityBlock<T> {
    public static final DirectionProperty FACING = Properties.FACING;
    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(4.0d, 0.0d, 4.0d, 12.0d, 2.0d, 12.0d);
    private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(4.0d, 14.0d, 4.0d, 12.0d, 16.0d, 12.0d);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(4.0d, 4.0d, 14.0d, 12.0d, 12.0d, 16.0d);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0d, 4.0d, 4.0d, 2.0d, 12.0d, 12.0d);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(4.0d, 4.0d, 0.0d, 12.0d, 12.0d, 2.0d);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.0d, 4.0d, 4.0d, 16.0d, 12.0d, 12.0d);

    public ItemStandBlock(Settings settings) {
        super(settings);
    }

	@Override
	public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		setBlockEntity((BlockEntityType<T>) MalumBlockEntityRegistry.ITEM_STAND);
		return super.createBlockEntity(pos, state);
	}

	@Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return switch (direction) {
            case UP -> UP_SHAPE;
            case DOWN -> DOWN_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
        };
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
		super.appendProperties(builder);
    }

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(FACING, context.getSide());
	}

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}

package dev.sterner.malum.common.block.spirit_crucible;

import com.sammy.lodestone.systems.block.WaterLoggedEntityBlock;
import dev.sterner.malum.common.blockentity.crucible.SpiritCatalyzerCoreBlockEntity;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class SpiritCatalyzerCoreBlock<T extends SpiritCatalyzerCoreBlockEntity> extends WaterLoggedEntityBlock<T> {
    public static final VoxelShape NORTH_SOUTH_SHAPE = makeNorthSouthShape();
    public static final VoxelShape WEST_EAST_SHAPE = makeWestEastShape();

    public SpiritCatalyzerCoreBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH));
    }

	@Override
	public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		setBlockEntity((BlockEntityType<T>) MalumBlockEntityRegistry.SPIRIT_CATALYZER);
		return super.createBlockEntity(pos, state);
	}

	@Override
	public @Nullable <B extends BlockEntity> BlockEntityTicker<B> getTicker(@NotNull World world, @NotNull BlockState state, @NotNull BlockEntityType<B> type) {
		return  (tickerWorld, pos, tickerState, blockEntity) -> {
			if(blockEntity instanceof SpiritCatalyzerCoreBlockEntity be){
				be.tick();
				if(world.isClient()){
					be.clientTick();
				}else{
					be.serverTick();
				}
			}
		};
	}

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SpiritCatalyzerCoreBlockEntity catalyzer) {
            return ScreenHandler.calculateComparatorOutput(catalyzer);
        }
        return 0;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
        super.appendProperties(builder);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> {
                return NORTH_SOUTH_SHAPE;
            }
            case EAST, WEST -> {
                return WEST_EAST_SHAPE;
            }
        }
        return NORTH_SOUTH_SHAPE;
    }

    public static VoxelShape makeNorthSouthShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.25, 0.3125, 0.1875, 0.75, 1, 0.8125), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0.625, 0.375, 0.3125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0, 0.0625, 0.625, 0.1875, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0.3125, 0.3125, 0.25, 1, 0.6875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.75, 0.3125, 0.3125, 0.9375, 1, 0.6875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0, 0.375, 0.375, 0.3125, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0, 0.375, 0.9375, 0.3125, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0, 0.8125, 0.625, 0.1875, 0.9375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0, 0.1875, 0.625, 0.3125, 0.8125), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0, 0.625, 1, 0.3125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0, 0.375, 0.3125, 0.375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0, 0, 1, 0.3125, 0.375), BooleanBiFunction.OR);

        return shape;
    }

    public static VoxelShape makeWestEastShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0.3125, 0.25, 0.8125, 1, 0.75), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0, 0.625, 1, 0.3125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0, 0.375, 0.1875, 0.1875, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0.3125, 0.75, 0.6875, 1, 0.9375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0.3125, 0.0625, 0.6875, 1, 0.25), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0, 0.625, 0.625, 0.3125, 0.9375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0, 0.0625, 0.625, 0.3125, 0.375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 0, 0.375, 0.9375, 0.1875, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0, 0.375, 0.8125, 0.3125, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0, 0, 1, 0.3125, 0.375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0.625, 0.375, 0.3125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0, 0.375, 0.3125, 0.375), BooleanBiFunction.OR);

        return shape;
    }
}

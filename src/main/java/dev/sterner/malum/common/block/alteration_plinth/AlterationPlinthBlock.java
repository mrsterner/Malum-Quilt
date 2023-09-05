package dev.sterner.malum.common.block.alteration_plinth;

import com.sammy.lodestone.systems.block.WaterLoggedEntityBlock;
import dev.sterner.malum.common.blockentity.alteration_plinth.AlterationPlinthBlockEntity;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
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

public class AlterationPlinthBlock <T extends AlterationPlinthBlockEntity> extends WaterLoggedEntityBlock<T> {
    public static final VoxelShape SHAPE = makeShape();
    public static final VoxelShape RENDER_SHAPE = makeRenderShape();

    public AlterationPlinthBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH));
    }

	@Override
	public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		setBlockEntity((BlockEntityType<T>) MalumBlockEntityRegistry.ALTERATION_PLINTH);
		return super.createBlockEntity(pos, state);
	}

	@Override
	public @Nullable <B extends BlockEntity> BlockEntityTicker<B> getTicker(@NotNull World world, @NotNull BlockState state, @NotNull BlockEntityType<B> type) {
		return  (tickerWorld, pos, tickerState, blockEntity) -> {
			if(blockEntity instanceof AlterationPlinthBlockEntity be){
				be.tick();
				if(world.isClient()){
					be.clientTick();
				}else{
					be.serverTick();
				}
			}
		};
	}

	public BlockState getPlacementState(ItemPlacementContext pContext) {
        Direction direction = pContext.getHorizontalPlayerFacing().getOpposite();
        return super.getPlacementState(pContext).with(HORIZONTAL_FACING, direction);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return RENDER_SHAPE;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HORIZONTAL_FACING);
    }

    public static VoxelShape makeShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.25, 0.9375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0.25, 0.1875, 0.8125, 0.625, 0.8125), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.625, 0, 1, 1, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 1, 0, 0.1875, 1.0625, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 1, 0.8125, 0.1875, 1.0625, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 1, 0, 1, 1.0625, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 1, 0.8125, 1, 1.0625, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 0, 0.3125, 1, 0.375, 0.6875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0, 0, 0.6875, 0.375, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0, 0.8125, 0.6875, 0.375, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0.3125, 0.1875, 0.375, 0.6875), BooleanBiFunction.OR);

        return shape;
    }

    public static VoxelShape makeRenderShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.25, 0.9375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0.25, 0.1875, 0.8125, 0.625, 0.8125), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.625, 0, 1, 1, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.125, 0.5625, -0.125, 0.1875, 1.0625, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.125, 0.5625, 0.8125, 0.1875, 1.0625, 1.125), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 0.5625, -0.125, 1.125, 1.0625, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 0.5625, 0.8125, 1.125, 1.0625, 1.125), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 0, 0.3125, 1, 0.375, 0.6875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0, 0, 0.6875, 0.375, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0, 0.8125, 0.6875, 0.375, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0.3125, 0.1875, 0.375, 0.6875), BooleanBiFunction.OR);

        return shape;
    }
}

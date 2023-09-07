package dev.sterner.malum.common.block.fusion_plate;

import dev.sterner.lodestone.systems.multiblock.MultiBlockComponentBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class FusionPlateComponentBlock extends MultiBlockComponentBlock {
    public static final VoxelShape SIDE_NORTH_SHAPE = makeNorthSideShape();
    public static final VoxelShape SIDE_SOUTH_SHAPE = makeSouthSideShape();
    public static final VoxelShape SIDE_EAST_SHAPE = makeEastSideShape();
    public static final VoxelShape SIDE_WEST_SHAPE = makeWestSideShape();
    public static final VoxelShape CORNER_NORTH_SHAPE = makeNorthCornerShape();
    public static final VoxelShape CORNER_SOUTH_SHAPE = makeSouthCornerShape();
    public static final VoxelShape CORNER_EAST_SHAPE = makeEastCornerShape();
    public static final VoxelShape CORNER_WEST_SHAPE = makeWestCornerShape();

    public static final BooleanProperty CORNER = BooleanProperty.of("corner");
    private final Item cloneStack;

    public FusionPlateComponentBlock(Settings settings, Item cloneStack) {
        super(settings);
        this.cloneStack = cloneStack;
        this.setDefaultState(this.getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH).with(CORNER, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(CORNER))
        {
            switch (state.get(HORIZONTAL_FACING))
            {
                case NORTH -> {
                    return CORNER_NORTH_SHAPE;
                }
                case SOUTH -> {
                    return CORNER_SOUTH_SHAPE;
                }
                case EAST -> {
                    return CORNER_EAST_SHAPE;
                }
                case WEST -> {
                    return CORNER_WEST_SHAPE;
                }
            }
        }
        else
        {
            switch (state.get(HORIZONTAL_FACING))
            {
                case NORTH -> {
                    return SIDE_NORTH_SHAPE;
                }
                case SOUTH -> {
                    return SIDE_SOUTH_SHAPE;
                }
                case EAST -> {
                    return SIDE_EAST_SHAPE;
                }
                case WEST -> {
                    return SIDE_WEST_SHAPE;
                }
            }
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return cloneStack.getDefaultStack();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, CORNER);
    }

    public static VoxelShape makeNorthSideShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.625, 0.625, 1, 0.75, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0.0625, 1, 0.125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.125, 0.375, 0.1875, 0.75, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 0.125, 0.375, 1, 0.75, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0.125, 0.25, 0.8125, 0.625, 0.625), BooleanBiFunction.OR);

        return shape;
    }
    public static VoxelShape makeSouthSideShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.625, 0, 1, 0.75, 0.375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 0.9375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.8125, 0.125, 0.375, 1, 0.75, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.125, 0.375, 0.1875, 0.75, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0.125, 0.375, 0.8125, 0.625, 0.75), BooleanBiFunction.OR);

        return shape;
    }
    public static VoxelShape makeEastSideShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.625, 0, 0.375, 0.75, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0, 0.9375, 0.125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0.125, 0, 0.625, 0.75, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0.125, 0.8125, 0.625, 0.75, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0.125, 0.1875, 0.75, 0.625, 0.8125), BooleanBiFunction.OR);

        return shape;
    }
    public static VoxelShape makeWestSideShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0.625, 0, 1, 0.75, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0, 0, 1, 0.125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0.125, 0.8125, 0.625, 0.75, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0.125, 0, 0.625, 0.75, 0.1875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.25, 0.125, 0.1875, 0.625, 0.625, 0.8125), BooleanBiFunction.OR);

        return shape;
    }
    public static VoxelShape makeNorthCornerShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.625, 0.625, 0.375, 0.75, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.125, 0.3125, 0.6875, 0.625, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0.1875, 0.8125, 0.125, 1), BooleanBiFunction.OR);

        return shape;
    }
    public static VoxelShape makeSouthCornerShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0.625, 0, 1, 0.75, 0.375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0.125, 0, 1, 0.625, 0.6875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0, 0, 1, 0.125, 0.8125), BooleanBiFunction.OR);

        return shape;
    }
    public static VoxelShape makeEastCornerShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.625, 0, 0.375, 0.75, 0.375), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.125, 0, 0.6875, 0.625, 0.6875), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, 0, 0.8125, 0.125, 0.8125), BooleanBiFunction.OR);

        return shape;
    }
    public static VoxelShape makeWestCornerShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0.625, 0.625, 1, 0.75, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0.125, 0.3125, 1, 0.625, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0, 0.1875, 1, 0.125, 1), BooleanBiFunction.OR);

        return shape;
    }
}

package dev.sterner.malum.common.block.spirit_crucible;

import dev.sterner.lodestone.systems.multiblock.MultiBlockComponentBlock;
import dev.sterner.lodestone.systems.multiblock.MultiBlockComponentEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static dev.sterner.malum.common.block.spirit_crucible.SpiritCatalyzerCoreBlock.makeNorthSouthShape;
import static dev.sterner.malum.common.block.spirit_crucible.SpiritCatalyzerCoreBlock.makeWestEastShape;
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class SpiritCatalyzerComponentBlock extends MultiBlockComponentBlock {
    public static final VoxelShape NORTH_SOUTH_SHAPE = makeNorthSouthShape();
    public static final VoxelShape WEST_EAST_SHAPE = makeWestEastShape();

    private final Item cloneStack;

    public SpiritCatalyzerComponentBlock(Settings settings, Item cloneStack) {
        super(settings);
        this.cloneStack = cloneStack;
        this.setDefaultState(this.getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return super.getPickStack(world, pos, state);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof MultiBlockComponentEntity component) {
            return ScreenHandler.calculateComparatorOutput(component);
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
}

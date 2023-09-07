package dev.sterner.malum.common.block.spirit_altar;

import dev.sterner.lodestone.systems.block.WaterLoggedEntityBlock;
import dev.sterner.malum.common.blockentity.spirit_altar.SpiritAltarBlockEntity;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("deprecation")
public class SpiritAltarBlock<T extends SpiritAltarBlockEntity> extends WaterLoggedEntityBlock<T>{
    public static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(1.0d, 0d, 1.0d, 15.0d, 4.0d, 15.0d),
        Block.createCuboidShape(3.0d, 4.0d, 3.0d, 13.0d, 10.0d, 13.0d),
        Block.createCuboidShape(0d, 10.0d, 0d, 16.0d, 16.0d, 16.0d),
        Block.createCuboidShape(-2.0d, 9.0d, -2.0d, 3.0d, 17.0d, 3.0d),
        Block.createCuboidShape(-2.0d, 9.0d, 13.0d, 3.0d, 17.0d, 18.0d),
        Block.createCuboidShape(13.0d, 9.0d, -2.0d, 18.0d, 17.0d, 3.0d),
        Block.createCuboidShape(13.0d, 9.0d, 13.0d, 18.0d, 17.0d, 18.0d),
        Block.createCuboidShape(13.0d, 0d, 5.0d, 16.0d, 6.0d, 11.0d),
        Block.createCuboidShape(5.0d, 0d, 0d, 11.0d, 6.0d, 3.0d),
        Block.createCuboidShape(5.0d, 0d, 13.0d, 11.0d, 6.0d, 16.0d),
        Block.createCuboidShape(0d, 0d, 5.0d, 3.0d, 6.0d, 11.0d)
    );

    public SpiritAltarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

	@Override
	public @Nullable <B extends BlockEntity> BlockEntityTicker<B> getTicker(@NotNull World world, @NotNull BlockState state, @NotNull BlockEntityType<B> type) {
		return  (tickerWorld, pos, tickerState, blockEntity) -> {
			if(blockEntity instanceof SpiritAltarBlockEntity be){
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
	public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		setBlockEntity((BlockEntityType<T>) MalumBlockEntityRegistry.SPIRIT_ALTAR);
		return super.createBlockEntity(pos, state);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}

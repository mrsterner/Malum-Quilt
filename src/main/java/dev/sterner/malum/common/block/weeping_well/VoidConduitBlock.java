package dev.sterner.malum.common.block.weeping_well;

import dev.sterner.lodestone.systems.block.LodestoneEntityBlock;
import dev.sterner.malum.common.blockentity.VoidConduitBlockEntity;
import dev.sterner.malum.common.component.MalumComponents;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.sterner.malum.common.block.weeping_well.PrimordialSoupBlock.TOP_SHAPE;

public class VoidConduitBlock<T extends VoidConduitBlockEntity> extends LodestoneEntityBlock<T> {
	public VoidConduitBlock(AbstractBlock.Settings properties) {
		super(properties);
	}

	@Override
	public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		setBlockEntity((BlockEntityType<T>) MalumBlockEntityRegistry.VOID_CONDUIT);
		return super.createBlockEntity(pos, state);
	}


	@Override
	public @Nullable <B extends BlockEntity> BlockEntityTicker<B> getTicker(@NotNull World world, @NotNull BlockState state, @NotNull BlockEntityType<B> type) {
		return  (tickerWorld, pos, tickerState, blockEntity) -> {
			if(blockEntity instanceof VoidConduitBlockEntity be){
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
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return TOP_SHAPE;
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.getBlock() instanceof PrimordialSoupBlock || super.isSideInvisible(state, stateFrom, direction);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityCollision(@NotNull BlockState pState, @NotNull World pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {
		if (pEntity instanceof LivingEntity livingEntity) {
			MalumComponents.TOUCH_OF_DARKNESS_COMPONENT.get(livingEntity).touchedByGoop(pState, livingEntity);
		}
	}
}

package dev.sterner.malum.common.block.storage;

import dev.sterner.lodestone.forge.ItemHandlerHelper;
import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.lodestone.systems.block.WaterLoggedEntityBlock;
import dev.sterner.malum.common.blockentity.storage.SpiritJarBlockEntity;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class SpiritJarBlock<T extends SpiritJarBlockEntity> extends WaterLoggedEntityBlock<T> {
	public static final VoxelShape SHAPE = VoxelShapes.union(Block.createCuboidShape(5.5d, -0.5d, 5.5d, 10.5d, 2.0d, 10.5d),
			Block.createCuboidShape(2.5d, 0.5d, 2.5d, 13.5d, 13.5d, 13.5d),
			Block.createCuboidShape(4.5d, 13.5d, 4.5d, 11.5d, 14.5d, 11.5d),
			Block.createCuboidShape(3.5d, 14.5d, 3.5d, 12.5d, 16.5d, 12.5d));
	public SpiritJarBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		setBlockEntity((BlockEntityType<T>) MalumBlockEntityRegistry.SPIRIT_JAR);
		return super.createBlockEntity(pos, state);
	}

	@Override
	public @Nullable <B extends BlockEntity> BlockEntityTicker<B> getTicker(@NotNull World world, @NotNull BlockState state, @NotNull BlockEntityType<B> type) {
		return  (tickerWorld, pos, tickerState, blockEntity) -> {
			if(blockEntity instanceof SpiritJarBlockEntity be){
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
	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		handleAttack(world, pos, player);
		super.onBlockBreakStart(state, world, pos, player);
	}

	public boolean handleAttack(World pWorld, BlockPos pPos, PlayerEntity pPlayer) {
		BlockEntity be = pWorld.getBlockEntity(pPos);
		if (be instanceof SpiritJarBlockEntity jar) {
			var jarHandler = jar.inventory.getValueUnsafer();
			ItemStack item = jarHandler.extractItemStack(0, pPlayer.isSneaking() ? 64 : 1, false);

			if (!item.isEmpty()) {
				ItemHandlerHelper.giveItemToPlayer(pPlayer, item, pPlayer.getInventory().selectedSlot);

				if (!pWorld.isClient()) {
					BlockHelper.updateAndNotifyState(pWorld, pPos);
				}
				return true;
			}
		}
		return false;
	}

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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

package dev.sterner.malum.common.block.totem;

import com.sammy.lodestone.systems.block.LodestoneEntityBlock;
import dev.sterner.malum.common.blockentity.totem.TotemPoleBlockEntity;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

@SuppressWarnings("deprecation")
public class TotemPoleBlock<T extends TotemPoleBlockEntity> extends LodestoneEntityBlock<T> {
    public final Block logBlock;
    public final boolean corrupted;
    public TotemPoleBlock(Block logBlock, AbstractBlock.Settings settings, boolean corrupted) {
        super(settings);
        this.logBlock = logBlock;
        this.corrupted = corrupted;
        this.setDefaultState(this.getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH));
    }

	@Override
	public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		setBlockEntity((BlockEntityType<T>) MalumBlockEntityRegistry.TOTEM_POLE);
		return super.createBlockEntity(pos, state);
	}

	@Override
	public @Nullable <B extends BlockEntity> BlockEntityTicker<B> getTicker(@NotNull World world, @NotNull BlockState state, @NotNull BlockEntityType<B> type) {
		return  (tickerWorld, pos, tickerState, blockEntity) -> {
			if(blockEntity instanceof TotemPoleBlockEntity be){
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
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return logBlock.getPickStack(world, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, MalumSpiritTypeRegistry.SPIRIT_TYPE_PROPERTY);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TotemPoleBlockEntity pole) {
            return Math.min(MalumSpiritTypeRegistry.getIndexForSpiritType(pole.type) + 1, 15);
        }
        return 0;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

	public void onStrip(BlockState blockState, World world, BlockPos blockPos) {

	}
}

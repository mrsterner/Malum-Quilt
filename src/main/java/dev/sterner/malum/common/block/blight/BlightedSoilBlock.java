package dev.sterner.malum.common.block.blight;

import com.sammy.lodestone.systems.worldgen.LodestoneBlockFiller;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.network.packet.s2c.block.blight.BlightMistParticlePacket;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.world.gen.feature.SoulwoodTreeFeature;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BlightedSoilBlock extends Block implements Fertilizable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public BlightedSoilBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld) {
            ItemStack itemInHand = player.getStackInHand(hand);
            Item item = itemInHand.getItem();
            if (item instanceof MalumSpiritItem) {
                if (!player.isCreative()) {
                    itemInHand.decrement(1);
                }
                world.syncWorldEvent(1505, pos, 0);
                grow(serverWorld, world.random, pos, state);
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		world.playSound(null, pos, MalumSoundRegistry.MAJOR_BLIGHT_MOTIF, SoundCategory.BLOCKS, 0.8f, 0.8f);
		world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.2f, 0.8f);
		LodestoneBlockFiller filler = new LodestoneBlockFiller(false);
		SoulwoodTreeFeature.generateBlight(world, filler, pos, 4);

		filler.getEntries().entrySet().stream().filter(e -> e.getValue().getState().getBlock() instanceof BlightedSoilBlock)
			.forEach(p -> PlayerLookup.tracking(world, world.getWorldChunk(pos).getPos()).forEach(track -> BlightMistParticlePacket.send(track, pos)));
	}

}

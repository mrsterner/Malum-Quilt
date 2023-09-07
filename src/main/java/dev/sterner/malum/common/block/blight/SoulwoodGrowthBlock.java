package dev.sterner.malum.common.block.blight;

import dev.sterner.malum.common.block.BlockTagRegistry;
import dev.sterner.malum.common.block.MalumSaplingBlock;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SoulwoodGrowthBlock extends MalumSaplingBlock {
	public SoulwoodGrowthBlock(SaplingGenerator generator, Settings settings) {
		super(generator, settings);
	}

	@Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        super.grow(world, random, pos, state);
        world.syncWorldEvent(1505, pos, 0);
        world.playSound(null, pos, MalumSoundRegistry.MINOR_BLIGHT_MOTIF, SoundCategory.BLOCKS, 1, 0.9f + world.random.nextFloat() * 0.25f);
        world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1, 0.9f + world.random.nextFloat() * 0.25f);
    }

    @Override
	public boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        if (floor.isIn(BlockTagRegistry.BLIGHTED_BLOCKS)) {
            return true;
        }
        return super.canPlantOnTop(floor, world, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemInHand = player.getStackInHand(hand);
        if (itemInHand.getItem() instanceof MalumSpiritItem) {
            if (world instanceof ServerWorld serverWorld) {
                grow(serverWorld, world.random, pos, state);
            }
            if (!player.isCreative()) {
                itemInHand.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}

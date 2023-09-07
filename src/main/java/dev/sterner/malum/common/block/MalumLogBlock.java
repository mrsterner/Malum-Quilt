package dev.sterner.malum.common.block;

import dev.sterner.lodestone.systems.block.LodestoneLogBlock;
import dev.sterner.malum.common.blockentity.totem.TotemPoleBlockEntity;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MalumLogBlock extends LodestoneLogBlock {
    private final boolean isCorrupt;

    public MalumLogBlock(Block stripped, Settings settings,boolean isCorrupt) {
        super(stripped, settings);
        this.isCorrupt = isCorrupt;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof MalumSpiritItem item) {
            if (hit.getSide().equals(Direction.UP) || hit.getSide().equals(Direction.DOWN)) {
                return ActionResult.FAIL;
            }
            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }
            boolean success = createTotemPole(world, pos, player, hand, hit, stack, item);
            if (success) {
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public boolean createTotemPole(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, ItemStack stack, MalumSpiritItem spirit) {
        world.setBlockState(pos, spirit.type.getBlockState(isCorrupt, hit));
        if (world.getBlockEntity(pos) instanceof TotemPoleBlockEntity blockEntity) {
            blockEntity.create(spirit.type);
        }
        if (!player.isCreative()) {
            stack.decrement(1);
        }
        world.syncWorldEvent(2001, pos, Block.getRawIdFromState(world.getBlockState(pos)));
        player.swingHand(hand, true);
        return true;
    }
}

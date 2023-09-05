package dev.sterner.malum.common.block;

import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.awt.*;

public class MalumLeavesBlock extends LeavesBlock {
    public static final Random RANDOM = Random.create();
    public static final IntProperty COLOR = IntProperty.of("color", 0, 4);
    public final Color maxColor;
    public final Color minColor;

    public MalumLeavesBlock(Settings settings, Color maxColor, Color minColor) {
        super(settings);
        this.maxColor = maxColor;
        this.minColor = minColor;
        this.setDefaultState(this.getDefaultState().with(COLOR, 0));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COLOR);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(COLOR, 0);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).getItem().equals(MalumObjects.INFERNAL_SPIRIT)) {
            world.setBlockState(pos, state.with(COLOR, (state.get(COLOR) + 1) % 5));
            player.swingHand(hand);
            player.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1F, 1.5f + RANDOM.nextFloat() * 0.5f);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}

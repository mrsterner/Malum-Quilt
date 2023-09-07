package dev.sterner.malum.common.item.ether;

import dev.sterner.lodestone.setup.LodestoneScreenParticleRegistry;
import dev.sterner.lodestone.systems.easing.Easing;
import dev.sterner.lodestone.systems.particle.ScreenParticleBuilder;
import dev.sterner.lodestone.systems.particle.data.ColorParticleData;
import dev.sterner.lodestone.systems.particle.data.GenericParticleData;
import dev.sterner.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.lodestone.systems.particle.screen.LodestoneScreenParticleTextureSheet;
import dev.sterner.lodestone.systems.particle.screen.ScreenParticleHolder;
import dev.sterner.lodestone.systems.particle.screen.base.ScreenParticle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EtherTorchItem extends AbstractEtherItem {
    protected final Block wallBlock;

    public EtherTorchItem(Block blockIn, Block wallBlockIn, Settings builder, boolean iridescent) {
        super(blockIn, builder, iridescent);
        this.wallBlock = wallBlockIn;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockstate = this.wallBlock.getPlacementState(context);
        BlockState blockstate1 = null;
        World world = context.getWorld();
        BlockPos blockpos = context.getBlockPos();

        for (Direction direction : context.getPlacementDirections()) {
            if (direction != Direction.UP) {
                BlockState blockstate2 = direction == Direction.DOWN ? this.getBlock().getPlacementState(context) : blockstate;
                if (blockstate2 != null && blockstate2.canPlaceAt(world, blockpos)) {
                    blockstate1 = blockstate2;
                    break;
                }
            }
        }

        return blockstate1 != null && world.canPlace(blockstate1, blockpos, ShapeContext.absent()) ? blockstate1 : null;
    }

    public void appendBlocks(Map<Block, Item> blockToItemMap, Item itemIn) {
        super.appendBlocks(blockToItemMap, itemIn);
        blockToItemMap.put(this.wallBlock, itemIn);
    }


	@Override
	public void spawnLateParticles(ScreenParticleHolder target, World world, float partialTick, ItemStack stack, float x, float y) {
		float gameTime = world.getTime() + partialTick;
		AbstractEtherItem etherItem = (AbstractEtherItem) stack.getItem();
		Color firstColor = new Color(etherItem.getFirstColor(stack));
		Color secondColor = new Color(etherItem.getSecondColor(stack));
		float alphaMultiplier = etherItem.iridescent ? 0.75f : 0.5f;
		final SpinParticleData.SpinParticleDataBuilder spinDataBuilder = SpinParticleData.create(0, 1).setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT);
		ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.STAR, target)
				.setTransparencyData(GenericParticleData.create(0.11f * alphaMultiplier, 0f).setEasing(Easing.QUINTIC_IN).build())
				.setScaleData(GenericParticleData.create((float) (0.75f + Math.sin(gameTime * 0.05f) * 0.125f), 0).build())
				.setColorData(ColorParticleData.create(firstColor, secondColor).setCoefficient(1.25f).build())
				.setSpinData(spinDataBuilder.build())
				.setLifetime(7)
				.setRandomOffset(0.05f)
				.spawn(x, y-1)
				.setScaleData(GenericParticleData.create((float) (0.75f - Math.sin(gameTime * 0.075f) * 0.125f), 0).build())
				.setColorData(ColorParticleData.create(secondColor, firstColor).build())
				.setSpinData(spinDataBuilder.setSpinOffset(0.785f - 0.01f * gameTime % 6.28f).build())
				.spawn(x, y-1);
    }
}

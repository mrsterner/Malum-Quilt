package dev.sterner.malum.common.block;


import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.lodestone.helpers.ItemHelper;
import dev.sterner.lodestone.setup.LodestoneParticleRegistry;
import dev.sterner.lodestone.systems.particle.WorldParticleBuilder;
import dev.sterner.lodestone.systems.particle.data.ColorParticleData;
import dev.sterner.lodestone.systems.particle.data.GenericParticleData;
import dev.sterner.lodestone.systems.particle.data.SpinParticleData;
import net.minecraft.block.Block;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.awt.*;

public class SapFilledLogBlock extends PillarBlock {
    public final Block drained;
    public final Item sap;
    public final Color sapColor;

    public SapFilledLogBlock(Settings settings, Block drained, Item sap, Color sapColor) {
        super(settings);
        this.drained = drained;
        this.sap = sap;
        this.sapColor = sapColor;
    }

    public void collectSap(World world, BlockPos pos, PlayerEntity player) {
        world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        ItemHelper.giveItemToEntity(new ItemStack(sap), player);
        if (world.random.nextBoolean()) {
            BlockHelper.setBlockStateWithExistingProperties(world, pos, drained.getDefaultState(), 3);
        }
        if (world.isClient()) {
			WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0.16f, 0f).build())
					.setSpinData(SpinParticleData.create(0.2f).build())
					.setScaleData(GenericParticleData.create(0.2f, 0).build())
					.setColorData(ColorParticleData.create(sapColor, sapColor).build())
					.setLifetime(20)
					.enableNoClip()
					.setRandomOffset(0.1f, 0.1f)
					.setRandomMotion(0.001f, 0.001f)
					.repeatSurroundBlock(world, pos, 8, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

			WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0.08f, 0f).build())
					.setSpinData(SpinParticleData.create(0.1f).build())
					.setScaleData(GenericParticleData.create(0.4f, 0).build())
					.setColorData(ColorParticleData.create(sapColor, sapColor).build())
					.setLifetime(40)
					.setRandomOffset(0.2f)
					.enableNoClip()
					.setRandomMotion(0.001f, 0.001f);
        }
    }
}

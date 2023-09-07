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
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EtherBrazierItem extends AbstractEtherItem{
    public EtherBrazierItem(Block blockIn, Settings builder, boolean iridescent) {
        super(blockIn, builder, iridescent);
    }

	@Override
	public void spawnLateParticles(ScreenParticleHolder target, World world, float partialTick, ItemStack stack, float x, float y) {
		float time = world.getTime() + partialTick;
		AbstractEtherItem etherItem = (AbstractEtherItem) stack.getItem();
		Color firstColor = new Color(etherItem.getFirstColor(stack));
		Color secondColor = new Color(etherItem.getSecondColor(stack));
		float alphaMultiplier = etherItem.iridescent ? 0.75f : 0.5f;
		SpinParticleData.SpinParticleDataBuilder spinDataBuilder = SpinParticleData.create(0, 1).setSpinOffset(0.025f * time % 6.28f).setEasing(Easing.EXPO_IN_OUT);
		ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.STAR, target)
			.setTransparencyData(GenericParticleData.create(0.1f*alphaMultiplier, 0f).setEasing(Easing.QUINTIC_IN).build())
			.setScaleData(GenericParticleData.create((float) (1.3f + Math.sin(time * 0.1f) * 0.125f), 0).build())
			.setColorData(ColorParticleData.create(firstColor, secondColor).setCoefficient(1.25f).build())
			.setLifetime(6)
			.setRandomOffset(0.05f)
			.setSpinData(spinDataBuilder.build())
			.spawn(x-0.5f, y-2)
			.setScaleData(GenericParticleData.create((float) (1.2f - Math.sin(time * 0.075f) * 0.125f), 0).build())
			.setColorData(ColorParticleData.create(secondColor, firstColor).build())
			.setSpinData(spinDataBuilder.setSpinOffset(0.785f-0.01f * time % 6.28f).build())
			.spawn(x-0.5f, y-2);
	}
}

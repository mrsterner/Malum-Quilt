package dev.sterner.malum.common.item.nitrate;

import dev.sterner.lodestone.setup.LodestoneScreenParticleRegistry;
import dev.sterner.lodestone.systems.easing.Easing;
import dev.sterner.lodestone.systems.particle.ScreenParticleBuilder;
import dev.sterner.lodestone.systems.particle.data.ColorParticleData;
import dev.sterner.lodestone.systems.particle.data.GenericParticleData;
import dev.sterner.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.lodestone.systems.particle.screen.LodestoneScreenParticleTextureSheet;
import dev.sterner.lodestone.systems.particle.screen.ScreenParticleHolder;
import dev.sterner.lodestone.systems.particle.screen.base.ScreenParticle;
import dev.sterner.malum.common.entity.nitrate.VividNitrateEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static dev.sterner.malum.common.entity.nitrate.VividNitrateEntity.COLOR_FUNCTION;


public class VividNitrateItem extends AbstractNitrateItem{
    public VividNitrateItem(Settings settings) {
        super(settings, p -> new VividNitrateEntity(p, p.getWorld()));
    }

	@Override
	public void spawnLateParticles(ScreenParticleHolder target, World world, float partialTick, ItemStack stack, float x, float y) {
		float gameTime = (float) (world.getTime() + partialTick + Math.sin(((world.getTime() + partialTick) * 0.1f)));
		Color firstColor = COLOR_FUNCTION.apply(new VividNitrateEntity.ColorFunctionData(world, 40f, 0, partialTick)).brighter();
		Color secondColor = COLOR_FUNCTION.apply(new VividNitrateEntity.ColorFunctionData(world, 40f, 0.125f, partialTick)).darker();
		final SpinParticleData.SpinParticleDataBuilder spinParticleData = SpinParticleData.create(0, 1).setCoefficient(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT);
		ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.STAR, target)
				.setTransparencyData(GenericParticleData.create(0.04f, 0f).setEasing(Easing.QUINTIC_IN).build())
				.setLifetime(6)
				.setScaleData(GenericParticleData.create((float) (1.5f + Math.sin(gameTime * 0.1f) * 0.125f), 0).build())
				.setColorData(ColorParticleData.create(firstColor, secondColor).setCoefficient(1.25f).build())
				.setRandomOffset(0.05f)
				.setSpinData(spinParticleData.build())
				.spawn( - 1,  + 4)
				.setScaleData(GenericParticleData.create((float) (1.4f - Math.sin(gameTime * 0.075f) * 0.125f), 0).build())
				.setColorData(ColorParticleData.create(secondColor, firstColor).build())
				.setSpinData(spinParticleData.setSpinOffset(0.785f - 0.01f * gameTime % 6.28f).build())
				.spawn( - 1,  + 4);
	}
}

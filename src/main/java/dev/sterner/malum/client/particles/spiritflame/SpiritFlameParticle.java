package dev.sterner.malum.client.particles.spiritflame;

import com.sammy.lodestone.systems.particle.world.FrameSetParticle;
import com.sammy.lodestone.systems.particle.world.WorldParticleEffect;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.world.ClientWorld;

public class SpiritFlameParticle extends FrameSetParticle {
	public SpiritFlameParticle(ClientWorld world, WorldParticleEffect data, FabricSpriteProviderImpl spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, data, spriteSet, x, y, z, xd, yd, zd);
		addFrames(0, 37);
		addLoop(38, 77, 2);
		setMaxAge(frameSet.size());
	}
	@Override
	public void tick() {
		super.tick();
		velocityX *= 0.9f;
		if (age < 5) {
			velocityY += 0.005f;
		} else {
			velocityY *= 0.9f;
		}
		velocityZ *= 0.9f;
	}

	@Override
	protected int getBrightness(float partialTicks) {
		return 0xF000F0;
	}
}

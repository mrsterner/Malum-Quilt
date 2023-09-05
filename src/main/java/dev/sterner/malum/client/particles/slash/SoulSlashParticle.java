package dev.sterner.malum.client.particles.slash;

import com.sammy.lodestone.systems.particle.world.GenericParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;

public class SoulSlashParticle extends GenericParticle {

	public SoulSlashParticle(ClientWorld world, WorldParticleOptions options, ParticleEngine.MutableSpriteSet spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, options, spriteSet, x, y, z, xd, yd, zd);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
		super.render(consumer, camera, partialTicks);
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return 0xF000F0;
	}
}

package dev.sterner.malum.client.particles.slash;

import dev.sterner.lodestone.systems.particle.world.GenericParticle;
import dev.sterner.lodestone.systems.particle.world.WorldParticleEffect;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;

public class SoulSlashParticle extends GenericParticle {


	public SoulSlashParticle(ClientWorld world, WorldParticleEffect options, FabricSpriteProviderImpl spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, options, spriteSet, x, y, z, xd, yd, zd);
	}

	@Override
	protected int getBrightness(float pPartialTick) {
		return 0xF000F0;
	}
}

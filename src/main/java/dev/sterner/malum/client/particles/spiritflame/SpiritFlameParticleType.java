package dev.sterner.malum.client.particles.spiritflame;

import com.mojang.serialization.Codec;
import com.sammy.lodestone.systems.rendering.particle.world.WorldParticleEffect;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleType;

public class SpiritFlameParticleType extends ParticleType<WorldParticleEffect> {

	public SpiritFlameParticleType() {
		super(false, WorldParticleEffect.DESERIALIZER);
	}

	@Override
	public Codec<WorldParticleEffect> getCodec() {
		return WorldParticleEffect.codecFor(this);
	}

	public record Factory(SpriteProvider sprite) implements ParticleFactory<WorldParticleEffect> {

		@Override
		public Particle createParticle(WorldParticleEffect data, ClientWorld world, double x, double y, double z, double mx, double my, double mz) {
			return new SpiritFlameParticle(world, data, (FabricSpriteProviderImpl) sprite, x, y, z, mx, my, mz);
		}
	}
}

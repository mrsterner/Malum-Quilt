package dev.sterner.malum.client.particles.cut;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class ScytheAttackParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteWithAge;

	public ScytheAttackParticle(ClientWorld level, double x, double y, double z, double scale, SpriteProvider spriteWithAge) {
		super(level, x, y, z, 0.0D, 0.0D, 0.0D);
		this.spriteWithAge = spriteWithAge;
		this.maxAge = 4;
		float f = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = f;
		this.colorGreen = f;
		this.colorBlue = f;
		this.scale = 1.0F - (float) scale * 0.5F;
		this.setSpriteForAge(spriteWithAge);
	}

	public int getLightColor(float partialTick) {
		return 15728880;
	}

	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteWithAge);
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_LIT;
	}



	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteSet;

		public Factory(SpriteProvider spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Nullable
		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new ScytheAttackParticle(world, x, y, z, velocityX, this.spriteSet);
		}
	}
}

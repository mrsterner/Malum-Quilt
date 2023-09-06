package dev.sterner.malum.common.entity.night_terror;

import com.sammy.lodestone.helpers.EntityHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import com.sammy.lodestone.systems.particle.world.LodestoneWorldParticleTextureSheet;
import dev.sterner.malum.client.ParticleEffects;
import dev.sterner.malum.common.registry.MalumEntityRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.math.MathHelper.nextFloat;

public class NightTerrorSeekerEntity extends ThrownEntity {
	public static final Color NIGHT_TERROR_DARK = new Color(37, 25, 56);
	public static final Color NIGHT_TERROR_PURPLE = new Color(179, 52, 208);

	protected float magicDamage;
	public int age;
	public int fadeoutStart;
	public int fadeoutDuration;
	public final ArrayList<EntityHelper.PastPosition> pastPositions = new ArrayList<>(); // *screaming*

	public NightTerrorSeekerEntity(World level) {
		super(MalumEntityRegistry.NIGHT_TERROR, level);
		noClip = false;
		fadeoutStart = MathHelper.nextInt(level.random, 4, 8);
		fadeoutDuration = MathHelper.nextInt(level.random, 18, 24);
	}

	public NightTerrorSeekerEntity(World level, double pX, double pY, double pZ) {
		super(MalumEntityRegistry.NIGHT_TERROR, pX, pY, pZ, level);
		noClip = false;
		fadeoutStart = MathHelper.nextInt(level.random, 6, 10);
		fadeoutDuration = MathHelper.nextInt(level.random, 18, 24);
	}

	public void setData(Entity owner, float magicDamage) {
		setOwner(owner);
		this.magicDamage = magicDamage;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound compound) {
		super.writeCustomDataToNbt(compound);
		if (magicDamage != 0) {
			compound.putFloat("magicDamage", magicDamage);
		}
		if (age != 0) {
			compound.putInt("age", age);
		}
		if (fadeoutStart != 0) {
			compound.putInt("fadeoutStart", fadeoutStart);
		}
		if (fadeoutDuration != 0) {
			compound.putInt("fadeoutDuration", fadeoutDuration);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound compound) {
		super.readCustomDataFromNbt(compound);
		magicDamage = compound.getFloat("magicDamage");
		age = compound.getInt("age");
//        fadeoutStart = compound.getInt("fadeoutStart");
//        fadeoutDuration = compound.getInt("fadeoutDuration");
		setVelocity(0, 0.4f, 0);
	}

	@Override
	protected void onBlockHit(BlockHitResult result) {
		super.onBlockHit(result);
		if (age < fadeoutStart) {
			age = fadeoutStart;
		}
	}

	@Override
	protected boolean canHit(Entity pTarget) {
		return !pTarget.equals(getOwner());
	}

	@Override
	protected void onEntityHit(EntityHitResult result) {
		if (getOwner() instanceof LivingEntity scytheOwner) {
			Entity target = result.getEntity();
			if (getWorld().isClient()) {
				return;
			}
			DamageSource source = DamageSourceRegistry.causeVoodooDamage(scytheOwner);
			target.damage(source, magicDamage);
			if (age < fadeoutStart) {
				fadeoutStart += 4;
			}
			target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), MalumSoundRegistry.SCYTHE_CUT, target.getSoundCategory(), 1.0F, 0.9f + target.level.random.nextFloat() * 0.2f);
		}
		super.onEntityHit(result);
	}
	@Override
	public void tick() {
		super.tick();
		trackPastPositions();
		age++;
		if (getWorld().isClient()) {
			ClientOnly.spawnParticles(this);
		}
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			Vec3d motion = getVelocity();
			setYaw((float) (MathHelper.atan2(motion.x, motion.z) * (double) (180F / (float) Math.PI)));
			prevYaw = getYaw();
			prevPitch = getPitch();
		}
		if (!getWorld().isClient()) {
			if (age > fadeoutStart) {
				Vec3d motion = getVelocity().multiply(0.92f);
				setVelocity(motion);
				if (age - fadeoutStart > fadeoutDuration) {
					discard();
				}
			}
		}
	}

	public void trackPastPositions() {
		EntityHelper.trackPastPositions(pastPositions, getPos(), 0.01f);
		removeOldPositions(pastPositions);
	}

	public void removeOldPositions(List<EntityHelper.PastPosition> pastPositions) {
		int amount = pastPositions.size() - 1;
		List<EntityHelper.PastPosition> toRemove = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			EntityHelper.PastPosition excess = pastPositions.get(i);
			if (excess.time > 12) {
				toRemove.add(excess);
			}
		}
		pastPositions.removeAll(toRemove);
	}

	public void shootFromRotation(Entity shooter, float rotationPitch, float rotationYaw, float pitchOffset, float velocity, float innacuracy) {
		float f = -MathHelper.sin(rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(rotationPitch * ((float) Math.PI / 180F));
		float f1 = -MathHelper.sin((rotationPitch + pitchOffset) * ((float) Math.PI / 180F));
		float f2 = MathHelper.cos(rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(rotationPitch * ((float) Math.PI / 180F));
		this.setVelocity(f, f1, f2, velocity, innacuracy);
		Vec3d vec3 = shooter.getVelocity();
		this.setVelocity(this.getVelocity().add(vec3.x, 0, vec3.z));
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	@Override
	protected void initDataTracker() {

	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	public float getTargetingMargin() {
		return 4f;
	}

	@Override
	public boolean isFireImmune() {
		return true;
	}

	@Override
	public boolean isImmuneToExplosion() {
		return true;
	}

	public static class ClientOnly {
		public static void spawnParticles(NightTerrorSeekerEntity nightTerrorSeekerEntity) {
			double ox = nightTerrorSeekerEntity.prevX, oy = nightTerrorSeekerEntity.prevY + 0.25f, oz = nightTerrorSeekerEntity.prevZ;
			double x = nightTerrorSeekerEntity.getX(), y = nightTerrorSeekerEntity.getY() + 0.25f, z = nightTerrorSeekerEntity.getZ();
			Vec3d motion = nightTerrorSeekerEntity.getVelocity();
			Vec3d norm = motion.normalize().multiply(0.1f);
			float extraAlpha = (float) motion.length();
			float cycles = 3;
			Color firstColor = NIGHT_TERROR_PURPLE.brighter();
			Random rand = nightTerrorSeekerEntity.getWorld().getRandom();
			for (int i = 0; i < cycles; i++) {
				float pDelta = i / cycles;
				double lerpX = MathHelper.lerp(pDelta, ox, x) + motion.x / 4f;
				double lerpY = MathHelper.lerp(pDelta, oy, y) + motion.y / 4f;
				double lerpZ = MathHelper.lerp(pDelta, oz, z) + motion.z / 4f;
				float alphaMultiplier = (0.35f + extraAlpha) * Math.min(1, nightTerrorSeekerEntity.age * 0.2f);
				ParticleEffects.spawnSpiritParticles(nightTerrorSeekerEntity.getWorld(), lerpX, lerpY, lerpZ, alphaMultiplier*0.8f, norm, firstColor, firstColor.darker());

				final ColorParticleData.ColorParticleDataBuilder colorDataBuilder = ColorParticleData.create(NIGHT_TERROR_DARK, NIGHT_TERROR_DARK)
					.setEasing(Easing.QUINTIC_OUT)
					.setCoefficient(1.25f);
				WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
					.setTransparencyData(GenericParticleData.create(Math.min(1, 0.25f * alphaMultiplier), 0f).setEasing(Easing.SINE_IN, Easing.SINE_OUT).build())
					.setLifetime(15 + rand.nextInt(15))
					.setSpinData(SpinParticleData.create(nextFloat(rand, -0.1f, 0.1f)).setSpinOffset(rand.nextFloat() * 6.28f).build())
					.setScaleData(GenericParticleData.create(0.25f + rand.nextFloat() * 0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN, Easing.SINE_OUT).build())
					.setColorData(colorDataBuilder.build())
					.setRandomOffset(0.02f)
					.enableNoClip()
					.addMotion(norm.x, norm.y, norm.z)
					.setRandomMotion(0.01f, 0.01f)
					.setRenderType(LodestoneWorldParticleTextureSheet.TRANSPARENT)
					.spawn(nightTerrorSeekerEntity.getWorld(), lerpX, lerpY, lerpZ)
					.setColorData(colorDataBuilder.setCoefficient(2f).build())
					.spawn(nightTerrorSeekerEntity.getWorld(), lerpX, lerpY, lerpZ);
			}
		}
	}
}

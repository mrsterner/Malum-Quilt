package dev.sterner.malum.common.entity.nitrate;

import com.sammy.lodestone.helpers.ColorHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.SimpleParticleEffect;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import com.sammy.lodestone.systems.particle.world.LodestoneWorldParticleTextureSheet;
import dev.sterner.malum.client.CommonParticleEffects;
import dev.sterner.malum.common.network.packet.s2c.entity.VividNitrateBounceParticlePacket;
import dev.sterner.malum.common.registry.MalumEntityRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.minecraft.util.math.MathHelper.nextFloat;

//TODO update
public class VividNitrateEntity extends AbstractNitrateEntity {

    public static final List<Color> COLORS = new ArrayList<>(List.of(
        new Color(231, 58, 73),
        new Color(246, 125, 70),
        new Color(249, 206, 77),
        new Color(48, 242, 71),
        new Color(48, 208, 242),
        new Color(59, 48, 242),
        new Color(145, 42, 247),
        new Color(231, 58, 73)));

    public static final Function<ColorFunctionData, Color> COLOR_FUNCTION = f -> {
        float time = ((f.world.getTime() + f.partialTicks) % f.duration) / f.duration;
        float lerp = time + f.offset;
        if (lerp > 1) {
            lerp -= Math.floor(lerp);
        }
        return ColorHelper.multicolorLerp(Easing.SINE_IN, lerp, VividNitrateEntity.COLORS);
    };


    public VividNitrateEntity(World world) {
        super(MalumEntityRegistry.VIVID_NITRATE, world);
    }

    public VividNitrateEntity(double x, double y, double z, World world) {
        super(MalumEntityRegistry.VIVID_NITRATE, x, y, z, world);
    }

    public VividNitrateEntity(LivingEntity owner, World world) {
        super(MalumEntityRegistry.VIVID_NITRATE, owner, world);
    }

    @Override
    public float getExplosionRadius() {
        return 3f;
    }

    @Override
    public int getPierce() {
        return 20;
    }

    @Override
    public void onExplode() {
        Vec3d deltaMovement = getVelocity();
        var random = getWorld().random;
        double x = MathHelper.lerp(0.5f, deltaMovement.x, Math.min(2, (0.9f + random.nextFloat() * 0.3f) * (random.nextBoolean() ? -deltaMovement.x : deltaMovement.x) + 0.45f - random.nextFloat() * 0.9f));
        double y = MathHelper.lerp(0.5f, deltaMovement.y, Math.min(2, (deltaMovement.y * (0.7f + random.nextFloat() * 0.3f) + 0.2f + random.nextFloat() * 0.4f)));
        double z = MathHelper.lerp(0.5f, deltaMovement.z, Math.min(2, (0.9f + random.nextFloat() * 0.3f) * (random.nextBoolean() ? -deltaMovement.z : deltaMovement.z) + 0.45f - random.nextFloat() * 0.9f));
        if (random.nextFloat() < 0.2f) {
            x *= 1.5f + random.nextFloat() * 0.5f;
            y += 0.25f + random.nextFloat() * 0.5f;
            z *= 1.5f + random.nextFloat() * 0.5f;
        }
        y = Math.min(y, 1f);
        setVelocity(x, y, z);
        if (getWorld() instanceof ServerWorld serverWorld) {
            PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(getBlockPos()).getPos()).forEach(track -> VividNitrateBounceParticlePacket.send(track, COLOR_FUNCTION.apply(new ColorFunctionData(serverWorld, 0f)), getX(), getY(), getZ()));

        }
    }

    @Override
    public void spawnParticles() {
        double ox = prevX, oy = prevY + getYOffset(0) + 0.25f, oz = prevZ;
        double x = getX(), y = getY() + getYOffset(0) + 0.25f, z = getZ();
        Vec3d motion = getVelocity();
        Vec3d norm = motion.normalize().multiply(0.1f);
        float extraAlpha = (float) motion.length();
        float cycles = 3;
        Color firstColor = COLOR_FUNCTION.apply(new ColorFunctionData(getWorld(), 0f)).brighter();
        Color secondColor = COLOR_FUNCTION.apply(new ColorFunctionData(getWorld(), 0.125f)).darker();
        var rand = getWorld().getRandom();
        for (int i = 0; i < cycles; i++) {
            float pDelta = i / cycles;
            double lerpX = MathHelper.lerp(pDelta, ox, x) - motion.x / 4f;
            double lerpY = MathHelper.lerp(pDelta, oy, y) - motion.y / 4f;
            double lerpZ = MathHelper.lerp(pDelta, oz, z) - motion.z / 4f;
            float alphaMultiplier = (0.30f + extraAlpha) * Math.min(1, windUp * 2);
			CommonParticleEffects.spawnSpiritParticles(getWorld(), lerpX, lerpY, lerpZ, alphaMultiplier + 0.1f, norm, firstColor, secondColor);
			final ColorParticleData.ColorParticleDataBuilder colorDataBuilder = ColorParticleData.create(secondColor, SECOND_SMOKE_COLOR).setEasing(Easing.SINE_OUT).setCoefficient(2.25f);
			WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
					.setTransparencyData(GenericParticleData.create(Math.min(1, 0.1f * alphaMultiplier), 0f).setEasing(Easing.SINE_IN, Easing.SINE_OUT).build())
					.setLifetime(65 + rand.nextInt(15))
					.setSpinData(SpinParticleData.create(nextFloat(rand, -0.1f, 0.1f)).setSpinOffset(rand.nextFloat() * 6.28f).build())
					.setScaleData(GenericParticleData.create(0.2f + rand.nextFloat() * 0.05f, 0.3f, 0f).build())
					.setColorData(colorDataBuilder.build())
					.setRandomOffset(0.02f)
					.enableNoClip()
					.setRandomMotion(0.01f, 0.01f)
					.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.INVISIBLE)
					.repeat(getWorld(), lerpX, lerpY, lerpZ, 1)
					.setRenderType(LodestoneWorldParticleTextureSheet.TRANSPARENT)
					.setColorData(colorDataBuilder.setCoefficient(2.75f).build())
					.repeat(getWorld(), lerpX, lerpY, lerpZ, 1);
        }
    }

    public record ColorFunctionData(World world, float duration, float offset, float partialTicks){
        public ColorFunctionData(World world, float offset) {
            this(world, 12f, offset, 0);
        }
    }

    public VividNitrateEntity(EntityType<? extends AbstractNitrateEntity> type, World world) {
        super(type, world);
    }
}

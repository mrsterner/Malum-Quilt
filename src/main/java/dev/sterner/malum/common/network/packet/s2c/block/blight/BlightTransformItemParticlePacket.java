package dev.sterner.malum.common.network.packet.s2c.block.blight;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.lodestone.setup.LodestoneParticleRegistry;
import dev.sterner.lodestone.systems.easing.Easing;
import dev.sterner.lodestone.systems.particle.SimpleParticleEffect;
import dev.sterner.lodestone.systems.particle.WorldParticleBuilder;
import dev.sterner.lodestone.systems.particle.data.ColorParticleData;
import dev.sterner.lodestone.systems.particle.data.GenericParticleData;
import dev.sterner.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.lodestone.systems.particle.world.LodestoneWorldParticleTextureSheet;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import dev.sterner.malum.common.spirit.SpiritHelper;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlightTransformItemParticlePacket {
    public static final Identifier ID = new Identifier(Malum.MODID, "blight_transform_particles");

    public static void send(PlayerEntity player, List<String> spirits, double posX, double posY, double posZ) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(spirits.size());
        for (String string : spirits) {
            buf.writeString(string);
        }
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        ServerPlayNetworking.send((ServerPlayerEntity) player, ID, buf);
    }


    public static void handle(MinecraftClient client, ClientPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        int strings = buf.readInt();
        List<String> spirits = new ArrayList<>();
        for (int i = 0; i < strings; i++) {
            spirits.add(buf.readString());
        }
        double posX = buf.readDouble();
        double posY = buf.readDouble();
        double posZ = buf.readDouble();

        ClientWorld world = client.world;
        client.execute(() -> {
            var rand = world.random;
            List<MalumSpiritType> types = new ArrayList<>();
            for (String string : spirits) {
                types.add(SpiritHelper.getSpiritType(string));
            }
            for (MalumSpiritType type : types) {
                Color color = type.getColor();
                for (int i = 0; i < 3; i++) {
                    int spinDirection = (rand.nextBoolean() ? 1 : -1);
                    int spinOffset = rand.nextInt(360);
					WorldParticleBuilder.create(LodestoneParticleRegistry.TWINKLE_PARTICLE)
							.setTransparencyData(GenericParticleData.create(0.4f, 0.8f, 0).build())
							.setLifetime(20)
							.setSpinData(SpinParticleData.create(0.7f * spinDirection, 0).setSpinOffset(spinOffset).setSpinOffset(1.25f).setEasing(Easing.CUBIC_IN).build())
							.setScaleData(GenericParticleData.create(0.075f, 0.15f, 0).setCoefficient(0.8f).setEasing(Easing.QUINTIC_OUT, Easing.EXPO_IN_OUT).build())
							.setColorData(ColorParticleData.create(ColorHelper.brighter(color, 2), color).build())
							.enableNoClip()
							.setRandomOffset(0.6f)
							.setGravity(1.1f)
							.addMotion(0, 0.28f + rand.nextFloat() * 0.15f, 0)
							.disableNoClip()
							.setRandomMotion(0.1f, 0.15f)
							.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
							.repeat(world, posX, posY, posZ, 2);
                }
                int spinOffset = rand.nextInt(360);
                for (int i = 0; i < 3; i++) {
                    int spinDirection = (rand.nextBoolean() ? 1 : -1);
					WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
							.setTransparencyData(GenericParticleData.create(0.12f, 0.06f, 0).setEasing(Easing.SINE_IN, Easing.CIRC_IN).build())
							.setSpinData(SpinParticleData.create((0.125f + rand.nextFloat() * 0.075f) * spinDirection).setSpinOffset(spinOffset).build())
							.setScaleData(GenericParticleData.create(0.85f, 0.5f, 0).setEasing(Easing.EXPO_OUT, Easing.SINE_IN).build())
							.setColorData(ColorParticleData.create(color.brighter(), color.darker()).build())
							.setLifetime(30)
							.setRandomOffset(0.4f)
							.enableNoClip()
							.setRandomMotion(0.01f, 0.01f)
							.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
							.repeat(world, posX, posY, posZ, 5);
                }
            }

            for (int i = 0; i < 3; i++) {
                float multiplier = MathHelper.nextFloat(world.random, 0.4f, 1f);
                float timeMultiplier = MathHelper.nextFloat(world.random, 0.9f, 1.4f);
                Color color = new Color((int)(31*multiplier), (int)(19*multiplier), (int)(31*multiplier));
                boolean spinDirection = world.random.nextBoolean();
				WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.4f, 1f, 0).build())
						.setLifetime((int) (45*timeMultiplier))
						.setSpinData(SpinParticleData.create(0.2f*(spinDirection ? 1 : -1)).build())
						.setScaleData(GenericParticleData.create(0.15f, 0.2f, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
						.setColorData(ColorParticleData.create(color, color).build())
						.enableNoClip()
						.setRandomOffset(0.1f, 0.1f)
						.setRandomMotion(0.01f, 0.02f)
						.addMotion(0, 0.01f, 0)
						.setRenderType(LodestoneWorldParticleTextureSheet.TRANSPARENT)
						.repeat(world, posX, posY, posZ, 2);

				WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.35f, 0.55f, 0).build())
						.setLifetime((int) (50*timeMultiplier))
						.setSpinData(SpinParticleData.create(0.1f*(spinDirection ? 1 : -1)).build())
						.setScaleData(GenericParticleData.create(0.35f, 0.4f, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
						.setColorData(ColorParticleData.create(color, color).build())
						.setRandomOffset(0.2f, 0)
						.enableNoClip()
						.setRandomMotion(0.015f, 0.015f)
						.addMotion(0, 0.01f, 0)
						.setRenderType(LodestoneWorldParticleTextureSheet.TRANSPARENT)
						.repeat(world, posX, posY, posZ, 3);

				color = new Color((int)(80*multiplier), (int)(40*multiplier), (int)(80*multiplier));
				WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.1f, 0.15f, 0).build())
						.setLifetime((int) (50*timeMultiplier))
						.setSpinData(SpinParticleData.create(0.1f*(spinDirection ? 1 : -1)).build())
						.setScaleData(GenericParticleData.create(0.35f, 0.4f, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
						.setColorData(ColorParticleData.create(color, color).build())
						.setRandomOffset(0.2f, 0)
						.enableNoClip()
						.setRandomMotion(0.02f, 0.01f)
						.addMotion(0, 0.01f, 0)
						.repeat(world, posX, posY, posZ, 2);
            }
        });
    }
}

package dev.sterner.malum.common.network.packet.s2c.entity;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.lodestone.setup.LodestoneParticleRegistry;
import dev.sterner.lodestone.systems.easing.Easing;
import dev.sterner.lodestone.systems.particle.WorldParticleBuilder;
import dev.sterner.lodestone.systems.particle.data.ColorParticleData;
import dev.sterner.lodestone.systems.particle.data.GenericParticleData;
import dev.sterner.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.malum.Malum;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.awt.*;

public class MajorEntityEffectParticlePacket {
    public static final Identifier ID = new Identifier(Malum.MODID, "major_effect_particle");

    public static void send(PlayerEntity player, Color color, double posX, double posY, double posZ) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(color.getRed());
        buf.writeInt(color.getGreen());
        buf.writeInt(color.getBlue());
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);

        ServerPlayNetworking.send((ServerPlayerEntity) player, ID, buf);
    }


    public static void handle(MinecraftClient client, ClientPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        Color color = new Color(buf.readInt(), buf.readInt(), buf.readInt());
        double posX = buf.readDouble();
        double posY = buf.readDouble();
        double posZ = buf.readDouble();

        ClientWorld world = client.world;
        client.execute(() -> {
            var rand = world.random;
            for (int i = 0; i <= 3; i++) {
                int spinDirection = (rand.nextBoolean() ? 1 : -1);
				WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0f, 0.125f, 0).build())
						.setSpinData(SpinParticleData.create(0.025f * spinDirection, (0.2f + rand.nextFloat() * 0.05f) * spinDirection, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
						.setScaleData(GenericParticleData.create(0.025f, 0.1f + rand.nextFloat() * 0.075f, 0.35f).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
						.setColorData(ColorParticleData.create(color, color.darker()).build())
						.setLifetime(25)
						.enableNoClip()
						.setRandomOffset(0.2f, 0.2f)
						.addActor(p -> p.setVelocity(p.getVelocity().multiply(0.95f)))
						.setRandomMotion(0.02f)
						.repeat(world, posX, posY, posZ, 8);
            }
			WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0f, 0.06f, 0).build())
					.setSpinData(SpinParticleData.create(0.1f, 0.4f, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
					.setScaleData(GenericParticleData.create(0.15f, 0.4f, 0.35f).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
					.setLifetime(20)
					.setColorData(ColorParticleData.create(color, color.darker()).build())
					.enableNoClip()
					.setRandomOffset(0.05f, 0.05f)
					.setRandomMotion(0.05f)
					.addActor(p -> p.setVelocity(p.getVelocity().multiply(0.5f)))
					.repeat(world, posX, posY, posZ, 12);

			WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0f, 0.06f, 0).build())
					.setSpinData(SpinParticleData.create(0.1f, 0.25f, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
					.setScaleData(GenericParticleData.create(0.15f, 0.45f, 0.35f).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
					.setColorData(ColorParticleData.create(color.darker(), ColorHelper.darker(color, 3)).build())
					.setLifetime(25)
					.enableNoClip()
					.setRandomOffset(0.15f, 0.15f)
					.setRandomMotion(0.015f, 0.015f)
					.addActor(p -> p.setVelocity(p.getVelocity().multiply(0.92f)))
					.repeat(world, posX, posY, posZ, 20);
        });
    }
}

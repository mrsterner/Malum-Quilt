package dev.sterner.malum.common.network.packet.s2c.entity;

import dev.sterner.lodestone.setup.LodestoneParticleRegistry;
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

public class SuccessfulSoulHarvestParticlePacket {
    public static final Identifier ID = new Identifier(Malum.MODID, "successful_soul_harvest_particle");

    public static void send(PlayerEntity player, Color color, Color endColor, double posX, double posY, double posZ) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(color.getRed());
        buf.writeInt(color.getGreen());
        buf.writeInt(color.getBlue());
        buf.writeInt(endColor.getRed());
        buf.writeInt(endColor.getGreen());
        buf.writeInt(endColor.getBlue());
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);

        ServerPlayNetworking.send((ServerPlayerEntity) player, ID, buf);
    }


    public static void handle(MinecraftClient client, ClientPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        Color color = new Color(buf.readInt(), buf.readInt(), buf.readInt());
        Color endColor = new Color(buf.readInt(), buf.readInt(), buf.readInt());
        double posX = buf.readDouble();
        double posY = buf.readDouble();
        double posZ = buf.readDouble();

        ClientWorld world = client.world;
        client.execute(() -> {
			WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
					.setTransparencyData(GenericParticleData.create(1.0f, 0).build())
					.setScaleData(GenericParticleData.create(0.4f, 0).build())
					.setColorData(ColorParticleData.create(color, endColor).build())
					.setSpinData(SpinParticleData.create(0.4f).build())
					.setLifetime(20)
					.setRandomOffset(0.5, 0).setRandomMotion(0, 0.125f)
					.addMotion(0, 0.28f, 0)
					.setGravity(1)
					.repeat(world, posX, posY, posZ, 40);

			WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0.75f, 0).build())
					.setScaleData(GenericParticleData.create(0.2f, 0).build())
					.setColorData(ColorParticleData.create(color, endColor).build())
					.setSpinData(SpinParticleData.create(0.3f).build())
					.setLifetime(40)
					.setRandomOffset(0.5, 0.5).setRandomMotion(0.125f, 0.05)
					.addMotion(0, 0.15f, 0)
					.setGravity(1)
					.repeat(world, posX, posY, posZ, 30);
        });
    }
}

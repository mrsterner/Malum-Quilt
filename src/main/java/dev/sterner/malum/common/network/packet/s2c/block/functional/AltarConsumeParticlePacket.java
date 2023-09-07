package dev.sterner.malum.common.network.packet.s2c.block.functional;

import dev.sterner.lodestone.setup.LodestoneParticleRegistry;
import dev.sterner.lodestone.systems.particle.WorldParticleBuilder;
import dev.sterner.lodestone.systems.particle.data.ColorParticleData;
import dev.sterner.lodestone.systems.particle.data.GenericParticleData;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AltarConsumeParticlePacket {
    public static final Identifier ID = new Identifier(Malum.MODID, "altar_consume_particles");
    protected final ItemStack stack;
    protected final List<String> spirits;
    protected final double posX;
    protected final double posY;
    protected final double posZ;
    protected final double altarPosX;
    protected final double altarPosY;
    protected final double altarPosZ;

    public AltarConsumeParticlePacket(ItemStack stack, List<String> spirits, double posX, double posY, double posZ, double altarPosX, double altarPosY, double altarPosZ) {
        this.stack = stack;
        this.spirits = spirits;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.altarPosX = altarPosX;
        this.altarPosY = altarPosY;
        this.altarPosZ = altarPosZ;
    }

    public static void send(PlayerEntity player, List<String> spirits, double posX, double posY, double posZ, double altarPosX, double altarPosY, double altarPosZ) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(spirits.size());
		for (String string : spirits) {
			buf.writeString(string);
		}
		buf.writeDouble(posX);
		buf.writeDouble(posY);
		buf.writeDouble(posZ);
		buf.writeDouble(altarPosX);
		buf.writeDouble(altarPosY);
		buf.writeDouble(altarPosZ);
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
		double altarPosX = buf.readDouble();
		double altarPosY = buf.readDouble();
		double altarPosZ = buf.readDouble();

        ClientWorld world = client.world;
        client.execute(() -> {
            List<MalumSpiritType> types = new ArrayList<>();
            for (String string : spirits) {
                types.add(SpiritHelper.getSpiritType(string));
            }
            float alpha = 0.1f / types.size();
            for (MalumSpiritType type : types) {
                Color color = type.getColor();
                Color endColor = type.getEndColor();
				WorldParticleBuilder.create(LodestoneParticleRegistry.TWINKLE_PARTICLE)
						.setTransparencyData(GenericParticleData.create(alpha * 2, 0f).build())
						.setScaleData(GenericParticleData.create(0.4f, 0).build())
						.setColorData(ColorParticleData.create(color, endColor).build())
						.setLifetime(60)
						.setRandomOffset(0.25f, 0.1f)
						.setRandomMotion(0.004f, 0.004f)
						.enableNoClip()
						.repeat(world, posX, posY, posZ, 12);

				WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
						.setTransparencyData(GenericParticleData.create(alpha, 0f).build())
						.setScaleData(GenericParticleData.create(0.2f, 0).build())
						.setColorData(ColorParticleData.create(color, endColor).build())
						.setLifetime(30)
						.setRandomOffset(0.05f, 0.05f)
						.setRandomMotion(0.002f, 0.002f)
						.enableNoClip()
						.repeat(world, posX, posY, posZ, 8);

				Vec3d velocity = new Vec3d(posX, posY, posZ).subtract(altarPosX, altarPosY, altarPosZ).normalize().multiply(-0.05f);
				WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
						.setTransparencyData(GenericParticleData.create(alpha, 0f).build())
						.setScaleData(GenericParticleData.create(0.3f, 0).build())
						.setColorData(ColorParticleData.create(color, color.darker()).build())
						.setLifetime(40)
						.setRandomOffset(0.15f)
						.setRandomMotion(0.005f, 0.005f)
						.addMotion(velocity.x, velocity.y, velocity.z)
						.enableNoClip()
						.repeat(world, posX, posY, posZ, 36);
            }
        });
    }
}

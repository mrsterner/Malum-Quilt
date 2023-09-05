package dev.sterner.malum.common.network.packet.s2c.block;

import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.SimpleParticleEffect;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.malum.Malum;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.awt.*;

public class BlockParticlePacket {
    public static final Identifier ID = new Identifier(Malum.MODID, "block_particle");

    public static void send(PlayerEntity player, Color color, BlockPos pos) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(color.getRed());
        buf.writeInt(color.getGreen());
        buf.writeInt(color.getBlue());
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());

        ServerPlayNetworking.send((ServerPlayerEntity) player, ID, buf);
    }


    public static void handle(MinecraftClient client, ClientPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        Color color = new Color(buf.readInt(), buf.readInt(), buf.readInt());
        BlockPos pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

        ClientWorld world = client.world;
        client.execute(() -> {
			WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0.12f, 0.16f, 0).setCoefficient(0.8f).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
					.setSpinData(SpinParticleData.create(0.2f).build())
					.setScaleData(GenericParticleData.create(0.15f, 0.2f, 0.1f).setCoefficient(0.7f).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
					.setColorData(ColorParticleData.create(color, color).build())
					.setLifetime(25)
					.enableNoClip()
					.setRandomOffset(0.1f, 0.1f)
					.setRandomMotion(0.001f, 0.001f)
					.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
					.repeatSurroundBlock(world, pos, 6, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

			WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0.04f, 0.08f, 0).setCoefficient(0.8f).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
					.setSpinData(SpinParticleData.create(0.1f).build())
					.setScaleData(GenericParticleData.create(0.35f, 0.4f, 0.1f).setCoefficient(0.7f).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
					.setColorData(ColorParticleData.create(color, color).build())
					.setLifetime(35)
					.setRandomOffset(0.2f)
					.enableNoClip()
					.setRandomMotion(0.001f, 0.001f)
					.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
					.repeatSurroundBlock(world, pos, 8, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        });
    }
}

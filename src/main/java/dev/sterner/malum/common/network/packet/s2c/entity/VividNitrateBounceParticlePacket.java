package dev.sterner.malum.common.network.packet.s2c.entity;

import com.sammy.lodestone.helpers.ColorHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.SimpleParticleEffect;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
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

public class VividNitrateBounceParticlePacket {
    public static final Identifier ID = new Identifier(Malum.MODID, "vivid_nitrate_bounce_particle");

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
            for (int i = 0; i < 4; i++) {
                int spinDirection = (rand.nextBoolean() ? 1 : -1);
                int spinOffset = rand.nextInt(360);
                float motionMultiplier = (float) (1+Math.pow(rand.nextFloat(), 2));
				WorldParticleBuilder.create(LodestoneParticleRegistry.TWINKLE_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.2f, 0.8f, 0).build())
						.setSpinData(SpinParticleData.create(0.9f * spinDirection, 0).setSpinOffset(spinOffset).setCoefficient(1.25f).setEasing(Easing.CUBIC_IN).build())
						.setScaleData(GenericParticleData.create(0.075f, 0.25f, 0).setCoefficient(0.8f).setEasing(Easing.QUINTIC_OUT, Easing.EXPO_IN_OUT).build())
						.setColorData(ColorParticleData.create(ColorHelper.brighter(color, 2), color).build())
						.setLifetime(15)
						.enableNoClip()
						.setRandomOffset(0.85f)
						.setGravity(1.1f)
						.addMotion(0, 0.3f + rand.nextFloat() * 0.15f * motionMultiplier, 0)
						.disableNoClip()
						.setRandomMotion(0.2f*motionMultiplier, 0.25f*motionMultiplier)
						.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
						.repeat(world, posX, posY, posZ, 4);
            }
            int spinOffset = rand.nextInt(360);
            for (int i = 0; i < 4; i++) {
                int spinDirection = (rand.nextBoolean() ? 1 : -1);
                float scaleMultiplier = (float) (1+Math.pow(rand.nextFloat(), 2));
				WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.4f, 0.08f, 0).setEasing(Easing.SINE_IN, Easing.CIRC_IN_OUT).build())
						.setLifetime(15)
						.setSpinData(SpinParticleData.create((0.125f + rand.nextFloat() * 0.075f) * spinDirection).setSpinOffset(spinOffset).build())
						.setScaleData(GenericParticleData.create(1.4f*scaleMultiplier, 0.6f, 0).setEasing(Easing.EXPO_OUT, Easing.SINE_IN).build())
						.setColorData(ColorParticleData.create(color.brighter(), color.darker()).build())
						.setRandomOffset(0.6f)
						.enableNoClip()
						.setRandomMotion(0.02f, 0.02f)
						.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
						.repeat(world, posX, posY, posZ, 5);
            }
        });
    }
}

package dev.sterner.malum.common.network.packet.s2c.block.blight;

import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import com.sammy.lodestone.systems.particle.world.LodestoneWorldParticleTextureSheet;
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
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class BlightMistParticlePacket {
    public static final Identifier ID = new Identifier(Malum.MODID, "blight_mist_particles");

    public static void send(PlayerEntity player, BlockPos pos) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());

        ServerPlayNetworking.send((ServerPlayerEntity) player, ID, buf);
    }


    public static void handle(MinecraftClient client, ClientPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        BlockPos pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

        ClientWorld world = client.world;
        client.execute(() -> {
            for (int i = 0; i < 3; i++) {
                float multiplier = MathHelper.nextFloat(world.random, 0.4f, 1f);
                float timeMultiplier = MathHelper.nextFloat(world.random, 0.9f, 1.4f);
                Color color = new Color((int)(31*multiplier), (int)(19*multiplier), (int)(31*multiplier));
                boolean spinDirection = world.random.nextBoolean();
				WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.15f, 1f, 0).build())
						.setSpinData(SpinParticleData.create(0.2f*(spinDirection ? 1 : -1)).build())
						.setScaleData(GenericParticleData.create(0.15f, 0.2f, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
						.setLifetime((int) (45*timeMultiplier))
						.setColorData(ColorParticleData.create(color, color).build())
						.enableNoClip()
						.setRandomOffset(0.1f, 0f)
						.setRandomMotion(0.005f, 0.01f)
						.setRenderType(LodestoneWorldParticleTextureSheet.TRANSPARENT)
						.repeatSurroundBlock(world, pos, 2, Direction.UP);

				WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.25f, 0.55f, 0).build())
						.setLifetime((int) (50*timeMultiplier))
						.setSpinData(SpinParticleData.create(0.1f*(spinDirection ? 1 : -1)).build())
						.setScaleData(GenericParticleData.create(0.35f, 0.4f, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
						.setColorData(ColorParticleData.create(color, color).build())
						.setRandomOffset(0.2f, 0)
						.enableNoClip()
						.setRandomMotion(0.005f, 0.005f)
						.setRenderType(LodestoneWorldParticleTextureSheet.TRANSPARENT)
						.repeatSurroundBlock(world, pos, 2, Direction.UP);

                color = new Color((int)(80*multiplier), (int)(40*multiplier), (int)(80*multiplier));
				WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.02f, 0.15f, 0).build())
						.setSpinData(SpinParticleData.create(0.1f*(spinDirection ? 1 : -1)).build())
						.setScaleData(GenericParticleData.create(0.35f, 0.4f, 0).setEasing(Easing.QUINTIC_OUT, Easing.SINE_IN).build())
						.setColorData(ColorParticleData.create(color, color).build())
						.setLifetime((int) (50*timeMultiplier))
						.setRandomOffset(0.2f, 0)
						.enableNoClip()
						.setRandomMotion(0.01f, 0.005f)
						.repeatSurroundBlock(world, pos, 2, Direction.UP);
            }
        });
    }
}

package dev.sterner.malum.common.event.world;

import dev.sterner.lodestone.systems.worldevent.WorldEventInstance;
import dev.sterner.lodestone.systems.worldgen.LodestoneBlockFiller;
import dev.sterner.malum.common.block.blight.BlightedSoilBlock;
import dev.sterner.malum.common.network.packet.s2c.block.blight.BlightMistParticlePacket;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.registry.WorldEventTypes;
import dev.sterner.malum.common.world.gen.feature.SoulwoodTreeFeature;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class ActiveBlightEvent extends WorldEventInstance {
	public int blightTimer, intensity, rate, times;
	public BlockPos sourcePos;
	public Map<Integer, Double> noiseValues;

	public ActiveBlightEvent() {
		super(WorldEventTypes.ACTIVE_BLIGHT);
	}

	public ActiveBlightEvent setBlightData(int intensity, int rate, int times) {
		this.intensity = intensity;
		this.rate = rate;
		this.times = times;
		return this;
	}

	public ActiveBlightEvent setPosition(BlockPos sourcePos) {
		this.sourcePos = sourcePos;
		return this;
	}

	@Override
	public void tick(World world) {
		if (times == 0) {
			end(world);
			return;
		}
		if (blightTimer == 0) {
			blightTimer = rate;
			times--;
			createBlight((ServerWorld) world);
			intensity+=2;
		} else {
			blightTimer--;
		}
	}

	public void createBlight(ServerWorld world) {
		LodestoneBlockFiller filler = new LodestoneBlockFiller(false);
		if (noiseValues == null) {
			noiseValues = SoulwoodTreeFeature.generateBlight(world, filler, sourcePos, intensity);
		} else {
			SoulwoodTreeFeature.generateBlight(world, filler, noiseValues, sourcePos, intensity);
		}
		filler.getEntries().entrySet().stream().filter(e -> e.getValue().getState().getBlock() instanceof BlightedSoilBlock).map(Map.Entry::getKey).forEach(
			p -> PlayerLookup.tracking(world, world.getWorldChunk(p).getPos()).forEach(track -> BlightMistParticlePacket.send(track, p))
		);

		world.playSound(null, sourcePos, MalumSoundRegistry.MAJOR_BLIGHT_MOTIF, SoundCategory.BLOCKS, 1f, 1.8f);
	}
}

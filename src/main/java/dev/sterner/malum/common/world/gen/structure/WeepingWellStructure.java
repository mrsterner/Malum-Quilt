package dev.sterner.malum.common.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sterner.malum.common.registry.MalumStructures;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeepingWellStructure extends Structure {
	public static final Codec<WeepingWellStructure> CODEC = RecordCodecBuilder.<WeepingWellStructure>mapCodec(instance ->
			instance.group(WeepingWellStructure.configCodecBuilder(instance),
					StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
					Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
					Codec.INT.fieldOf("size").forGetter(provider -> provider.size),
					Codec.INT.fieldOf("min_y").forGetter(provider -> provider.min),
					Codec.INT.fieldOf("max_y").forGetter(provider -> provider.max),
					Codec.INT.fieldOf("offset_in_ground").forGetter(provider -> provider.offsetInGround),
					Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
			).apply(instance, WeepingWellStructure::new)).codec();

	private final RegistryEntry<StructurePool> startPool;
	private final Optional<Identifier> startJigsawName;
	private final int size;
	private final int min;
	private final int max;
	private final int offsetInGround;
	private final int maxDistanceFromCenter;

	public WeepingWellStructure(Structure.Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size, int min, int max, int offsetInGround, int maxDistanceFromCenter) {
		super(config);
		this.startPool = startPool;
		this.startJigsawName = startJigsawName;
		this.size = size;
		this.min = min;
		this.max = max;
		this.offsetInGround = offsetInGround;
		this.maxDistanceFromCenter = maxDistanceFromCenter;
	}

	@Override
	protected Optional<StructurePosition> getStructurePosition(Context context) {
		BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 0, context.chunkPos().getStartZ());
		BlockPos validPos = new BlockPos(blockPos.getX(), getValidY(context.chunkGenerator().getColumnSample(blockPos.getX(), blockPos.getZ(), context.world(), context.noiseConfig())), blockPos.getZ());
		if (validPos.getY() != min - 1 && isSufficientlyFlat(context, validPos, 3)) {
			return StructurePoolBasedGenerator.generate(context, this.startPool, this.startJigsawName, this.size, validPos.down(-offsetInGround), false, Optional.empty(), this.maxDistanceFromCenter);
		}
		return Optional.empty();
	}

	public boolean isSufficientlyFlat(Context context, BlockPos origin, int check) {
		List<BlockPos> blockPosList = new ArrayList<>();
		for(int x = -check; x < check; x++){
			for(int z = -check; z < check; z++){
				blockPosList.add(origin.add(x, 0, z));
			}
		}
		int count = 0;
		for(BlockPos pos : blockPosList){
			VerticalBlockSample blockView = context.chunkGenerator().getColumnSample(pos.getX(), pos.getZ(), context.world(), context.noiseConfig());
			if(blockView.getState(pos.getY()).isAir() && !blockView.getState(pos.down().getY()).isAir()){
				count++;
			}
		}
		return count >= check * check * 2;
	}

	public int getValidY(VerticalBlockSample sample){
		int maxLength = 0;
		int currentLength = 0;
		int maxIndex = min - 1;
		for (int i = min; i < max; i += size) {
			if (sample.getState(i).isAir()) {
				// check if there are at least size more true values
				int j = i + 1;
				while (j < max && sample.getState(j).isAir()) {
					j++;
				}
				int sequenceLength = j - i;
				if (sequenceLength >= size) {
					currentLength += sequenceLength;
					if (currentLength > maxLength) {
						maxLength = currentLength;
						maxIndex = i;
					}
					i = j - 1; // skip the sequence we just found
				}
			} else {
				currentLength = 0;
			}
		}
		return maxIndex;
	}

	@Override
	public StructureType<?> getType() {
		return MalumStructures.WEEPING_WELL;
	}
}

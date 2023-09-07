package dev.sterner.malum.common.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;

public interface MalumBlockProperties {

	static FabricBlockSettings WEEPING_WELL_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).sounds(MalumSoundRegistry.TAINTED_ROCK).strength(-1.0F, 3600000.0F);
	}

	static FabricBlockSettings PRIMORDIAL_SOUP_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.END_PORTAL).sounds(MalumSoundRegistry.BLIGHTED_EARTH).strength(-1.0F, 3600000.0F);
	}

	static FabricBlockSettings TAINTED_ROCK_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).sounds(MalumSoundRegistry.TAINTED_ROCK).strength(1.25F, 9.0F);
	}

	static FabricBlockSettings TWISTED_ROCK_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).sounds(MalumSoundRegistry.TWISTED_ROCK).strength(1.25F, 9.0F);
	}

	static FabricBlockSettings SOULSTONE_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(5.0F, 3.0F).sounds(MalumSoundRegistry.SOULSTONE);
	}

	static FabricBlockSettings DEEPSLATE_SOULSTONE_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(7.0F, 6.0F).sounds(MalumSoundRegistry.DEEPSLATE_SOULSTONE);
	}

	static FabricBlockSettings BLAZE_QUARTZ_ORE_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(3.0F, 3.0F).sounds(MalumSoundRegistry.BLAZING_QUARTZ_ORE);
	}

	static FabricBlockSettings BLAZE_QUARTZ_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(5.0F, 6.0F).sounds(MalumSoundRegistry.BLAZING_QUARTZ_BLOCK);
	}

	static FabricBlockSettings ARCANE_CHARCOAL_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(5.0F, 6.0F).sounds(MalumSoundRegistry.ARCANE_CHARCOAL_BLOCK);
	}

	static FabricBlockSettings RUNEWOOD_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).sounds(BlockSoundGroup.WOOD).strength(1.75F, 4.0F);
	}

	static FabricBlockSettings RUNEWOOD_PLANTS_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.OAK_SAPLING).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS).breakInstantly();
	}

	static FabricBlockSettings RUNEWOOD_LEAVES_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).strength(0.2F).ticksRandomly().nonOpaque().allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never).blockVision(Blocks::never).sounds(MalumSoundRegistry.RUNEWOOD_LEAVES);
	}

	static FabricBlockSettings SOULWOOD_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).sounds(MalumSoundRegistry.SOULWOOD).strength(1.75F, 4.0F);
	}

	static FabricBlockSettings BLIGHT_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.MOSS_BLOCK).sounds(MalumSoundRegistry.BLIGHTED_EARTH).strength(0.7f);
	}

	static FabricBlockSettings BLIGHT_PLANTS_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.PITCHER_PLANT).noCollision().nonOpaque().sounds(MalumSoundRegistry.BLIGHTED_FOLIAGE).breakInstantly();
	}

	static FabricBlockSettings SOULWOOD_LEAVES_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never).blockVision(Blocks::never).sounds(MalumSoundRegistry.SOULWOOD_LEAVES);
	}

	static FabricBlockSettings ETHER_BLOCK_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).sounds(MalumSoundRegistry.ETHER).noCollision().breakInstantly().luminance((b) -> 14);
	}

	static FabricBlockSettings HALLOWED_GOLD_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).sounds(MalumSoundRegistry.HALLOWED_GOLD).nonOpaque().strength(2F, 16.0F);
	}

	static FabricBlockSettings SOUL_STAINED_STEEL_BLOCK_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(MalumSoundRegistry.SOUL_STAINED_STEEL).strength(5f, 64.0f);
	}

	static FabricBlockSettings SPIRIT_JAR_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.GLASS).strength(0.5f, 64f).sounds(MalumSoundRegistry.HALLOWED_GOLD).nonOpaque();
	}

	static FabricBlockSettings SOUL_VIAL_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.GLASS).strength(0.75f, 64f).sounds(MalumSoundRegistry.SOUL_STAINED_STEEL).nonOpaque();
	}

	static FabricBlockSettings BRILLIANCE_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(3f, 3f).sounds(BlockSoundGroup.STONE);
	}

	static FabricBlockSettings DEEPSLATE_BRILLIANCE_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.DEEPSLATE).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE);
	}

	static FabricBlockSettings NATURAL_QUARTZ_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(4f, 3f).sounds(MalumSoundRegistry.NATURAL_QUARTZ);
	}

	static FabricBlockSettings DEEPSLATE_QUARTZ_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.DEEPSLATE).strength(6F, 3.0F).sounds(MalumSoundRegistry.DEEPSLATE_QUARTZ);
	}

	static FabricBlockSettings NATURAL_QUARTZ_CLUSTER_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(1.5F).sounds(MalumSoundRegistry.QUARTZ_CLUSTER);
	}

	static FabricBlockSettings RARE_EARTH_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(25f, 9999f).sounds(MalumSoundRegistry.RARE_EARTH);
	}

	static FabricBlockSettings MOTE_OF_MANA_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(25f, 9999f).sounds(MalumSoundRegistry.RARE_EARTH);
	}

	static FabricBlockSettings AURUM_PROPERTIES() {
		return FabricBlockSettings.copyOf(Blocks.STONE).strength(25f, 9999f).sounds(MalumSoundRegistry.CTHONIC_GOLD);
	}

}

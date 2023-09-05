package dev.sterner.malum.common.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;

public interface MalumBlockProperties {

	static FabricBlockSettings WEEPING_WELL_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.GRAY).sounds(MalumSoundRegistry.TAINTED_ROCK).strength(-1.0F, 3600000.0F);
	}

	static FabricBlockSettings PRIMORDIAL_SOUP_PROPERTIES() {
		return FabricBlockSettings.of(Material.PORTAL, MapColor.BLACK).sounds(MalumSoundRegistry.BLIGHTED_EARTH).strength(-1.0F, 3600000.0F);
	}

	static FabricBlockSettings TAINTED_ROCK_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.GRAY).sounds(MalumSoundRegistry.TAINTED_ROCK).strength(1.25F, 9.0F);
	}

	static FabricBlockSettings TWISTED_ROCK_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.GRAY).sounds(MalumSoundRegistry.TWISTED_ROCK).strength(1.25F, 9.0F);
	}

	static FabricBlockSettings SOULSTONE_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.DARK_RED).strength(5.0F, 3.0F).sounds(MalumSoundRegistry.SOULSTONE);
	}

	static FabricBlockSettings DEEPSLATE_SOULSTONE_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.DARK_RED).strength(7.0F, 6.0F).sounds(MalumSoundRegistry.DEEPSLATE_SOULSTONE);
	}

	static FabricBlockSettings BLAZE_QUARTZ_ORE_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.DARK_RED).strength(3.0F, 3.0F).sounds(MalumSoundRegistry.BLAZING_QUARTZ_ORE);
	}

	static FabricBlockSettings BLAZE_QUARTZ_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.RED).strength(5.0F, 6.0F).sounds(MalumSoundRegistry.BLAZING_QUARTZ_BLOCK);
	}

	static FabricBlockSettings ARCANE_CHARCOAL_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.BLACK).strength(5.0F, 6.0F).sounds(MalumSoundRegistry.ARCANE_CHARCOAL_BLOCK);
	}

	static FabricBlockSettings RUNEWOOD_PROPERTIES() {
		return FabricBlockSettings.of(Material.WOOD, MapColor.YELLOW).sounds(BlockSoundGroup.WOOD).strength(1.75F, 4.0F);
	}

	static FabricBlockSettings RUNEWOOD_PLANTS_PROPERTIES() {
		return FabricBlockSettings.of(Material.PLANT, MapColor.YELLOW).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS).breakInstantly();
	}

	static FabricBlockSettings RUNEWOOD_LEAVES_PROPERTIES() {
		return FabricBlockSettings.of(Material.LEAVES, MapColor.YELLOW).strength(0.2F).ticksRandomly().nonOpaque().allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never).blockVision(Blocks::never).sounds(MalumSoundRegistry.RUNEWOOD_LEAVES);
	}

	static FabricBlockSettings SOULWOOD_PROPERTIES() {
		return FabricBlockSettings.of(Material.WOOD, MapColor.PURPLE).sounds(MalumSoundRegistry.SOULWOOD).strength(1.75F, 4.0F);
	}

	static FabricBlockSettings BLIGHT_PROPERTIES() {
		return FabricBlockSettings.of(Material.MOSS_BLOCK, MapColor.PURPLE).sounds(MalumSoundRegistry.BLIGHTED_EARTH).strength(0.7f);
	}

	static FabricBlockSettings BLIGHT_PLANTS_PROPERTIES() {
		return FabricBlockSettings.of(Material.REPLACEABLE_PLANT, MapColor.PURPLE).noCollision().nonOpaque().sounds(MalumSoundRegistry.BLIGHTED_FOLIAGE).breakInstantly();
	}

	static FabricBlockSettings SOULWOOD_LEAVES_PROPERTIES() {
		return FabricBlockSettings.of(Material.LEAVES, MapColor.PURPLE).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never).blockVision(Blocks::never).sounds(MalumSoundRegistry.SOULWOOD_LEAVES);
	}

	static FabricBlockSettings ETHER_BLOCK_PROPERTIES() {
		return FabricBlockSettings.of(Material.WOOL, MapColor.YELLOW).sounds(MalumSoundRegistry.ETHER).noCollision().breakInstantly().luminance((b) -> 14);
	}

	static FabricBlockSettings HALLOWED_GOLD_PROPERTIES() {
		return FabricBlockSettings.of(Material.METAL, MapColor.YELLOW).sounds(MalumSoundRegistry.HALLOWED_GOLD).nonOpaque().strength(2F, 16.0F);
	}

	static FabricBlockSettings SOUL_STAINED_STEEL_BLOCK_PROPERTIES() {
		return FabricBlockSettings.of(Material.METAL, MapColor.PURPLE).sounds(MalumSoundRegistry.SOUL_STAINED_STEEL).strength(5f, 64.0f);
	}

	static FabricBlockSettings SPIRIT_JAR_PROPERTIES() {
		return FabricBlockSettings.of(Material.GLASS, MapColor.BLUE).strength(0.5f, 64f).sounds(MalumSoundRegistry.HALLOWED_GOLD).nonOpaque();
	}

	static FabricBlockSettings SOUL_VIAL_PROPERTIES() {
		return FabricBlockSettings.of(Material.GLASS, MapColor.BLUE).strength(0.75f, 64f).sounds(MalumSoundRegistry.SOUL_STAINED_STEEL).nonOpaque();
	}

	static FabricBlockSettings BRILLIANCE_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.GRAY).strength(3f, 3f).sounds(BlockSoundGroup.STONE);
	}

	static FabricBlockSettings DEEPSLATE_BRILLIANCE_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE);
	}

	static FabricBlockSettings NATURAL_QUARTZ_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.GRAY).strength(4f, 3f).sounds(MalumSoundRegistry.NATURAL_QUARTZ);
	}

	static FabricBlockSettings DEEPSLATE_QUARTZ_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).strength(6F, 3.0F).sounds(MalumSoundRegistry.DEEPSLATE_QUARTZ);
	}

	static FabricBlockSettings NATURAL_QUARTZ_CLUSTER_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.GRAY).strength(1.5F).sounds(MalumSoundRegistry.QUARTZ_CLUSTER);
	}

	static FabricBlockSettings RARE_EARTH_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.GRAY).strength(25f, 9999f).sounds(MalumSoundRegistry.RARE_EARTH);
	}

	static FabricBlockSettings MOTE_OF_MANA_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.CYAN).strength(25f, 9999f).sounds(MalumSoundRegistry.RARE_EARTH);
	}

	static FabricBlockSettings AURUM_PROPERTIES() {
		return FabricBlockSettings.of(Material.STONE, MapColor.GRAY).strength(25f, 9999f).sounds(MalumSoundRegistry.CTHONIC_GOLD);
	}

}

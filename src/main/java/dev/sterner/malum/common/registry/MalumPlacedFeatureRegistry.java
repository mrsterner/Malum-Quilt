package dev.sterner.malum.common.registry;

import com.google.common.collect.Lists;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.world.gen.feature.RunewoodTreeFeature;
import dev.sterner.malum.common.world.gen.feature.SoulwoodTreeFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;

public class MalumPlacedFeatureRegistry {
	//ORES
	public static RegistryKey<PlacedFeature> ORE_SOULSTONE_UPPER = placedFeature(Malum.id("ore_soulstone_upper"));
	public static RegistryKey<PlacedFeature> ORE_SOULSTONE_LOWER = placedFeature(Malum.id("ore_soulstone_lower"));
	public static RegistryKey<PlacedFeature> ORE_BRILLIANT = placedFeature(Malum.id("ore_brilliant"));
	public static RegistryKey<PlacedFeature> ORE_BLAZING_QUARTZ = placedFeature(Malum.id("ore_blazing_quartz"));
	public static RegistryKey<PlacedFeature> ORE_NATURAL_QUARTZ = placedFeature(Malum.id("ore_natural_quartz"));

	//TREES
	public static Feature<DefaultFeatureConfig> RUNEWOOD_FEATURE =
		Registry.register(Registries.FEATURE, Malum.id("runewood_tree"), new RunewoodTreeFeature());
	public static Feature<DefaultFeatureConfig> SOULWOOD_FEATURE =
		Registry.register(Registries.FEATURE, Malum.id("soulwood_tree"), new SoulwoodTreeFeature());

	public static PlacedFeature PLACED_RUNEWOOD_TREE_FEATURE;
	public static RegistryKey<PlacedFeature> PLACED_RUNEWOOD_TREE_FEATURE_KEY;

	public static ConfiguredFeature<DefaultFeatureConfig, Feature<DefaultFeatureConfig>> CONFIGURED_RUNEWOOD_TREE_FEATURE;
	public static RegistryKey<ConfiguredFeature<?, ?>> CONFIGURED_RUNEWOOD_TREE_FEATURE_KEY;

	public static ConfiguredFeature<DefaultFeatureConfig, Feature<DefaultFeatureConfig>> CONFIGURED_SOULWOOD_TREE_FEATURE;
	public static RegistryKey<ConfiguredFeature<?, ?>> CONFIGURED_SOULWOOD_TREE_FEATURE_KEY;

	//GEODES
	public static RegistryKey<PlacedFeature> GEODE_NATURAL_QUARTZ_UPPER = placedFeature(Malum.id("geode_quartz_upper"));
	public static RegistryKey<PlacedFeature> GEODE_NATURAL_QUARTZ_LOWER = placedFeature(Malum.id("geode_quartz_lower"));

	static RegistryKey<PlacedFeature> placedFeature(Identifier id) {
		return RegistryKey.of(RegistryKeys.PLACED_FEATURE, id);
	}

	public static void init(DynamicRegistryView registryView, Registry<ConfiguredFeature<?, ?>> configuredFeatures) {
		CONFIGURED_RUNEWOOD_TREE_FEATURE = Registry.register(configuredFeatures, Malum.id("runewood_tree_configured"), new ConfiguredFeature<>(RUNEWOOD_FEATURE, DefaultFeatureConfig.INSTANCE));
		CONFIGURED_RUNEWOOD_TREE_FEATURE_KEY = ConfiguredFeatures.of(Malum.id("runewood_tree_configured").toString());

		CONFIGURED_SOULWOOD_TREE_FEATURE = Registry.register(configuredFeatures, Malum.id("soulwood_tree_configured"), new ConfiguredFeature<>(SOULWOOD_FEATURE, DefaultFeatureConfig.INSTANCE));
		CONFIGURED_SOULWOOD_TREE_FEATURE_KEY = ConfiguredFeatures.of(Malum.id("soulwood_tree_configured").toString());

		registryView.getOptional(RegistryKeys.PLACED_FEATURE).ifPresent(registry -> {
			RegistryEntryLookup<ConfiguredFeature<?, ?>> entry = registryView.asDynamicRegistryManager().createRegistryLookup().getOrThrow(RegistryKeys.CONFIGURED_FEATURE);
			PLACED_RUNEWOOD_TREE_FEATURE = new PlacedFeature(entry.getOrThrow(CONFIGURED_RUNEWOOD_TREE_FEATURE_KEY), Lists.newArrayList());
			Registry.register(registry, Malum.id("runewood_tree_placed"), PLACED_RUNEWOOD_TREE_FEATURE);
		});
	}

	public static void init(){
		PLACED_RUNEWOOD_TREE_FEATURE_KEY =  PlacedFeatures.of(Malum.id("runewood_tree").toString());
		BiomeModification modifications = BiomeModifications.create(Malum.id("worldgen"));

		modifications.add(ModificationPhase.ADDITIONS, BiomeSelectors.tag(BiomeTags.IS_FOREST), ctx -> {
			ctx.getGenerationSettings().addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, PLACED_RUNEWOOD_TREE_FEATURE_KEY);
		});

		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.LOCAL_MODIFICATIONS, GEODE_NATURAL_QUARTZ_UPPER);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.LOCAL_MODIFICATIONS, GEODE_NATURAL_QUARTZ_LOWER);

		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SOULSTONE_UPPER);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SOULSTONE_LOWER);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_BRILLIANT);
		BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_BLAZING_QUARTZ);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_NATURAL_QUARTZ);
	}
}

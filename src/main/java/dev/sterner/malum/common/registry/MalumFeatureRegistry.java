package dev.sterner.malum.common.registry;

import dev.sterner.malum.common.world.gen.feature.RunewoodTreeFeature;
import dev.sterner.malum.common.world.gen.feature.SoulwoodTreeFeature;
import dev.sterner.malum.common.world.gen.feature.WeepingWellFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.PlacedFeatureUtil;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;

import java.util.LinkedHashMap;
import java.util.Map;

import static dev.sterner.malum.Malum.MODID;


public interface MalumFeatureRegistry {
    Map<Identifier, Feature<? extends FeatureConfig>> FEATURES = new LinkedHashMap<>();

	Feature<DefaultFeatureConfig> RUNEWOOD_TREE_FEATURE = register("runewood_tree", new RunewoodTreeFeature());
	Feature<DefaultFeatureConfig> SOULWOOD_TREE_FEATURE = register("soulwood_tree", new SoulwoodTreeFeature());
	Feature<DefaultFeatureConfig> WEEPING_WELL_FEATURE = register("weeping_well", new WeepingWellFeature());


	RegistryKey<PlacedFeature> RUNEWOOD_CHECKED = PlacedFeatureUtil.m_ssakkmfw("malum:runewood_tree");
	static <C extends FeatureConfig, F extends Feature<C>> F register(String id, F feature) {
        FEATURES.put(new Identifier(MODID, id), feature);
        return feature;
    }

	static void init() {
		FEATURES.forEach((id, feature) -> Registry.register(Registries.FEATURE_WORLDGEN, id, feature));
		//BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.VEGETAL_DECORATION, RUNEWOOD_CHECKED);
    }
}

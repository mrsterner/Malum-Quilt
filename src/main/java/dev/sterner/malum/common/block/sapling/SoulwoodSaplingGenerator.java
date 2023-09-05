package dev.sterner.malum.common.block.sapling;

import dev.sterner.malum.common.registry.MalumPlacedFeatureRegistry;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class SoulwoodSaplingGenerator extends SaplingGenerator {
    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        return MalumPlacedFeatureRegistry.CONFIGURED_SOULWOOD_TREE_FEATURE_KEY;
    }
}

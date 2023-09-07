package dev.sterner.malum.common.block.sapling;

import dev.sterner.malum.common.registry.MalumFeatureRegistry;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class RunewoodSaplingGenerator extends SaplingGenerator {
    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        return MalumFeatureRegistry.CONFIGURED_RUNEWOOD_TREE_FEATURE_KEY;
    }
}

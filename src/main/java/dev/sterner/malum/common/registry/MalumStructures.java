package dev.sterner.malum.common.registry;

import dev.sterner.malum.Malum;
import dev.sterner.malum.common.world.gen.structure.WeepingWellStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;

public class MalumStructures {
	public static StructureType<WeepingWellStructure> WEEPING_WELL;

	public static void init() {
		WEEPING_WELL = Registry.register(Registries.STRUCTURE_TYPE, Malum.id("weeping_well"), () -> WeepingWellStructure.CODEC);
	}
}

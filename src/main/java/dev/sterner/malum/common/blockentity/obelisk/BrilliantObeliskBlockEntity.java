package dev.sterner.malum.common.blockentity.obelisk;

import dev.sterner.lodestone.systems.multiblock.MultiBlockStructure;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

public class BrilliantObeliskBlockEntity extends ObeliskCoreBlockEntity {
	public static final Supplier<MultiBlockStructure> STRUCTURE = () -> (MultiBlockStructure.of(new MultiBlockStructure.StructurePiece(0, 1, 0, MalumObjects.BRILLIANT_OBELISK_COMPONENT.getDefaultState())));

	public BrilliantObeliskBlockEntity(BlockPos pos, BlockState state) {
		super(MalumBlockEntityRegistry.BRILLIANT_OBELISK, STRUCTURE.get(), pos, state);
	}
}

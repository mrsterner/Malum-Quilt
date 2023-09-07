package dev.sterner.malum.common.blockentity;

import dev.sterner.lodestone.systems.multiblock.MultiBlockCoreEntity;
import dev.sterner.lodestone.systems.multiblock.MultiBlockStructure;
import dev.sterner.malum.common.block.fusion_plate.FusionPlateComponentBlock;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.function.Supplier;

import static net.minecraft.state.property.Properties.*;

public class FusionPlateBlockEntity extends MultiBlockCoreEntity {
    public static final Supplier<MultiBlockStructure> STRUCTURE = () -> MultiBlockStructure.of(
        new MultiBlockStructure.StructurePiece(-1, 0, 0, MalumObjects.SOULWOOD_FUSION_PLATE_COMPONENT.getDefaultState().with(HORIZONTAL_FACING, Direction.WEST)),
        new MultiBlockStructure.StructurePiece(1, 0, 0, MalumObjects.SOULWOOD_FUSION_PLATE_COMPONENT.getDefaultState().with(HORIZONTAL_FACING, Direction.EAST)),
        new MultiBlockStructure.StructurePiece(0, 0, -1, MalumObjects.SOULWOOD_FUSION_PLATE_COMPONENT.getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH)),
        new MultiBlockStructure.StructurePiece(0, 0, 1, MalumObjects.SOULWOOD_FUSION_PLATE_COMPONENT.getDefaultState().with(HORIZONTAL_FACING, Direction.SOUTH)),
        new MultiBlockStructure.StructurePiece(-1, 0, -1, MalumObjects.SOULWOOD_FUSION_PLATE_COMPONENT.getDefaultState().with(HORIZONTAL_FACING, Direction.WEST).with(FusionPlateComponentBlock.CORNER, true)),
        new MultiBlockStructure.StructurePiece(1, 0, 1, MalumObjects.SOULWOOD_FUSION_PLATE_COMPONENT.getDefaultState().with(HORIZONTAL_FACING, Direction.EAST).with(FusionPlateComponentBlock.CORNER, true)),
        new MultiBlockStructure.StructurePiece(1, 0, -1, MalumObjects.SOULWOOD_FUSION_PLATE_COMPONENT.getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH).with(FusionPlateComponentBlock.CORNER, true)),
        new MultiBlockStructure.StructurePiece(-1, 0, 1, MalumObjects.SOULWOOD_FUSION_PLATE_COMPONENT.getDefaultState().with(HORIZONTAL_FACING, Direction.SOUTH).with(FusionPlateComponentBlock.CORNER, true)));

    public FusionPlateBlockEntity(BlockEntityType<?> type, MultiBlockStructure structure, BlockPos pos, BlockState state) {
        super(type, structure, pos, state);
    }

    public FusionPlateBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.FUSION_PLATE, STRUCTURE.get(), pos, state);
    }

	@Override
	public MultiBlockStructure getStructure() {
		return STRUCTURE.get();
	}
}

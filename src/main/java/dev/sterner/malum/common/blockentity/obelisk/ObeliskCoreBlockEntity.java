package dev.sterner.malum.common.blockentity.obelisk;

import com.sammy.lodestone.systems.multiblock.MultiBlockCoreEntity;
import com.sammy.lodestone.systems.multiblock.MultiBlockStructure;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class ObeliskCoreBlockEntity extends MultiBlockCoreEntity {
	public ObeliskCoreBlockEntity(BlockEntityType<? extends ObeliskCoreBlockEntity> type, MultiBlockStructure structure, BlockPos pos, BlockState state) {
		super(type, structure, pos, state);
	}
}

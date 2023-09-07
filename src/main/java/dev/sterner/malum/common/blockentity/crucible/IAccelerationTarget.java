package dev.sterner.malum.common.blockentity.crucible;

import dev.sterner.lodestone.helpers.BlockHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IAccelerationTarget {

    boolean canBeAccelerated();

    List<ICrucibleAccelerator> getAccelerators();

    List<BlockPos> getAcceleratorPositions();

    default int getLookupRange() {
        return 4;
    }

    default Map<ICrucibleAccelerator.CrucibleAcceleratorType, Integer> recalibrateAccelerators(World world, BlockPos pos) {
        getAccelerators().clear();
        getAcceleratorPositions().clear();
        Collection<ICrucibleAccelerator> nearbyAccelerators = BlockHelper.getBlockEntities(ICrucibleAccelerator.class, world, pos, getLookupRange());
        Map<ICrucibleAccelerator.CrucibleAcceleratorType, Integer> entries = new HashMap<>();
        for (ICrucibleAccelerator accelerator : nearbyAccelerators) {
            if (accelerator.canStartAccelerating() && (accelerator.getTarget() == null || accelerator.getTarget() == this)) {
                accelerator.setTarget(this);
                int max = accelerator.getAcceleratorType().maximumEntries;
                int amount = entries.computeIfAbsent(accelerator.getAcceleratorType(), (a) -> 0);
                if (amount < max) {
                    getAccelerators().add(accelerator);
                    getAcceleratorPositions().add(((BlockEntity) accelerator).getPos());
                    entries.replace(accelerator.getAcceleratorType(), amount + 1);
                }
            }
        }
        return entries;
    }

    default void saveAcceleratorData(NbtCompound compound) {
        NbtCompound acceleratorTag = new NbtCompound();
        List<BlockPos> positions = getAcceleratorPositions();
        if (!positions.isEmpty()) {
            acceleratorTag.putInt("amount", positions.size());
            for (int i = 0; i < positions.size(); i++) {
                BlockPos position = positions.get(i);
                BlockHelper.saveBlockPos(acceleratorTag, position, "accelerator_" + i + "_");
            }
            compound.put("acceleratorData", acceleratorTag);
        }
    }

    default void loadAcceleratorData(World world, NbtCompound compound) {
        getAcceleratorPositions().clear();
        getAccelerators().clear();
        if (compound.contains("acceleratorData")) {
            NbtCompound acceleratorTag = compound.getCompound("acceleratorData");
            int amount = acceleratorTag.getInt("amount");
            for (int i = 0; i < amount; i++) {
                BlockPos pos = BlockHelper.loadBlockPos(acceleratorTag, "accelerator_" + i + "_");
                if (world != null && world.getBlockEntity(pos) instanceof ICrucibleAccelerator accelerator) {
                    getAccelerators().add(accelerator);
                } else if (world != null) {
                    continue;
                }
                getAcceleratorPositions().add(pos);
            }
        }
    }
}

package dev.sterner.malum.common.blockentity.tablet;

import dev.sterner.lodestone.helpers.BlockHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.minecraft.state.property.Properties.FACING;

public interface ITabletTracker {
    List<TwistedTabletBlockEntity> getTablets();

    Set<BlockPos> getTabletPositions();

    default int getLookupRange() {
        return 4;
    }

    default void fetchTablets(World world, BlockPos pos) {
        getTablets().clear();
        getTabletPositions().clear();
        int range = getLookupRange();
        Collection<TwistedTabletBlockEntity> nearbyTablets = BlockHelper.getBlockEntities(TwistedTabletBlockEntity.class, world, pos, range);

        nearbyTablets = nearbyTablets.stream().filter(tabletBlockEntity -> {
            Direction direction = tabletBlockEntity.getCachedState().get(FACING);
            BlockPos tabletPos = tabletBlockEntity.getPos();
            if (tabletPos.getZ() == pos.getZ() && tabletPos.getX() == pos.getX()) {
                return direction == (tabletPos.getY() > pos.getY() ? Direction.DOWN : Direction.UP);

            }
            if (tabletPos.getZ() == pos.getZ()) {
                return direction == (tabletPos.getX() > pos.getX() ? Direction.WEST : Direction.EAST);
            } else if (tabletPos.getX() == pos.getX()) {
                return direction == (tabletPos.getZ() > pos.getZ() ? Direction.NORTH : Direction.SOUTH);
            }
            return false;
        }).collect(Collectors.toCollection(ArrayList::new));

        getTabletPositions().addAll(nearbyTablets.stream().map(BlockEntity::getPos).toList());
        getTablets().addAll(nearbyTablets);

    }

    default void saveTwistedTabletData(NbtCompound compound) {
		if (!getTabletPositions().isEmpty()) {
			NbtCompound twistedTabletTag = new NbtCompound();
			AtomicInteger tabletNumber = new AtomicInteger(0);
			twistedTabletTag.putInt("amount", getTabletPositions().size());
			getTabletPositions().forEach(pos -> BlockHelper.saveBlockPos(twistedTabletTag, pos, "" + tabletNumber.getAndIncrement()));
            compound.put("twistedTabletData", twistedTabletTag);
        }
    }

    default void loadTwistedTabletData(World world, NbtCompound compound) {
        if (compound.contains("twistedTabletData")) {
            NbtCompound twistedTabletTag = compound.getCompound("twistedTabletData");
            int amount = twistedTabletTag.getInt("amount");
            for (int i = 0; i < amount; i++) {
                BlockPos pos = BlockHelper.loadBlockPos(twistedTabletTag, "tablet_" + i);
                if (world != null && world.getBlockEntity(pos) instanceof TwistedTabletBlockEntity tabletBlockEntity) {
                    getTabletPositions().add(pos);
                    getTablets().add(tabletBlockEntity);
                }
            }
        }
    }
}

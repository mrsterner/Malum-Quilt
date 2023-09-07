package dev.sterner.malum.common.blockentity.mirror;

import dev.sterner.lodestone.forge.ItemHandler;
import dev.sterner.lodestone.systems.blockentity.LodestoneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MirrorBlockEntity extends LodestoneBlockEntity {
    public Direction direction;
    public BlockEntity attachedBlockEntity;
    public DefaultedList<ItemHandler> attachedInventory;
    public boolean updateAttached = false;
    public int cooldown;

    public MirrorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.direction = state.get(Properties.FACING);
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        if (direction != null) {
            compound.putString("direction", direction.toString());
        }
        super.writeNbt(compound);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        direction = Direction.byName(compound.getString("direction"));
        updateAttached = true;
        super.readNbt(compound);
    }

    @Override
    public void onPlace(LivingEntity placer, ItemStack stack) {
        updateAttached = true;
    }

    @Override
    public void onNeighborUpdate(BlockState state, BlockPos pos, BlockPos neighbor) {
        updateAttached = true;
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
        } else if (attachedBlockEntity != null) {
            attachedInventory.forEach(this::attachedTick);
        }
    }
    public void attachedTick(ItemHandler handler)
    {

    }
}

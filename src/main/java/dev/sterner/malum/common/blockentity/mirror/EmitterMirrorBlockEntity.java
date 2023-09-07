package dev.sterner.malum.common.blockentity.mirror;

import dev.sterner.lodestone.forge.ItemHandler;
import dev.sterner.malum.common.entity.spirit.MirrorItemEntity;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class EmitterMirrorBlockEntity extends MirrorBlockEntity {
    public EmitterMirrorBlockEntity(BlockPos pos, BlockState state) {
        super(MalumBlockEntityRegistry.EMITTER_MIRROR, pos, state);
    }

    @Override
    public void attachedTick(ItemHandler handler) {
        for (int i = handler.size() - 1; i >= 0; i--) {
            ItemStack stack = handler.getStack(i);
            if (!stack.isEmpty()) {
                cooldown = 20;
                MirrorItemEntity entity = new MirrorItemEntity(world, getCachedState().get(Properties.FACING),stack.split(stack.getCount()), getPos());
                world.spawnEntity(entity);
                break;
            }
        }
    }
}

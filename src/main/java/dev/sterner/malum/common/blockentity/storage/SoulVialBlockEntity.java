package dev.sterner.malum.common.blockentity.storage;

import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.lodestone.helpers.ItemHelper;
import dev.sterner.lodestone.systems.blockentity.LodestoneBlockEntity;
import dev.sterner.malum.api.interfaces.item.SoulContainerItem;
import dev.sterner.malum.client.ParticleEffects;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.spirit.MalumEntitySpiritData;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SoulVialBlockEntity extends LodestoneBlockEntity {
    public MalumEntitySpiritData data;

    public SoulVialBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SoulVialBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.SOUL_VIAL, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound pTag) {
        if (data != null) {
            data.saveTo(pTag);
        }
        super.writeNbt(pTag);
    }

    @Override
    public void readNbt(NbtCompound pTag) {
        if (pTag.contains(MalumEntitySpiritData.SOUL_DATA)) {
            data = MalumEntitySpiritData.load(pTag);
        } else {
            data = null;
        }
        super.readNbt(pTag);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof SoulContainerItem) {
            if (data == null) {
                if (stack.hasNbt() && stack.getNbt().contains(MalumEntitySpiritData.SOUL_DATA)) {
                    data = MalumEntitySpiritData.load(stack.getNbt());
                    if (stack.getCount() > 1) {
                        ItemStack split = stack.split(1);
                        split.getOrCreateNbt().remove(MalumEntitySpiritData.SOUL_DATA);
                        ItemHelper.giveItemToEntity(split, player);
                    } else {
                        stack.getOrCreateNbt().remove(MalumEntitySpiritData.SOUL_DATA);
                    }
                }
            } else {
                if (!stack.getOrCreateNbt().contains(MalumEntitySpiritData.SOUL_DATA)) {
                    if (stack.getCount() > 1) {
                        ItemStack split = stack.split(1);
                        data.saveTo(split.getOrCreateNbt());
                        data = null;
                        ItemHelper.giveItemToEntity(split, player);
                    } else {
                        data.saveTo(stack.getOrCreateNbt());
                        data = null;
                    }
                }
            }
            player.swingHand(hand, true);
            BlockHelper.updateAndNotifyState(world, pos);
            return ActionResult.SUCCESS;
        }
        return super.onUse(player, hand);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPlace(LivingEntity placer, ItemStack stack) {
        if (stack.hasNbt()) {
            readNbt(stack.getNbt());
        }
        markDirty();
    }

    @Override
    public void tick() {
        if (world.isClient) {
            if (data != null) {
                double y = 0.5f + Math.sin(world.getTime() / 20f) * 0.08f;
				ParticleEffects.spawnSoulParticles(world, pos.getX() + 0.5f, pos.getY() + y, pos.getZ() + 0.5f, 1, 0.75f, Vec3d.ZERO, data.primaryType.getColor(), data.primaryType.getEndColor());
            }
        }
    }
}

package dev.sterner.malum.common.blockentity.storage;

import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.helpers.ItemHelper;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import com.sammy.lodestone.systems.multiblock.MultiBlockCoreEntity;
import com.sammy.lodestone.systems.multiblock.MultiBlockStructure;
import dev.sterner.malum.api.interfaces.item.SoulContainerItem;
import dev.sterner.malum.client.ParticleEffects;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.spirit.MalumEntitySpiritData;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PlinthCoreBlockEntity extends MultiBlockCoreEntity {
    public static final Supplier<MultiBlockStructure> STRUCTURE = () -> MultiBlockStructure.of(new MultiBlockStructure.StructurePiece(0, 1, 0, MalumObjects.SOULWOOD_PLINTH_COMPONENT.getDefaultState()));

    public LodestoneBlockEntityInventory inventory;
    public MalumEntitySpiritData data;

    public PlinthCoreBlockEntity(BlockEntityType<?> type, MultiBlockStructure structure, BlockPos pos, BlockState state) {
        super(type, structure, pos, state);
    }

    public PlinthCoreBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.PLINTH, STRUCTURE.get(), pos, state);
        inventory = new LodestoneBlockEntityInventory(1, 64, (s) -> data == null) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(world, pos);
            }
        };
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        inventory.save(compound);
        if (data != null) {
            data.saveTo(compound);
        }
    }

    @Override
    public void readNbt(NbtCompound compound) {
        inventory.load(compound);
        if (compound.contains(MalumEntitySpiritData.SOUL_DATA)) {
            data = MalumEntitySpiritData.load(compound);
        } else {
            data = null;
        }
        super.readNbt(compound);
    }

    @Override
    public MultiBlockStructure getStructure() {
        return STRUCTURE.get();
    }

    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof SoulContainerItem) {
            if (world.isClient) {
                return ActionResult.CONSUME;
            }
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
        if (hand.equals(Hand.MAIN_HAND)) {
            inventory.interact(world, player, hand);
            return ActionResult.SUCCESS;
        }
        return super.onUse(player, hand);
    }

    @Override
    public void onBreak(@Nullable PlayerEntity player) {
        inventory.dumpItems(world, pos);
        super.onBreak(player);
    }

    @Override
    public void tick() {
        if (world.isClient) {
            if (inventory.getStack(0).getItem() instanceof MalumSpiritItem item) {
                Vec3d pos = getItemPos();
                double x = pos.x;
                double y = pos.y + Math.sin((world.getTime()) / 20f) * 0.05f;
                double z = pos.z;
				ParticleEffects.spawnSpiritGlimmerParticles(world, x, y, z, item.type.getColor(), item.type.getEndColor());
            }
            if (data != null) {
                Vec3d pos = getItemPos();
                double x = pos.x;
                double y = pos.y + Math.sin((world.getTime()) / 20f) * 0.08f;
                double z = pos.z;
				ParticleEffects.spawnSoulParticles(world, x, y, z, 1, 1, Vec3d.ZERO, data.primaryType.getColor(), data.primaryType.getEndColor());
            }
        }
    }

    public Vec3d getItemPos() {
        return BlockHelper.fromBlockPos(getPos()).add(itemOffset());
    }

    public Vec3d itemOffset() {
        return new Vec3d(0.5f, 2f, 0.5f);
    }
}

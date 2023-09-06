package dev.sterner.malum.common.blockentity.storage;

import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.systems.blockentity.ItemHolderBlockEntity;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import dev.sterner.malum.client.ParticleEffects;
import dev.sterner.malum.common.blockentity.spirit_altar.IAltarProvider;
import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.blockentity.totem.TotemPoleBlockEntity;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class ItemStandBlockEntity extends ItemHolderBlockEntity implements IAltarProvider {
    public ItemStandBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.ITEM_STAND, pos, state);
    }

    public ItemStandBlockEntity(BlockEntityType<? extends ItemStandBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        inventory = new LodestoneBlockEntityInventory(1, 64) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(world, pos);
            }
        };
    }

    public ItemStack getHeldItem() {
        return inventory.getStack(0);
    }

    @Override
    public LodestoneBlockEntityInventory getInventoryForAltar() {
        return inventory;
    }

    @Override
    public Vec3d getItemPosForAltar() {
        return getItemPos();
    }

    @Override
    public BlockPos getBlockPosForAltar() {
        return pos;
    }

    public Vec3d getItemPos() {
        return BlockHelper.fromBlockPos(getPos()).add(itemOffset());
    }

    public Vec3d itemOffset() {
        Direction direction = getCachedState().get(Properties.FACING);
        Vec3d directionVector = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
        return new Vec3d(0.5f - directionVector.getX() * 0.25f, 0.5f - directionVector.getY() * 0.1f, 0.5f - directionVector.getZ() * 0.25f);
    }

    @Override
    public void onPlace(LivingEntity placer, ItemStack stack) {
        Direction direction = getCachedState().get(Properties.FACING);
        BlockPos totemPolePos = getPos().offset(direction.getOpposite());
        if (world.getBlockEntity(totemPolePos) instanceof TotemPoleBlockEntity totemPole) {
            TotemBaseBlockEntity totemBase = totemPole.totemBase;
            if (totemBase != null) {
                totemBase.addFilter(this);
                BlockHelper.updateState(world, totemBase.getPos());
                BlockHelper.updateState(world, totemPole.getPos());
            }
        }
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
        }
    }

    public void clientTick(World world1, BlockPos pos, BlockState state1) {
    }
}

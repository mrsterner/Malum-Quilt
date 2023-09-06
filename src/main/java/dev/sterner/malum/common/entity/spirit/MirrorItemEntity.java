package dev.sterner.malum.common.entity.spirit;

import com.sammy.lodestone.helpers.ColorHelper;
import dev.sterner.malum.api.interfaces.item.FloatingGlowItem;
import dev.sterner.malum.client.ParticleEffects;
import dev.sterner.malum.common.entity.FloatingItemEntity;
import dev.sterner.malum.common.registry.MalumEntityRegistry;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MirrorItemEntity extends FloatingItemEntity {
    public Direction cachedDirection;
    public Direction direction;
    public BlockPos cachedBlockPos;
    public float desiredMoveTime = 1f;

    public MirrorItemEntity(World world) {
        super(MalumEntityRegistry.MIRROR_ITEM, world);
        maxAge = 4000;
        direction = Direction.NORTH;
        moveTime = 1f;
    }

    public MirrorItemEntity(World world, Direction direction, ItemStack stack, BlockPos pos) {
        super(MalumEntityRegistry.MIRROR_ITEM, world);
        this.direction = direction;
        setItem(stack);
        setPos(pos.getX() + 0.5f, pos.getY() + 0.25f, pos.getZ() + 0.5f);
        maxAge = 4000;
        moveTime = 1f;
        float multiplier = 0.02f;
        setVelocity(direction.getOffsetX() * multiplier, direction.getOffsetY() * multiplier, direction.getOffsetZ() * multiplier);
    }

    @Override
    public void setItem(ItemStack pStack) {
        if (!(pStack.getItem() instanceof FloatingGlowItem)) {
            setColor(ColorHelper.brighter(MalumSpiritTypeRegistry.ARCANE_SPIRIT.getColor(), 2), MalumSpiritTypeRegistry.ARCANE_SPIRIT.getEndColor());
        }
        super.setItem(pStack);
    }

    @Override
    public void spawnParticles(double x, double y, double z) {
		ParticleEffects.spawnSpiritParticles(getWorld(), x, y, z, 1.5f, Vec3d.ZERO, color, endColor);
    }

    @Override
    public void move() {
        if (moveTime < desiredMoveTime) {
            moveTime += 0.1f;
        }
        if (moveTime > desiredMoveTime) {
            moveTime -= 0.1f;
        }
        if (cachedBlockPos != getBlockPos() || getWorld().getTime() % 10L == 0) {
            cachedBlockPos = getBlockPos();
            BlockPos ahead = cachedBlockPos.offset(direction, 1);
            BlockState state = getWorld().getBlockState(ahead);
            if (state.isSideSolidFullSquare(getWorld(), ahead, direction)) {
                desiredMoveTime = 0f;
            } else {
                desiredMoveTime = 1f;
            }
        }
        if (cachedDirection != direction) {
            cachedDirection = direction;
        }
        float multiplier = 0.02f * moveTime;
        setVelocity(direction.getOffsetX() * multiplier, direction.getOffsetY() * multiplier, direction.getOffsetZ() * multiplier);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        compound.putString("direction", direction.toString());
        compound.putFloat("desiredMoveTime", desiredMoveTime);
        super.writeCustomDataToNbt(compound);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        direction = Direction.byName(compound.getString("direction"));
        cachedDirection = direction;
        desiredMoveTime = compound.getFloat("desiredMoveTime");
        super.readCustomDataFromNbt(compound);
    }
}

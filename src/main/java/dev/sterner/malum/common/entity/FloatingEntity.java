package dev.sterner.malum.common.entity;

import com.sammy.lodestone.helpers.EntityHelper;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class FloatingEntity extends Entity {
    protected static final TrackedData<Integer> DATA_COLOR = DataTracker.registerData(FloatingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> DATA_END_COLOR = DataTracker.registerData(FloatingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public final float hoverStart;
    public final ArrayList<EntityHelper.PastPosition> pastPositions = new ArrayList<>();
    public Color color = MalumSpiritTypeRegistry.SACRED_SPIRIT.getColor();
    public Color endColor = MalumSpiritTypeRegistry.SACRED_SPIRIT.getEndColor();
    public int maxAge;
    public int age;
    public float moveTime;
    public int range = 3;
    public float windUp;

    public FloatingEntity(EntityType<? extends FloatingEntity> type, World world) {
        super(type, world);
        noClip = false;
        this.hoverStart = (float) (Math.random() * Math.PI * 2.0D);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(DATA_COLOR, MalumSpiritTypeRegistry.SACRED_SPIRIT.getColor().getRGB());
        this.getDataTracker().startTracking(DATA_END_COLOR, MalumSpiritTypeRegistry.SACRED_SPIRIT.getEndColor().getRGB());
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound tag) {
        tag.putInt("age", age);
        tag.putFloat("moveTime", moveTime);
        tag.putInt("range", range);
        tag.putFloat("windUp", windUp);
        tag.putInt("red", color.getRed());
        tag.putInt("green", color.getGreen());
        tag.putInt("blue", color.getBlue());
        tag.putInt("endRed", endColor.getRed());
        tag.putInt("endGreen", endColor.getGreen());
        tag.putInt("endBlue", endColor.getBlue());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound tag) {
        age = tag.getInt("age");
        moveTime = tag.getFloat("moveTime");
        int range = tag.getInt("range");
        if (range > 0) {
            this.range = range;
        }
        windUp = tag.getFloat("windUp");
        color = new Color(tag.getInt("red"), tag.getInt("green"), tag.getInt("blue"));
        endColor = new Color(tag.getInt("endRed"), tag.getInt("endGreen"), tag.getInt("endBlue"));
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (DATA_COLOR.equals(data)) {
            color = new Color(dataTracker.get(DATA_COLOR));
        }
        if (DATA_END_COLOR.equals(data)) {
            endColor = new Color(dataTracker.get(DATA_END_COLOR));
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public void tick() {
        super.tick();
        baseTick();
        trackPastPositions();
        age++;
        if (windUp < 1f) {
            windUp += 0.02f;
        }
        if (age > maxAge) {
            remove(RemovalReason.KILLED);
        }
        if (getWorld().isClient) {
            double x = getX(), y = getY() + getYOffset(0) + 0.25f, z = getZ();
            spawnParticles(x, y, z);
        } else {
            move();
        }
    }

    public void trackPastPositions() {
        EntityHelper.trackPastPositions(pastPositions, getPos().add(0, getYOffset(0) + 0.25F, 0), 0.01f);
        removeOldPositions(pastPositions);
    }

    public void removeOldPositions(List<EntityHelper.PastPosition> pastPositions) {
        int amount = pastPositions.size() - 1;
        List<EntityHelper.PastPosition> toRemove = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            EntityHelper.PastPosition excess = pastPositions.get(i);
            if (excess.time > 9) {
                toRemove.add(excess);
            }
        }
        pastPositions.removeAll(toRemove);
    }


    public void baseTick() {
        BlockHitResult result = getWorld().raycast(new RaycastContext(getPos(), getPos().add(getVelocity()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = result.getBlockPos();
            BlockState blockstate = this.getWorld().getBlockState(blockpos);
            if (blockstate.isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(blockpos);
            } else if (blockstate.isOf(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.getWorld().getBlockEntity(blockpos);
                if (blockentity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                    EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockpos, blockstate, this, (EndGatewayBlockEntity) blockentity);
                }
            }
        }
        this.checkBlockCollision();
        Vec3d movement = this.getVelocity();
        double nextX = this.getX() + movement.x;
        double nextY = this.getY() + movement.y;
        double nextZ = this.getZ() + movement.z;
        double distance = movement.horizontalLength();
        this.setPitch(lerpRotation(this.prevPitch, (float) (MathHelper.atan2(movement.y, distance) * (double) (180F / (float) Math.PI))));
        this.setYaw(lerpRotation(this.prevYaw, (float) (MathHelper.atan2(movement.x, movement.z) * (double) (180F / (float) Math.PI))));
        this.setPos(nextX, nextY, nextZ);
    }

    protected static float lerpRotation(float p_37274_, float p_37275_) {
        while (p_37275_ - p_37274_ < -180.0F) {
            p_37274_ -= 360.0F;
        }

        while (p_37275_ - p_37274_ >= 180.0F) {
            p_37274_ += 360.0F;
        }

        return MathHelper.lerp(0.2F, p_37274_, p_37275_);
    }

    public void spawnParticles(double x, double y, double z) {

    }

    public void move() {
    }

    public float getYOffset(float partialTicks) {
        return MathHelper.sin(((float) age + partialTicks) / 20.0F + hoverStart) * 0.1F + 0.1F;
    }

    public float getRotation(float partialTicks) {
        return ((float) age + partialTicks) / 20.0F + this.hoverStart;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }
}

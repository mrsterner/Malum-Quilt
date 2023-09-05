package dev.sterner.malum.common.entity.spirit;

import dev.sterner.malum.client.CommonParticleEffects;
import dev.sterner.malum.common.entity.FloatingEntity;
import dev.sterner.malum.common.registry.MalumEntityRegistry;
import dev.sterner.malum.common.spirit.MalumEntitySpiritData;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;


public class SoulEntity extends FloatingEntity {
    public UUID thiefUUID;
    public MalumEntitySpiritData spiritData = MalumEntitySpiritData.EMPTY;
    public LivingEntity thief;

    public SoulEntity(World world) {
        super(MalumEntityRegistry.NATURAL_SOUL, world);
        maxAge = 2000;
    }

    public SoulEntity(World world, MalumEntitySpiritData spiritData, UUID ownerUUID, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        super(MalumEntityRegistry.NATURAL_SOUL, world);
        this.spiritData = spiritData;
        if (!spiritData.equals(MalumEntitySpiritData.EMPTY)) {
            this.color = spiritData.primaryType.getColor();
            getDataTracker().set(DATA_COLOR, color.getRGB());
            this.endColor = spiritData.primaryType.getEndColor();
            getDataTracker().set(DATA_END_COLOR, endColor.getRGB());
        }
        setThief(ownerUUID);
        setPos(posX, posY, posZ);
        setVelocity(velX, velY, velZ);
        maxAge = 600;
    }


    public void setThief(UUID ownerUUID) {
        this.thiefUUID = ownerUUID;
        updateThief();
    }

    public void updateThief() {
        if (!getWorld().isClient()) {
            thief = (LivingEntity) ((ServerWorld) getWorld()).getEntity(thiefUUID);
        }
    }

    @Override
    public void spawnParticles(double x, double y, double z) {
        Vec3d motion = getVelocity();
        Vec3d norm = motion.normalize().multiply(0.025f);
        float cycles = 4;
        for (int i = 0; i < cycles; i++) {
            double lerpX = MathHelper.lerp(i / cycles, x - motion.x, x);
            double lerpY = MathHelper.lerp(i / cycles, y - motion.y, y);
            double lerpZ = MathHelper.lerp(i / cycles, z - motion.z, z);
			CommonParticleEffects.spawnSoulParticles(getWorld(), lerpX, lerpY, lerpZ, 0.25f, 1, norm, color, endColor);
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        if (pReason.equals(RemovalReason.KILLED)) {
            SpiritHelper.createSpiritsFromSoul(spiritData, getWorld(), getPos(), thief);
        }
        super.remove(pReason);
    }

    @Override
    public void move() {
        setVelocity(getVelocity().multiply(0.95f, 0.97f, 0.95f));
        if (thief == null || !thief.isAlive()) {
            if (getWorld().getTime() % 40L == 0) {
                PlayerEntity playerEntity = getWorld().getClosestPlayer(this, 10);
                if (playerEntity != null) {
                    setThief(playerEntity.getUuid());
                }
            }
            return;
        }
        float sine = MathHelper.sin(getWorld().getTime()*0.05f)*0.2f;
        Vec3d desiredLocation = thief.getPos().add(0, thief.getStandingEyeHeight() / 4, 0).add(-sine, sine, -sine);
        float distance = (float) squaredDistanceTo(desiredLocation);
        float velocity = MathHelper.lerp(Math.min(moveTime, 20) / 20f, 0.05f, 0.1f);
        if (distance > 2) {
            moveTime++;
            Vec3d desiredMotion = desiredLocation.subtract(getPos()).normalize().multiply(velocity, velocity, velocity).add(0, 0.075f, 0);
            float easing = 0.01f;
            float xMotion = (float) MathHelper.lerp(easing, getVelocity().x, desiredMotion.x);
            float yMotion = (float) MathHelper.lerp(easing, getVelocity().y, desiredMotion.y);
            float zMotion = (float) MathHelper.lerp(easing, getVelocity().z, desiredMotion.z);
            Vec3d resultingMotion = new Vec3d(xMotion, yMotion, zMotion);
            setVelocity(resultingMotion);
            return;
        }

        boolean above = !getWorld().isSpaceEmpty(getBoundingBox().offset(0, 1.5f, 0));
        boolean below = !getWorld().isSpaceEmpty(getBoundingBox().offset(0, -2f, 0));
        if (above && below) {
            setVelocity(getVelocity().add(0, 0.002f, 0));
            return;
        }
        if (below) {
            setVelocity(getVelocity().add(0, 0.003f, 0));
        }
        if (above || !below) {
            setVelocity(getVelocity().add(0, -0.0015f, 0));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        if (!spiritData.equals(MalumEntitySpiritData.EMPTY)) {
            spiritData.saveTo(compound);
        }
        if (thiefUUID != null) {
            compound.putUuid("thiefUUID", thiefUUID);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        spiritData = MalumEntitySpiritData.load(compound);
        if (compound.contains("thiefUUID")) {
            setThief(compound.getUuid("thiefUUID"));
        }
    }
}

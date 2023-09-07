package dev.sterner.malum.common.entity.nitrate;

import dev.sterner.lodestone.helpers.EntityHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNitrateEntity extends ThrownEntity {
    public static final Color SECOND_SMOKE_COLOR = new Color(45, 45, 45);

    public final ArrayList<EntityHelper.PastPosition> pastPositions = new ArrayList<>(); // *screaming*
    public int maxAge = 1000;
    public int age;
    public float windUp;
    public int pierce = getPierce();

    public AbstractNitrateEntity(EntityType<? extends AbstractNitrateEntity> type, World world) {
        super(type, world);
    }

    public AbstractNitrateEntity(EntityType<? extends AbstractNitrateEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
    }

    public AbstractNitrateEntity(EntityType<? extends AbstractNitrateEntity> type, LivingEntity owner, World world) {
        super(type, owner, world);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("maxAge", maxAge);
        compound.putInt("age", age);
        compound.putFloat("windUp", windUp);
        compound.putInt("pierce", pierce);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        maxAge = compound.getInt("maxAge");
        age = compound.getInt("age");
        windUp = compound.getFloat("windUp");
        pierce = compound.getInt("pierce");
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        EthericExplosion.explode(getWorld(), this, getX(), getBodyY(0.0625D), getZ(), getExplosionRadius(), Explosion.DestructionType.DESTROY);
        onExplode();
        if (pierce <= 0) {
            discard();
        } else {
            pierce--;
        }
    }

    @Override
    public boolean isTouchingWater() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        trackPastPositions();
        age++;
        if (age > maxAge) {
            discard();
        }
        if (windUp < 1f) {
            windUp += 0.1f;
        }
        if (getWorld().isClient) {
            spawnParticles();
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
            if (excess.time > Math.min(age*0.8f, 25)) {
                toRemove.add(excess);
            }
        }
        pastPositions.removeAll(toRemove);
    }

    public void onExplode() {

    }

    public abstract void spawnParticles();

    public float getYOffset(float partialTicks) {
        return MathHelper.sin(((float) age + partialTicks) / 10.0F) * 0.2F + 0.1F;
    }

    public abstract int getPierce();

    public abstract float getExplosionRadius();
}

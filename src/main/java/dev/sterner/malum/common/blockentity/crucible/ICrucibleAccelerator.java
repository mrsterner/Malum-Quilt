package dev.sterner.malum.common.blockentity.crucible;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public interface ICrucibleAccelerator {
    CrucibleAcceleratorType getAcceleratorType();

    default boolean canStartAccelerating() {
        return true;
    }
    default boolean canAccelerate() {
        return true;
    }
    IAccelerationTarget getTarget();

    void setTarget(IAccelerationTarget target);

    default void addParticles(BlockPos targetPos, Vec3d targetItemPos) {
    }

    default void addParticles(Color color, Color endColor, float alpha, BlockPos targetPos, Vec3d targetItemPos) {

    }

    abstract class CrucibleAcceleratorType {
        public final int maximumEntries;
        public final String type;

        public CrucibleAcceleratorType(int maximumEntries, String type) {
            this.maximumEntries = maximumEntries;
            this.type = type;
        }

        public float getDamageChance(int entries) {
            return 0;
        }

        public int getMaximumDamage(int entries) {
            return 0;
        }

        public float getAcceleration(int entries) {
            return 0;
        }
    }

    class ArrayCrucibleAcceleratorType extends CrucibleAcceleratorType {
        public final float[] damageChance;
        public final int[] maximumDamage;
        public final float[] acceleration;

        public ArrayCrucibleAcceleratorType(String type, float[] damageChance, int[] maximumDamage, float[] acceleration) {
            super(damageChance.length, type);
            this.damageChance = damageChance;
            this.maximumDamage = maximumDamage;
            this.acceleration = acceleration;
        }

        @Override
        public float getDamageChance(int entries) {
            return damageChance[entries - 1];
        }

        @Override
        public int getMaximumDamage(int entries) {
            return maximumDamage[entries - 1];
        }

        @Override
        public float getAcceleration(int entries) {
            return acceleration[entries - 1];
        }
    }
}

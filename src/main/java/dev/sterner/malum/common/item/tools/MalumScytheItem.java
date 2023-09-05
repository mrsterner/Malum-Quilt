package dev.sterner.malum.common.item.tools;

import dev.sterner.malum.common.entity.boomerang.ScytheBoomerangEntity;
import dev.sterner.malum.common.event.MalumEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * uses event for sweep attack, @see {@link MalumEvents#scytheSweep(LivingEntity, DamageSource, float)}
 */
public class MalumScytheItem extends SwordItem {
    public MalumScytheItem(ToolMaterial material, float damage, float speed , Settings settings) {
        super(material, (int) damage + 3, speed - 3.2f, settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return true;
    }

    public static void spawnSweepParticles(PlayerEntity player, DefaultParticleType type) {
        double d0 = (-MathHelper.sin(player.getYaw() * ((float) Math.PI / 180F)));
        double d1 = MathHelper.cos(player.getYaw() * ((float) Math.PI / 180F));
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(type, player.getX() + d0, player.getBodyY(0.5D), player.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
        }
    }

    public static ItemStack getScytheItemStack(DamageSource source, LivingEntity attacker) {
        ItemStack stack = attacker.getMainHandStack();
        if (source.getSource() instanceof ScytheBoomerangEntity) {
            stack = ((ScytheBoomerangEntity) source.getSource()).scythe;
        }
        return stack.getItem() instanceof MalumScytheItem ? stack : ItemStack.EMPTY;
    }
}

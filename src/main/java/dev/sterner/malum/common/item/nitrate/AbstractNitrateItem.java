package dev.sterner.malum.common.item.nitrate;

import com.sammy.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import com.sammy.lodestone.systems.particle.screen.LodestoneScreenParticleTextureSheet;
import com.sammy.lodestone.systems.particle.screen.base.ScreenParticle;
import dev.sterner.malum.common.entity.nitrate.AbstractNitrateEntity;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class AbstractNitrateItem extends Item implements ParticleEmitterHandler.ItemParticleSupplier {
    public final Function<PlayerEntity, AbstractNitrateEntity> entitySupplier;

    public AbstractNitrateItem(Settings settings, Function<PlayerEntity, AbstractNitrateEntity> entitySupplier) {
        super(settings);
        this.entitySupplier = entitySupplier;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemstack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), MalumSoundRegistry.NITRATE_THROWN, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
        if (!world.isClient()) {
            AbstractNitrateEntity bombEntity = entitySupplier.apply(user);
            int angle = hand == Hand.MAIN_HAND ? 225 : 90;
            Vec3d pos = user.getPos().add(user.getRotationVector().multiply(0.5)).add(0.5 * Math.sin(Math.toRadians(angle - user.headYaw)), user.getHeight() * 2 / 3, 0.5 * Math.cos(Math.toRadians(angle - user.headYaw)));
            float pitch = -10.0F;
            bombEntity.setVelocity(user, user.getPitch(), user.getYaw(), pitch, 1.25F, 0.9F);
            bombEntity.setPosition(pos);
            world.spawnEntity(bombEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemstack.decrement(1);
        }
        return TypedActionResult.success(itemstack, world.isClient());
    }


	@Override
	public void spawnParticles(HashMap<LodestoneScreenParticleTextureSheet, ArrayList<ScreenParticle>> target, World world, float partialTick, ItemStack stack) {

	}
}

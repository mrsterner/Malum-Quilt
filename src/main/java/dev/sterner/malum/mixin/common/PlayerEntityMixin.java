package dev.sterner.malum.mixin.common;


import dev.emi.trinkets.api.TrinketsApi;
import dev.sterner.malum.common.item.spirit.TyrvingItem;
import dev.sterner.malum.common.item.tools.MalumScytheItem;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumParticleRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.util.handler.SoulwardHandler;
import dev.sterner.malum.common.statuseffect.AqueousAura;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static dev.sterner.malum.common.registry.MalumAttributeRegistry.SCYTHE_PROFICIENCY;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Unique
	float f;

	@Shadow
	public abstract void spawnSweepAttackParticles();

	@Shadow
	public abstract SoundCategory getSoundCategory();

	/**
	 * Performs many reactions when being hit
	 */
	@ModifyArgs(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
	private void malum$onDamaged(Args args) {
		DamageSource source = args.get(0);
		float value = args.get(1);
		args.set(1, SoulwardHandler.shieldPlayer(this, source, value));
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void malum$tick(CallbackInfo ci) {
		SoulwardHandler.recoverSoulWard((PlayerEntity) (Object) this);
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMovementSpeed()F"), index = 3)
	private float malum$captureF(float f) {
		this.f = f;
		return f;
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
	private void malum$attack(Entity target, CallbackInfo ci) {
		if (this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MalumScytheItem) {
			boolean canSweep = !this.getComponent(TrinketsApi.TRINKET_COMPONENT).isEquipped(MalumObjects.NECKLACE_OF_THE_NARROW_EDGE);
			SoundEvent sound;
			if (canSweep) {
				spawnSweepParticles((PlayerEntity) (Object) this, MalumParticleRegistry.SCYTHE_SWEEP_ATTACK_PARTICLE);
				sound = SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP;
			} else {
				spawnSweepParticles((PlayerEntity) (Object) this, MalumParticleRegistry.SCYTHE_CUT_ATTACK_PARTICLE);
				sound = MalumSoundRegistry.SCYTHE_CUT;
			}
			getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), sound, this.getSoundCategory(), 1, 1);

			float damage = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * f;
			getWorld().getOtherEntities(this, target.getBoundingBox().expand(1)).forEach(e -> {
				if (e instanceof LivingEntity livingEntity) {
					if (livingEntity.isAlive()) {
						livingEntity.damage(MalumDamageSourceRegistry.create(livingEntity.getWorld(), MalumDamageSourceRegistry.SCYTHE_SWEEP,this), damage);
						livingEntity.takeKnockback(0.4F, MathHelper.sin(this.getYaw() * ((float) Math.PI / 180F)), (-MathHelper.cos(this.getYaw() * ((float) Math.PI / 180F))));
					}
				}
			});
		}
	}
	@Unique
	public void spawnSweepParticles(PlayerEntity player, DefaultParticleType type) {
		double d0 = (-MathHelper.sin(player.getYaw() * ((float) Math.PI / 180F)));
		double d1 = MathHelper.cos(player.getYaw() * ((float) Math.PI / 180F));
		if (player.getWorld() instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(type, player.getX() + d0, player.getBodyY(0.5D), player.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
		}
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"), index = 3)
	private float malum$attackCooldown(float value) {
		if (this.getMainHandStack().getItem() instanceof MalumScytheItem) {
			return value + (float) this.getAttributeValue(SCYTHE_PROFICIENCY) * 0.5f;
		}
		return value;
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/entity/LivingEntity;)I", ordinal = 0), index = 8)
	private boolean malum$attackFireAspect(boolean bl4) {
		ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
		if (itemStack.getItem() instanceof TyrvingItem) {
			return false;
		}
		return bl4;
	}

	@ModifyArg(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"), index = 1)
	private Box malum$aiStep(Box aabb) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		return AqueousAura.growBoundingBox(player, aabb);
	}
}

package dev.sterner.malum.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import dev.sterner.malum.api.event.LivingEntityEvent;
import dev.sterner.malum.common.component.MalumComponents;
import dev.sterner.malum.common.item.equipment.trinket.CurioAlchemicalRing;
import dev.sterner.malum.common.item.equipment.trinket.CurioHarmonyNecklace;
import dev.sterner.malum.common.item.equipment.trinket.CurioVoraciousRing;
import dev.sterner.malum.common.registry.MalumAttributeRegistry;
import dev.sterner.malum.common.statuseffect.GluttonyEffect;
import dev.sterner.malum.common.util.handler.SoulHarvestHandler;
import dev.sterner.malum.common.util.handler.SpiritHarvestHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static dev.sterner.lodestone.setup.LodestoneAttributeRegistry.MAGIC_RESISTANCE;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract double getAttributeValue(EntityAttribute attribute);

	@Shadow
	public abstract @Nullable StatusEffectInstance getStatusEffect(StatusEffect effect);

	@Shadow public abstract ItemStack getStackInHand(Hand hand);

	@Shadow public abstract void setHealth(float health);

	@Shadow public abstract boolean clearStatusEffects();

	@Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

	@Shadow public abstract void setStackInHand(Hand hand, ItemStack stack);

	@Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

	@Shadow
	protected int itemUseTimeLeft;

	@Shadow
	public abstract ItemStack getActiveItem();

	public LivingEntityMixin(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "createLivingAttributes", at = @At("RETURN"))
	private static void malum$createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
		MalumAttributeRegistry.ATTRIBUTES.forEach((id, entityAttribute) -> info.getReturnValue().add(entityAttribute));
	}

	@Inject(method = "setCurrentHand", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I", shift = At.Shift.AFTER))
	private void malum$affectTimeLeft(Hand hand, CallbackInfo ci) {
		itemUseTimeLeft = CurioVoraciousRing.accelerateEating((LivingEntity) (Object) this, itemUseTimeLeft);

	}

	@Inject(method = "onTrackedDataSet", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I", shift = At.Shift.AFTER))
	private void malum$affectTimeLeft(TrackedData<?> data, CallbackInfo ci) {
		itemUseTimeLeft = CurioVoraciousRing.accelerateEating((LivingEntity) (Object) this, itemUseTimeLeft);

	}

	@Inject(method = "onDeath", at = @At("HEAD"))
	private void malum$onDeath(DamageSource source, CallbackInfo ci) {
		if (!getWorld().isClient) {
			SpiritHarvestHandler.shatterSoul(source, (LivingEntity) (Object) this);
		}
	}


	@ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
	private float malum$onDamageEvent(float amount, DamageSource source, float amount2) {
		return LivingEntityEvent.ON_DAMAGE_EVENT.invoker().react((LivingEntity) (Object) this, source, amount);
	}

	@ModifyVariable(method = "modifyAppliedDamage", at = @At(value = "RETURN", ordinal = 2, shift = At.Shift.BEFORE), index = 2, argsOnly = true)
	private float malum$applyEnchantmentsToDamage(float value, DamageSource source, float amount) {
		if (source.isOf(DamageTypes.MAGIC)) {
			float multiplier = 1.0f - (float) Math.max(((1 - (0.5 * (1 / (0.6 * this.getAttributeValue(MAGIC_RESISTANCE))))) * 0.6), 0);
			return value * multiplier;
		}
		return value;
	}

	@ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasNoGravity()Z", ordinal = 1), index = 2)
	private double malum$setVelocity(double d) {
		// todo, fix corrupted aerial aura
		return d;
	}
/*
	@Inject(method = "consumeItem", at = @At(value = "INVOKE", shift = At.Shift.BY, by = 1, target = "Lnet/minecraft/item/ItemStack;finishUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void malum$onFinishUsing(CallbackInfo ci, Hand hand, ItemStack result) {
		CurioVoraciousRing.finishEating((LivingEntity)(Object) this, result);
		GluttonyEffect.finishEating(result, (LivingEntity)(Object) this);
	}

 */

	@ModifyReceiver(method = "consumeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;finishUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;"))
	private ItemStack malum$onFinished(ItemStack itemStack, World world, LivingEntity livingEntity){
		CurioVoraciousRing.finishEating((LivingEntity)(Object) this, itemStack);
		GluttonyEffect.finishEating(itemStack, (LivingEntity)(Object) this);
		return itemStack;
	}

	@ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
	private double malum$travel(double value) {
		LivingEntity livingEntity = (LivingEntity) (Object) this;
		return MalumComponents.TOUCH_OF_DARKNESS_COMPONENT.get(livingEntity).updateEntityGravity(livingEntity, value);
	}

	@Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
	private void malum$potionUpdateInject(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir){
		LivingEntity livingEntity = (LivingEntity)(Object)this;
		if(!GluttonyEffect.canApplyPotion(livingEntity)){
			cir.setReturnValue(false);
		}
		if(livingEntity instanceof PlayerEntity player){
			CurioAlchemicalRing.onPotionApplied(player);
		}
	}

	@Inject(method = "getAttackDistanceScalingFactor", at = @At("RETURN"), cancellable = true)
	private void malum$injectVisibility(Entity entity, CallbackInfoReturnable<Double> cir){
		if((LivingEntity)(Object)this instanceof PlayerEntity player){
			cir.setReturnValue(CurioHarmonyNecklace.preventDetection(player, entity, cir.getReturnValue()));
		}
	}

	@ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
	private float malum$swimSpeed(float original) {
		return original * (float)this.getAttributeValue(MalumAttributeRegistry.SWIM_SPEED);
	}

	@ModifyArg(method = "swimUpward", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"), index = 1)
	private double malum$modifySwimSpeed(double y) {
		return y * this.getAttributeValue(MalumAttributeRegistry.SWIM_SPEED);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void malum$eventInject(CallbackInfo ci){
		LivingEntityEvent.TICK_EVENT.invoker().react((LivingEntity) (Object)this);
	}
}

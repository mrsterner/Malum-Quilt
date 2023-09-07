package dev.sterner.malum.mixin.common;

import dev.sterner.malum.api.event.LivingEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

	@Inject(method = "addEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerEntityManager;addEntity(Lnet/minecraft/world/entity/EntityLike;)Z"))
	private void malum$eventInject(Entity entity, CallbackInfoReturnable<Boolean> cir){
		if(entity instanceof LivingEntity livingEntity){
			LivingEntityEvent.ADDED_EVENT.invoker().react(livingEntity, false);
		}

	}

	@Inject(method = "spawnNewEntityAndPassengers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
	private void malum$eventInject2(Entity entity, CallbackInfoReturnable<Boolean> cir){
		if(entity instanceof LivingEntity livingEntity){
			LivingEntityEvent.ADDED_EVENT.invoker().react(livingEntity, false);
		}
	}

	@Inject(method = "addPlayer", at = @At("HEAD"))
	private void malum$eventInject(ServerPlayerEntity player, CallbackInfo ci){
		LivingEntityEvent.ADDED_EVENT.invoker().react(player, false);
	}

}

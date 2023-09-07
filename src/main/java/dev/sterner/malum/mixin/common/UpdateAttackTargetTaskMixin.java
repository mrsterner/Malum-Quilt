package dev.sterner.malum.mixin.common;

import dev.sterner.malum.api.event.LivingEntityEvent;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(UpdateAttackTargetTask.class)
public class UpdateAttackTargetTaskMixin {

	@Inject(method = "method_47123",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/MemoryQueryResult;remember(Ljava/lang/Object;)V"))
	private static void malum$injectEvent(Predicate predicate, Function function, MemoryQueryResult memoryQueryResult, MemoryQueryResult memoryQueryResult2, ServerWorld world, MobEntity entity, long time, CallbackInfoReturnable<Boolean> cir){
		LivingEntityEvent.ON_TARGETING_EVENT.invoker().react(entity, null);
	}
}

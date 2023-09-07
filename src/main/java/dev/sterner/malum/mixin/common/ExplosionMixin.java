package dev.sterner.malum.mixin.common;

import dev.sterner.malum.api.event.ExplosionEvent;
import dev.sterner.malum.common.item.equipment.trinket.CurioDemolitionistRing;
import dev.sterner.malum.common.item.equipment.trinket.CurioHoarderRing;
import dev.sterner.malum.common.item.equipment.trinket.CurioProspectorBelt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
	@Unique
	boolean hasEarthenRing;
	@Unique
	ItemStack droppedItem;

	@Shadow
	@Nullable
	public abstract LivingEntity getCausingEntity();

	@Mutable
	@Shadow
	@Final
	private float power;

	@Shadow
	@Final
	private World world;

	@ModifyArg(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getDroppedStacks(Lnet/minecraft/loot/context/LootContextParameterSet$Builder;)Ljava/util/List;"))
	private LootContextParameterSet.Builder malum$getBlockDrops(LootContextParameterSet.Builder builder) {
		return CurioProspectorBelt.applyFortune(getCausingEntity(), builder);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)V", at = @At(value = "RETURN"))
	private void malum$modifyExplosionStats(World pLevel, Entity pSource, DamageSource pDamageSource, ExplosionBehavior pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.DestructionType pBlockInteraction, CallbackInfo ci) {
		power = CurioDemolitionistRing.increaseExplosionRadius(getCausingEntity(), power);
	}

	@Inject(method = "affectWorld", at = @At(value = "HEAD"))
	private void malum$finalizeExplosion(boolean pSpawnParticles, CallbackInfo ci) {
		hasEarthenRing = CurioHoarderRing.hasHoarderRing(getCausingEntity());
	}

	@ModifyArg(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"), index = 2)
	private ItemStack malum$popResourceCache(ItemStack pStack) {
		return droppedItem = pStack;
	}

	@ModifyArg(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"), index = 1)
	private BlockPos malum$popResource(BlockPos value) {
		return CurioHoarderRing.getExplosionPos(hasEarthenRing, value, getCausingEntity(), droppedItem);
	}

	@Inject(method = "collectBlocksAndDamageEntities", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	public void malum$onExplode(CallbackInfo ci, Set<BlockPos> blocks, int i, float j, int k, int l, int d, int q, int e, int r, List<Entity> list) {//Dont fix the red squiggly lines, they have to be broken
		ExplosionEvent.DETONATE.invoker().onDetonate(this.world, (Explosion) (Object) this, list, j);
	}
}

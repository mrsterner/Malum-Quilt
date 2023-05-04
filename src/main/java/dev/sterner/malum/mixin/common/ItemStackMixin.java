package dev.sterner.malum.mixin.common;

import dev.sterner.malum.common.item.tools.MalumScytheItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.sammy.lodestone.setup.LodestoneAttributeRegistry.MAGIC_PROFICIENCY;
import static dev.sterner.malum.common.registry.MalumAttributeRegistry.MAGIC_DAMAGE_MODIFIER_ID;
import static dev.sterner.malum.common.registry.MalumAttributeRegistry.SCYTHE_PROFICIENCY;
import static net.minecraft.item.Item.ATTACK_DAMAGE_MODIFIER_ID;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Unique
	private EntityAttributeModifier entityAttributeModifier;


	@ModifyVariable(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getId()Ljava/util/UUID;", ordinal = 0), index = 13)
	private EntityAttributeModifier malum$capture(EntityAttributeModifier value) {
		this.entityAttributeModifier = value;
		return value;
	}

	@ModifyVariable(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getOperation()Lnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;", ordinal = 0), index = 18)
	private boolean malum$getTooltip(boolean value, @Nullable PlayerEntity player, TooltipContext context) {
		if (player != null && entityAttributeModifier.getId() == MAGIC_DAMAGE_MODIFIER_ID) {
			return true;
		}
		return value;
	}

	@ModifyArgs(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/text/DecimalFormat;format(D)Ljava/lang/String;", ordinal = 0))
	private void malum$magicProficiencyMultiplier(Args args, @Nullable PlayerEntity player, TooltipContext context) {
		if (player == null) {
			return;
		}

		if (entityAttributeModifier.getId() == MAGIC_DAMAGE_MODIFIER_ID && player.getAttributes().hasAttribute(MAGIC_PROFICIENCY)) {
			args.set(0, (double) args.get(0) + 0.5f * player.getAttributeValue(MAGIC_PROFICIENCY));
		} else if (entityAttributeModifier.getId() == ATTACK_DAMAGE_MODIFIER_ID && (((ItemStack) (Object) this).getItem() instanceof MalumScytheItem) && player.getAttributes().hasAttribute(SCYTHE_PROFICIENCY)) {
			args.set(0, (double) args.get(0) + 0.5f * player.getAttributeValue(SCYTHE_PROFICIENCY));
		}
	}
}

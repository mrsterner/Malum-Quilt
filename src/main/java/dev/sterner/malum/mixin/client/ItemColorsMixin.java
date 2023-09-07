package dev.sterner.malum.mixin.client;

import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Function;

import static dev.sterner.malum.common.registry.MalumObjects.*;

@Mixin(ItemColors.class)
public class ItemColorsMixin {

	private static int getOrDefaultInt(Function<NbtCompound, Integer> getter, int defaultValue, NbtCompound nbt) {
		try {
			return getter.apply(nbt);
		} catch (Exception ignored) {
			return defaultValue;
		}
	}

	private static int getOrThrowInt(NbtCompound nbt, String value) {
		if (!nbt.contains(value)) {
			throw new NullPointerException();
		}
		return nbt.getInt(value);
	}

	@Inject(method = "create", at = @At(value = "RETURN", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void malum$create(BlockColors blockColors, CallbackInfoReturnable<ItemColors> cir, ItemColors itemColors) {
		itemColors.register((stack, tintIndex) -> {
			if (tintIndex != 0) return -1;
			return getOrDefaultInt(nbt -> getOrThrowInt(nbt.getCompound("display"), "FirstColor"), 15712278, stack.getNbt());
		}, ETHER, ETHER_TORCH, TAINTED_ETHER_BRAZIER, TWISTED_ETHER_BRAZIER);
		itemColors.register((stack, tintIndex) -> {
			if (tintIndex == 1) return -1;
			if (tintIndex == 0) {
				return getOrDefaultInt(nbt -> getOrThrowInt(nbt.getCompound("display"), "FirstColor"), 15712278, stack.getNbt());
			}
			return getOrDefaultInt(nbt -> getOrThrowInt(nbt.getCompound("display"), "SecondColor"), 4607909, stack.getNbt());
		}, IRIDESCENT_ETHER_TORCH, TAINTED_IRIDESCENT_ETHER_BRAZIER, TWISTED_IRIDESCENT_ETHER_BRAZIER);
		itemColors.register((stack, tintIndex) -> {
			if (tintIndex == -1) return -1;
			if (tintIndex == 0) {
				return getOrDefaultInt(nbt -> getOrThrowInt(nbt.getCompound("display"), "FirstColor"), 15712278, stack.getNbt());
			}
			return getOrDefaultInt(nbt -> getOrThrowInt(nbt.getCompound("display"), "SecondColor"), 4607909, stack.getNbt());
		}, IRIDESCENT_ETHER);
		itemColors.register((stack, tintIndex) -> {
			if (tintIndex != 0) return -1;
			return 251 << 16 | 193 << 8 | 76;
		}, RUNEWOOD_LEAVES);
		itemColors.register((stack, tintIndex) -> {
			if (tintIndex != 0) return -1;
			return 224 << 16 | 30 << 8 | 214;
		}, SOULWOOD_LEAVES);
		itemColors.register((stack, tintIndex) -> MalumSpiritTypeRegistry.SACRED_SPIRIT.getColor().getRGB(), SACRED_SPIRIT);
		itemColors.register((stack, tintIndex) -> MalumSpiritTypeRegistry.AERIAL_SPIRIT.getColor().getRGB(), AERIAL_SPIRIT);
		itemColors.register((stack, tintIndex) -> MalumSpiritTypeRegistry.AQUEOUS_SPIRIT.getColor().getRGB(), AQUEOUS_SPIRIT);
		itemColors.register((stack, tintIndex) -> MalumSpiritTypeRegistry.ARCANE_SPIRIT.getColor().getRGB(), ARCANE_SPIRIT);
		itemColors.register((stack, tintIndex) -> MalumSpiritTypeRegistry.ELDRITCH_SPIRIT.getColor().getRGB(), ELDRITCH_SPIRIT);
		itemColors.register((stack, tintIndex) -> MalumSpiritTypeRegistry.INFERNAL_SPIRIT.getColor().getRGB(), INFERNAL_SPIRIT);
		itemColors.register((stack, tintIndex) -> MalumSpiritTypeRegistry.EARTHEN_SPIRIT.getColor().getRGB(), EARTHEN_SPIRIT);
		itemColors.register((stack, tintIndex) -> MalumSpiritTypeRegistry.WICKED_SPIRIT.getColor().getRGB(), WICKED_SPIRIT);
	}

}

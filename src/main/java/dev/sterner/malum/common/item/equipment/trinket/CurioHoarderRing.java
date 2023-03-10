package dev.sterner.malum.common.item.equipment.trinket;

import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class CurioHoarderRing extends TrinketItem {
    public CurioHoarderRing(Settings settings) {
        super(settings);
    }

	public static boolean hasHoarderRing(LivingEntity entity) {
		return TrinketsApi.getTrinketComponent(entity).map(trinketComponent -> trinketComponent.isEquipped(MalumObjects.RING_OF_THE_HOARDER)).orElse(false);
	}

    public static BlockPos getExplosionPos(boolean hasTheRing, BlockPos originalPos, LivingEntity entity, ItemStack droppedItem) {
        if (hasTheRing) {
            ItemStack itemInHand = entity.getStackInHand(Hand.OFF_HAND);
            if (!itemInHand.isEmpty()) {
                if (!droppedItem.getItem().equals(itemInHand.getItem())) {
                    return originalPos;
                }
            }
        }
        return hasTheRing ? entity.getBlockPos().up() : originalPos;
    }
}

package dev.sterner.malum.common.item.equipment.trinket;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.sterner.malum.api.interfaces.item.SpiritCollectActivity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class CurioRingOfProwess extends TrinketItem implements SpiritCollectActivity {
    public CurioRingOfProwess(Settings settings) {
        super(settings);
    }

    @Override
    public void collect(ItemStack spirit, LivingEntity livingEntity, SlotReference slot, ItemStack trinket, double arcaneResonance) {
        int i = 1 + livingEntity.getWorld().random.nextInt(1) + livingEntity.getWorld().random.nextInt(2);

        while (i > 0) {
            int j = ExperienceOrbEntity.roundToOrbSize(i);
            i -= j;
            livingEntity.getWorld().spawnEntity(new ExperienceOrbEntity(livingEntity.getWorld(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), j));
        }
    }
}

package dev.sterner.malum.common.item.equipment.trinket;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.sterner.lodestone.helpers.EntityHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.sterner.malum.api.interfaces.item.SpiritCollectActivity;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.registry.MalumStatusEffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.UUID;

public class CurioStarvedBelt extends TrinketItem implements SpiritCollectActivity {
    public CurioStarvedBelt(Settings settings) {
        super(settings);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<EntityAttribute, EntityAttributeModifier> map = HashMultimap.create();
        map.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "Curio armor boost", 2, EntityAttributeModifier.Operation.ADDITION));
        return map;
    }

    @Override
    public void collect(ItemStack spirit, LivingEntity livingEntity, SlotReference slot, ItemStack trinket, double arcaneResonance) {
        var gluttony = MalumStatusEffectRegistry.GLUTTONY;
        var effect = livingEntity.getStatusEffect(gluttony);
        if (effect == null) {
            livingEntity.addStatusEffect(new StatusEffectInstance(gluttony, 100+(int)(arcaneResonance*25), 0, true, true, true));
        } else {
            EntityHelper.extendEffect(effect, livingEntity, 50, 200+(int)(arcaneResonance*50));
            EntityHelper.amplifyEffect(effect, livingEntity, 1, 9+(int)(arcaneResonance*5));
        }
        World world = livingEntity.getWorld();
        world.playSound(null, livingEntity.getBlockPos(), MalumSoundRegistry.HUNGRY_BELT_FEEDS, SoundCategory.PLAYERS, 0.7f, 1.5f + world.random.nextFloat() * 0.5f);
        world.playSound(null, livingEntity.getBlockPos(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.7f, 0.8f + world.random.nextFloat() * 0.4f);
    }
}

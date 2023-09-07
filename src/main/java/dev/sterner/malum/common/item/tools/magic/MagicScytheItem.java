package dev.sterner.malum.common.item.tools.magic;

import com.google.common.collect.ImmutableMultimap;
import dev.sterner.malum.common.item.tools.MalumScytheItem;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ToolMaterial;

import static dev.sterner.lodestone.setup.LodestoneAttributeRegistry.MAGIC_DAMAGE;
import static dev.sterner.malum.common.registry.MalumAttributeRegistry.MAGIC_DAMAGE_MODIFIER_ID;


public class MagicScytheItem extends MalumScytheItem {
    public final float magicDamage;
    public MagicScytheItem(ToolMaterial material, float damage, float speed, float magic, Settings settings) {
        super(material, damage, speed, settings);
        this.magicDamage = magic;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
            EntityAttributes.GENERIC_ATTACK_SPEED,
            new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", speed, EntityAttributeModifier.Operation.ADDITION)
        );
        if (magic > 0.0f) {
            builder.put(
                MAGIC_DAMAGE,
                new EntityAttributeModifier(MAGIC_DAMAGE_MODIFIER_ID, "Weapon modifier", magicDamage, EntityAttributeModifier.Operation.ADDITION)
            );
        }
        this.attributeModifiers = builder.build();
    }


}

package dev.sterner.malum.common.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static dev.sterner.malum.Malum.MODID;

public interface MalumAttributeRegistry {

	Map<Identifier, EntityAttribute> ATTRIBUTES = new LinkedHashMap<>();

	EntityAttribute SCYTHE_PROFICIENCY = register("scythe_proficiency", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".scythe_proficiency", 0.0D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute SPIRIT_SPOILS = register("spirit_spoils", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".spirit_spoils", 0.0D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute SPIRIT_REACH = register("spirit_reach", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".spirit_reach", 5D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute HEART_OF_STONE_STRENGTH = register("heart_of_stone_strength", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".heart_of_stone_strength", 2D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute HEART_OF_STONE_RECOVERY_SPEED = register("heart_of_stone_recovery_speed", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".heart_of_stone_recovery_speed", 0D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute HEART_OF_STONE_COST = register("heart_of_stone_hunger_cost", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".heart_of_stone_hunger_cost", 0D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute HEART_OF_STONE_CAP = register("heart_of_stone_capacity", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".heart_of_stone_capacity", 0D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute SOUL_WARD_STRENGTH = register("soul_ward_strength", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".soul_ward_strength", 1D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute SOUL_WARD_RECOVERY_SPEED = register("soul_ward_recovery_speed", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".soul_ward_recovery_speed", 0D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute SOUL_WARD_SHATTER_COOLDOWN = register("soul_ward_shatter_cooldown", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".soul_ward_shatter_cooldown", 0.0D, 0.0D, 2048.0D).setTracked(true));
	EntityAttribute SOUL_WARD_CAP = register("soul_ward_capacity", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".soul_ward_capacity", 0D, 0.0D, 2048.0D).setTracked(true));

	EntityAttribute ARCANE_RESONANCE = register("arcane_resonance", new ClampedEntityAttribute("attribute.name.generic." + MODID + ".arcane_resonance", 0.0D, 0.0D, 2048.0D).setTracked(true));




	EntityAttribute SWIM_SPEED = register("swim_speed", new ClampedEntityAttribute("attribute.name.generic" + MODID + ".swim_speed", 1.0D, 0.0D, 1024D).setTracked(true));


	UUID MAGIC_DAMAGE_MODIFIER_ID = UUID.fromString("763FFDF7-B4CA-4802-BD58-9FD9B9D76622");

	static <T extends EntityAttribute> EntityAttribute register(String id, EntityAttribute attribute) {
		ATTRIBUTES.put(new Identifier(MODID, id), attribute);
		return attribute;
	}
	static void init() {
		ATTRIBUTES.forEach((id, attribute) -> Registry.register(Registries.ATTRIBUTE, id, attribute));
	}
}

package dev.sterner.malum.common.registry;

import dev.sterner.malum.Malum;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface MalumSoundRegistry {
	Map<Identifier, SoundEvent> SOUND_EVENTS = new LinkedHashMap<>();

	SoundEvent ARCANA_CODEX_OPEN = register(("arcana_codex_opened"));
	SoundEvent ARCANA_CODEX_CLOSE = register(("arcana_codex_closed"));
	SoundEvent ARCANA_ENTRY_OPEN = register(("arcana_entry_opened"));
	SoundEvent ARCANA_ENTRY_CLOSE = register(("arcana_entry_closed"));
	SoundEvent ARCANA_PAGE_FLIP = register(("arcana_page_flipped"));
	SoundEvent ARCANA_SWEETENER_NORMAL = register(("arcana_sweetener_normal"));
	SoundEvent ARCANA_SWEETENER_EVIL = register(("arcana_sweetener_evil"));
	SoundEvent ARCANA_TRANSITION_NORMAL = register(("arcana_transition_normal"));
	SoundEvent ARCANA_TRANSITION_EVIL = register(("arcana_transition_evil"));

	SoundEvent ARCANE_WHISPERS = register(("arcane_whispers"));
	SoundEvent SPIRIT_PICKUP = register(("spirit_picked_up"));

	SoundEvent SOULSTONE_BREAK = register("soulstone_break");
	SoundEvent SOULSTONE_PLACE = register("soulstone_place");
	SoundEvent SOULSTONE_STEP = register("soulstone_step");
	SoundEvent SOULSTONE_HIT = register("soulstone_hit");

	SoundEvent DEEPSLATE_SOULSTONE_BREAK = register("deepslate_soulstone_break");
	SoundEvent DEEPSLATE_SOULSTONE_PLACE = register("deepslate_soulstone_place");
	SoundEvent DEEPSLATE_SOULSTONE_STEP = register("deepslate_soulstone_step");
	SoundEvent DEEPSLATE_SOULSTONE_HIT = register("deepslate_soulstone_hit");

	SoundEvent ARCANE_CHARCOAL_BREAK = register("arcane_charcoal_block_break");
	SoundEvent ARCANE_CHARCOAL_STEP = register("arcane_charcoal_block_step");
	SoundEvent ARCANE_CHARCOAL_PLACE = register("arcane_charcoal_block_place");
	SoundEvent ARCANE_CHARCOAL_HIT = register("arcane_charcoal_block_hit");

	SoundEvent BRILLIANCE_BREAK = register("brilliance_break");
	SoundEvent BRILLIANCE_PLACE = register("brilliance_place");

	SoundEvent BLAZING_QUARTZ_ORE_BREAK = register("blazing_quartz_ore_break");
	SoundEvent BLAZING_QUARTZ_ORE_PLACE = register("blazing_quartz_ore_place");

	SoundEvent BLAZING_QUARTZ_BLOCK_BREAK = register("blazing_quartz_block_break");
	SoundEvent BLAZING_QUARTZ_BLOCK_PLACE = register("blazing_quartz_block_place");
	SoundEvent BLAZING_QUARTZ_BLOCK_STEP = register("blazing_quartz_block_step");
	SoundEvent BLAZING_QUARTZ_BLOCK_HIT = register("blazing_quartz_block_hit");

	SoundEvent ARCANE_CHARCOAL_BLOCK_BREAK = register("arcane_charcoal_block_break");
	SoundEvent ARCANE_CHARCOAL_BLOCK_PLACE = register("arcane_charcoal_block_place");
	SoundEvent ARCANE_CHARCOAL_BLOCK_STEP = register("arcane_charcoal_block_step");
	SoundEvent ARCANE_CHARCOAL_BLOCK_HIT = register("arcane_charcoal_block_hit");

	SoundEvent TAINTED_ROCK_BREAK = register("tainted_rock_break");
	SoundEvent TAINTED_ROCK_PLACE = register("tainted_rock_place");
	SoundEvent TAINTED_ROCK_STEP = register("tainted_rock_step");
	SoundEvent TAINTED_ROCK_HIT = register("tainted_rock_hit");

	SoundEvent HALLOWED_GOLD_BREAK = register("hallowed_gold_break");
	SoundEvent HALLOWED_GOLD_HIT = register("hallowed_gold_hit");
	SoundEvent HALLOWED_GOLD_PLACE = register("hallowed_gold_place");
	SoundEvent HALLOWED_GOLD_STEP = register("hallowed_gold_step");

	SoundEvent SOUL_STAINED_STEEL_BREAK = register("soul_stained_steel_break");
	SoundEvent SOUL_STAINED_STEEL_HIT = register("soul_stained_steel_hit");
	SoundEvent SOUL_STAINED_STEEL_PLACE = register("soul_stained_steel_place");
	SoundEvent SOUL_STAINED_STEEL_STEP = register("soul_stained_steel_step");

	SoundEvent ETHER_PLACE = register("ether_place");
	SoundEvent ETHER_BREAK = register("ether_break");

	SoundEvent SCYTHE_CUT = register("scythe_cuts");
	SoundEvent SPIRIT_HARVEST = register("a_soul_shatters");

	SoundEvent TOTEM_CHARGE = register("totem_charges");
	SoundEvent TOTEM_ACTIVATED = register("spirit_rite_activated");
	SoundEvent TOTEM_CANCELLED = register("spirit_rite_cancelled");
	SoundEvent TOTEM_ENGRAVE = register("spirit_engraved");

	SoundEvent ALTAR_CRAFT = register("spirit_altar_completes_infusion");
	SoundEvent ALTAR_LOOP = register("spirit_altar_infuses");
	SoundEvent ALTAR_CONSUME  = register("spirit_altar_absorbs_item");
	SoundEvent ALTAR_SPEED_UP = register("spirit_altar_speeds_up");

	SoundEvent CRUCIBLE_CRAFT = register("spirit_crucible_completes_focusing");
	SoundEvent CRUCIBLE_LOOP = register("spirit_crucible_focuses");
	SoundEvent IMPETUS_CRACK = register("impetus_takes_damage");

	SoundEvent SINISTER_EQUIP = register("ornate_trinket_equipped");
	SoundEvent HOLY_EQUIP = register("gilded_trinket_equipped");

	SoundEvent VOID_SLASH = register("void_slash_swooshes");
	SoundEvent AERIAL_FALL = register("aerial_magic_swooshes");
	SoundEvent HIDDEN_BLADE_STRIKES = register("hidden_blade_strikes");
	SoundEvent HUNGRY_BELT_FEEDS = register("hungry_belt_feeds");
	SoundEvent NITRATE_THROWN = register("nitrate_thrown");


	SoundEvent SOUL_WARD_HIT = register("soul_ward_damaged");
	SoundEvent SOUL_WARD_GROW = register("soul_ward_grows");
	SoundEvent SOUL_WARD_CHARGE = register("soul_ward_charges");

	SoundEvent HEART_OF_STONE_HIT = register("heart_of_stone_hit");
	SoundEvent HEART_OF_STONE_GROW = register("heart_of_stone_grow");

	SoundEvent SUSPICIOUS_SOUND = register("suspicious_sound_plays");


	SoundEvent MAJOR_BLIGHT_MOTIF = register("blight_reacts");
	SoundEvent MINOR_BLIGHT_MOTIF = register("blight_reacts_faintly");

	SoundEvent UNCANNY_VALLEY = register("the_unknown_weeps");
	SoundEvent VOID_HEARTBEAT = register("the_void_heart_beats");
	SoundEvent SONG_OF_THE_VOID = register("song_of_the_void");
	SoundEvent VOID_REJECTION = register("rejected_by_the_unknown");
	SoundEvent VOID_TRANSMUTATION = register("void_transmutation");


	SoundEvent ALTERATION_PLINTH_ALTERS = register("alteration_plinth_alters");

	SoundEvent QUARTZ_CLUSTER_BLOCK_BREAK = register("quartz_cluster_block_break");
	SoundEvent QUARTZ_CLUSTER_BLOCK_PLACE = register("quartz_cluster_block_place");
	SoundEvent QUARTZ_CLUSTER_BLOCK_STEP = register("quartz_cluster_block_step");
	SoundEvent QUARTZ_CLUSTER_BLOCK_HIT = register("quartz_cluster_block_hit");

	SoundEvent SOULWOOD_LEAVES_BREAK = register("soulwood_leaves_break");
	SoundEvent SOULWOOD_LEAVES_HIT = register("soulwood_leaves_hit");
	SoundEvent SOULWOOD_LEAVES_PLACE = register("soulwood_leaves_place");
	SoundEvent SOULWOOD_LEAVES_STEP = register("soulwood_leaves_step");

	SoundEvent RUNEWOOD_LEAVES_BREAK = register("runewood_leaves_break");
	SoundEvent RUNEWOOD_LEAVES_HIT = register("runewood_leaves_hit");
	SoundEvent RUNEWOOD_LEAVES_PLACE = register("runewood_leaves_place");
	SoundEvent RUNEWOOD_LEAVES_STEP = register("runewood_leaves_step");

	SoundEvent CTHONIC_GOLD_BREAK = register("cthonic_gold_break");
	SoundEvent CTHONIC_GOLD_PLACE = register("cthonic_gold_place");

	BlockSoundGroup CTHONIC_GOLD = new BlockSoundGroup(
			1.0F,
			1.0F,
			CTHONIC_GOLD_BREAK,
			SOULSTONE_STEP,
			CTHONIC_GOLD_PLACE,
			DEEPSLATE_SOULSTONE_HIT,
			SoundEvents.BLOCK_AZALEA_LEAVES_FALL
	);

	BlockSoundGroup RUNEWOOD_LEAVES = new BlockSoundGroup(
		1.0F,
		1.0F,
		RUNEWOOD_LEAVES_BREAK,
		RUNEWOOD_LEAVES_STEP,
		RUNEWOOD_LEAVES_PLACE,
		RUNEWOOD_LEAVES_HIT,
		SoundEvents.BLOCK_AZALEA_LEAVES_FALL
	);

	BlockSoundGroup TAINTED_ROCK = new BlockSoundGroup(
		1.0F,
		1.0F,
		TAINTED_ROCK_BREAK,
		TAINTED_ROCK_STEP,
		TAINTED_ROCK_PLACE,
		TAINTED_ROCK_HIT,
		SoundEvents.BLOCK_BASALT_FALL
	);

	BlockSoundGroup TWISTED_ROCK = new BlockSoundGroup(
		1.0F,
		1.0F,
		TAINTED_ROCK_BREAK,
		TAINTED_ROCK_STEP,
		TAINTED_ROCK_PLACE,
		TAINTED_ROCK_HIT,
		SoundEvents.BLOCK_BASALT_FALL
	);

	BlockSoundGroup SOULSTONE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SOULSTONE_BREAK,
		SOULSTONE_STEP,
		SOULSTONE_PLACE,
		SOULSTONE_HIT,
		SoundEvents.BLOCK_BASALT_FALL
	);

	BlockSoundGroup DEEPSLATE_SOULSTONE = new BlockSoundGroup(
		1.0F,
		1.0F,
		DEEPSLATE_SOULSTONE_BREAK,
		DEEPSLATE_SOULSTONE_STEP,
		DEEPSLATE_SOULSTONE_PLACE,
		DEEPSLATE_SOULSTONE_HIT,
		SoundEvents.BLOCK_BASALT_FALL
	);

	BlockSoundGroup BLAZING_QUARTZ_ORE = new BlockSoundGroup(
		1.0F,
		1.0F,
		BLAZING_QUARTZ_ORE_BREAK,
		SoundEvents.BLOCK_NETHER_ORE_STEP,
		SoundEvents.BLOCK_NETHER_ORE_PLACE,
		DEEPSLATE_SOULSTONE_HIT,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_FALL
	);

	BlockSoundGroup BLAZING_QUARTZ_BLOCK = new BlockSoundGroup(
		1.0F,
		1.25F,
		BLAZING_QUARTZ_BLOCK_BREAK,
		BLAZING_QUARTZ_BLOCK_STEP,
		BLAZING_QUARTZ_BLOCK_PLACE,
		BLAZING_QUARTZ_BLOCK_HIT,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_FALL
	);

	BlockSoundGroup ARCANE_CHARCOAL_BLOCK = new BlockSoundGroup(
		1.0F,
		1.0F,
		ARCANE_CHARCOAL_BLOCK_BREAK,
		ARCANE_CHARCOAL_BLOCK_STEP,
		ARCANE_CHARCOAL_BLOCK_PLACE,
		ARCANE_CHARCOAL_BLOCK_HIT,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_FALL
	);

	BlockSoundGroup SOULWOOD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_WOOD_BREAK,
		SoundEvents.BLOCK_WOOD_STEP,
		SoundEvents.BLOCK_WOOD_PLACE,
		SoundEvents.BLOCK_WOOD_HIT,
		SoundEvents.BLOCK_WOOD_FALL
	);

	BlockSoundGroup BLIGHTED_EARTH = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NYLIUM_BREAK,
		SoundEvents.BLOCK_NYLIUM_STEP,
		SoundEvents.BLOCK_NYLIUM_PLACE,
		SoundEvents.BLOCK_NYLIUM_HIT,
		SoundEvents.BLOCK_NYLIUM_FALL
	);

	BlockSoundGroup BLIGHTED_FOLIAGE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHER_WART_BREAK,
		SoundEvents.BLOCK_STONE_STEP,
		SoundEvents.ITEM_NETHER_WART_PLANT,
		SoundEvents.BLOCK_STONE_HIT,
		SoundEvents.BLOCK_STONE_FALL
	);

	BlockSoundGroup ETHER = new BlockSoundGroup(
		1.0F,
		1.0F,
		ETHER_BREAK,
		SoundEvents.BLOCK_WOOL_STEP,
		ETHER_PLACE,
		SoundEvents.BLOCK_ANCIENT_DEBRIS_HIT,
		SoundEvents.BLOCK_WOOL_FALL
	);

	BlockSoundGroup HALLOWED_GOLD = new BlockSoundGroup(
		1.0F,
		1.0F,
		HALLOWED_GOLD_BREAK,
		HALLOWED_GOLD_STEP,
		HALLOWED_GOLD_PLACE,
		HALLOWED_GOLD_HIT,
		SoundEvents.BLOCK_METAL_FALL
	);

	BlockSoundGroup SOUL_STAINED_STEEL = new BlockSoundGroup(
		1.0F,
		1.0F,
		SOUL_STAINED_STEEL_BREAK,
		SOUL_STAINED_STEEL_STEP,
		SOUL_STAINED_STEEL_PLACE,
		SOUL_STAINED_STEEL_HIT,
		SoundEvents.BLOCK_METAL_FALL
	);

	BlockSoundGroup NATURAL_QUARTZ = new BlockSoundGroup(
		1.0F,
		0.9f,
		SoundEvents.BLOCK_STONE_BREAK,
		SoundEvents.BLOCK_STONE_STEP,
		SoundEvents.BLOCK_STONE_PLACE,
		SoundEvents.BLOCK_STONE_HIT,
		SoundEvents.BLOCK_STONE_FALL
	);

	BlockSoundGroup QUARTZ_CLUSTER = new BlockSoundGroup(
		1.0F,
		1.0F,
		QUARTZ_CLUSTER_BLOCK_BREAK,
		QUARTZ_CLUSTER_BLOCK_STEP,
		QUARTZ_CLUSTER_BLOCK_PLACE,
		QUARTZ_CLUSTER_BLOCK_HIT,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_FALL
	);

	BlockSoundGroup RARE_EARTH = new BlockSoundGroup(
		1.0F,
		1.15F,
		SOULSTONE_BREAK,
		SOULSTONE_STEP,
		SOULSTONE_PLACE,
		DEEPSLATE_SOULSTONE_HIT,
		SoundEvents.BLOCK_DEEPSLATE_FALL
	);

	BlockSoundGroup DEEPSLATE_QUARTZ = new BlockSoundGroup(
		1.0F,
		0.9f,
		SoundEvents.BLOCK_DEEPSLATE_BREAK,
		SoundEvents.BLOCK_DEEPSLATE_STEP,
		SoundEvents.BLOCK_DEEPSLATE_PLACE,
		SoundEvents.BLOCK_DEEPSLATE_HIT,
		SoundEvents.BLOCK_DEEPSLATE_FALL
	);

	BlockSoundGroup SOULWOOD_LEAVES = new BlockSoundGroup(
		1.0F,
		0.9F,
		SOULWOOD_LEAVES_BREAK,
		SOULWOOD_LEAVES_STEP,
		SOULWOOD_LEAVES_PLACE,
		SOULWOOD_LEAVES_HIT,
		SoundEvents.BLOCK_AZALEA_LEAVES_FALL);

	static SoundEvent register(String name) {
		Identifier id = new Identifier(Malum.MODID, name);
		SoundEvent soundEvent = SoundEvent.of(id);
		SOUND_EVENTS.put(id, soundEvent);
		return soundEvent;
	}


	static void init() {
		SOUND_EVENTS.forEach((id, sound) -> Registry.register(Registries.SOUND_EVENT, id, sound));
	}
}

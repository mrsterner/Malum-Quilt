package dev.sterner.malum.common.event;

import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static dev.sterner.malum.Malum.MODID;
import static dev.sterner.malum.Malum.id;
import static dev.sterner.malum.common.registry.MalumObjects.*;

public class MalumItemGroupEvents {

	public static final RegistryKey<ItemGroup> MALUM = RegistryKey.of(RegistryKeys.ITEM_GROUP, id(MODID));
	public static final RegistryKey<ItemGroup> MALUM_ARCANE_ROCKS = RegistryKey.of(RegistryKeys.ITEM_GROUP, id("malum_shaped_stones"));
	public static final RegistryKey<ItemGroup> MALUM_NATURAL_WONDERS = RegistryKey.of(RegistryKeys.ITEM_GROUP, id("malum_natural_wonders"));
	public static final RegistryKey<ItemGroup> MALUM_SPIRITS = RegistryKey.of(RegistryKeys.ITEM_GROUP, id("malum_spirits"));
	public static final RegistryKey<ItemGroup> MALUM_METALLURGIC_MAGIC = RegistryKey.of(RegistryKeys.ITEM_GROUP, id("malum_metallurgic_magic"));

	public static void init(){
		Registry.register(Registries.ITEM_GROUP, MALUM, FabricItemGroup.builder()
			.icon(() -> new ItemStack(SPIRIT_ALTAR))
			.displayName(Text.translatable(MODID + ".group.malum"))
			.build());
		Registry.register(Registries.ITEM_GROUP, MALUM_ARCANE_ROCKS, FabricItemGroup.builder()
			.icon(() -> new ItemStack(TAINTED_ROCK))
			.displayName(Text.translatable(MODID + ".group.malum_shaped_stones"))
			.build());
		Registry.register(Registries.ITEM_GROUP, MALUM_NATURAL_WONDERS, FabricItemGroup.builder()
			.icon(() -> new ItemStack(RUNEWOOD_SAPLING))
			.displayName(Text.translatable(MODID + ".group.malum_natural_wonders"))
			.build());
		Registry.register(Registries.ITEM_GROUP, MALUM_SPIRITS, FabricItemGroup.builder()
			.icon(() -> new ItemStack(ARCANE_SPIRIT))
			.displayName(Text.translatable(MODID + ".group.malum_spirits"))
			.build());
		Registry.register(Registries.ITEM_GROUP, MALUM_METALLURGIC_MAGIC, FabricItemGroup.builder()
			.icon(() -> new ItemStack(ALCHEMICAL_IMPETUS))
			.displayName(Text.translatable(MODID + ".group.malum_metallurgic_magic"))
			.build());

		ItemGroupEvents.modifyEntriesEvent(MALUM).register(MalumItemGroupEvents::malumGroup);
		ItemGroupEvents.modifyEntriesEvent(MALUM_ARCANE_ROCKS).register(MalumItemGroupEvents::rocksGroup);
		ItemGroupEvents.modifyEntriesEvent(MALUM_NATURAL_WONDERS).register(MalumItemGroupEvents::naturalGroup);
		ItemGroupEvents.modifyEntriesEvent(MALUM_SPIRITS).register(MalumItemGroupEvents::spiritsGroup);
		ItemGroupEvents.modifyEntriesEvent(MALUM_METALLURGIC_MAGIC).register(MalumItemGroupEvents::metallurgicGroup);

	}

	private static void metallurgicGroup(FabricItemGroupEntries e) {
		e.add(CRACKED_IRON_IMPETUS);
		e.add(IRON_IMPETUS);
		e.add(IRON_NODE);

		e.add(CRACKED_COPPER_IMPETUS);
		e.add(COPPER_IMPETUS);
		e.add(COPPER_NODE);

		e.add(CRACKED_GOLD_IMPETUS);
		e.add(GOLD_IMPETUS);
		e.add(GOLD_NODE);

		e.add(CRACKED_SILVER_IMPETUS);
		e.add(SILVER_IMPETUS);
		e.add(SILVER_NODE);

		e.add(CRACKED_ZINC_IMPETUS);
		e.add(ZINC_IMPETUS);
		e.add(ZINC_NODE);

		e.add(CRACKED_ALCHEMICAL_IMPETUS);
		e.add(ALCHEMICAL_IMPETUS);
		//TODO add compat, lead, aluminium, nickel, uranium, tin

	}

	private static void naturalGroup(FabricItemGroupEntries e) {
		e.add(HOLY_SAP);
		e.add(HOLY_SAPBALL);
		e.add(HOLY_SYRUP);
		e.add(RUNEWOOD_LEAVES);
		e.add(RUNEWOOD_SAPLING);
		e.add(RUNEWOOD_LOG);
		e.add(STRIPPED_RUNEWOOD_LOG);
		e.add(RUNEWOOD);
		e.add(STRIPPED_RUNEWOOD);
		e.add(EXPOSED_RUNEWOOD_LOG);
		e.add(REVEALED_RUNEWOOD_LOG);
		e.add(RUNEWOOD_PLANKS);
		e.add(VERTICAL_RUNEWOOD_PLANKS);
		e.add(RUNEWOOD_PANEL);
		e.add(RUNEWOOD_TILES);
		e.add(RUNEWOOD_PLANKS_SLAB);
		e.add(VERTICAL_RUNEWOOD_PLANKS_SLAB);
		e.add(RUNEWOOD_PANEL_SLAB);
		e.add(RUNEWOOD_TILES_SLAB);
		e.add(RUNEWOOD_PLANKS_STAIRS);
		e.add(VERTICAL_RUNEWOOD_PLANKS_STAIRS);
		e.add(RUNEWOOD_PANEL_STAIRS);
		e.add(RUNEWOOD_TILES_STAIRS);
		e.add(CUT_RUNEWOOD_PLANKS);
		e.add(RUNEWOOD_BEAM);
		e.add(RUNEWOOD_DOOR);
		e.add(RUNEWOOD_TRAPDOOR);
		e.add(SOLID_RUNEWOOD_TRAPDOOR);
		e.add(RUNEWOOD_PLANKS_BUTTON);
		e.add(RUNEWOOD_PLANKS_PRESSURE_PLATE);
		e.add(RUNEWOOD_PLANKS_FENCE);
		e.add(RUNEWOOD_PLANKS_FENCE_GATE);
		e.add(RUNEWOOD_ITEM_STAND);
		e.add(RUNEWOOD_ITEM_PEDESTAL);
		//TODO SIGN
		//TODO BOAT
		e.add(BLIGHTED_EARTH);
		e.add(BLIGHTED_SOIL);
		e.add(BLIGHTED_WEED);
		e.add(BLIGHTED_TUMOR);
		e.add(BLIGHTED_SOULWOOD);
		e.add(BLIGHTED_GUNK);
		e.add(UNHOLY_SAP);
		e.add(UNHOLY_SAPBALL);
		e.add(UNHOLY_SYRUP);
		e.add(SOULWOOD_LEAVES);
		e.add(SOULWOOD_LOG);
		e.add(STRIPPED_SOULWOOD_LOG);
		e.add(SOULWOOD);
		e.add(STRIPPED_SOULWOOD);
		e.add(EXPOSED_SOULWOOD_LOG);
		e.add(REVEALED_SOULWOOD_LOG);
		e.add(SOULWOOD_PLANKS);
		e.add(VERTICAL_SOULWOOD_PLANKS);
		e.add(SOULWOOD_PANEL);
		e.add(SOULWOOD_TILES);
		e.add(SOULWOOD_PLANKS_SLAB);
		e.add(VERTICAL_SOULWOOD_PLANKS_SLAB);
		e.add(SOULWOOD_PANEL_SLAB);
		e.add(SOULWOOD_TILES_SLAB);
		e.add(SOULWOOD_PLANKS_STAIRS);
		e.add(VERTICAL_SOULWOOD_PLANKS_STAIRS);
		e.add(SOULWOOD_PANEL_STAIRS);
		e.add(SOULWOOD_TILES_STAIRS);
		e.add(CUT_SOULWOOD_PLANKS);
		e.add(SOULWOOD_BEAM);
		e.add(SOULWOOD_DOOR);
		e.add(SOULWOOD_TRAPDOOR);
		e.add(SOLID_SOULWOOD_TRAPDOOR);
		e.add(SOULWOOD_PLANKS_BUTTON);
		e.add(SOULWOOD_PLANKS_PRESSURE_PLATE);
		e.add(SOULWOOD_PLANKS_FENCE);
		e.add(SOULWOOD_PLANKS_FENCE_GATE);
		e.add(SOULWOOD_ITEM_STAND);
		e.add(SOULWOOD_ITEM_PEDESTAL);
		//TODO SOUL SIGN
		//TODO SOUL BOAT
	}

	private static void rocksGroup(FabricItemGroupEntries e) {
		e.add(TAINTED_ROCK);
		e.add(SMOOTH_TAINTED_ROCK);
		e.add(POLISHED_TAINTED_ROCK);
		e.add(TAINTED_ROCK_BRICKS);
		e.add(CRACKED_TAINTED_ROCK_BRICKS);
		e.add(TAINTED_ROCK_TILES);
		e.add(CRACKED_TAINTED_ROCK_TILES);
		e.add(SMALL_TAINTED_ROCK_BRICKS);
		e.add(CRACKED_SMALL_TAINTED_ROCK_BRICKS);
		e.add(TAINTED_ROCK_COLUMN);
		e.add(TAINTED_ROCK_COLUMN_CAP);
		e.add(CUT_TAINTED_ROCK);
		e.add(CHISELED_TAINTED_ROCK);
		e.add(TAINTED_ROCK_PRESSURE_PLATE);
		e.add(TAINTED_ROCK_BUTTON);
		e.add(TAINTED_ROCK_WALL);
		e.add(TAINTED_ROCK_BRICKS_WALL);
		e.add(CRACKED_TAINTED_ROCK_BRICKS_WALL);
		e.add(TAINTED_ROCK_TILES_WALL);
		e.add(CRACKED_TAINTED_ROCK_TILES_WALL);
		e.add(SMALL_TAINTED_ROCK_BRICKS_WALL);
		e.add(CRACKED_SMALL_TAINTED_ROCK_BRICKS_WALL);
		e.add(TAINTED_ROCK_SLAB);
		e.add(SMOOTH_TAINTED_ROCK_SLAB);
		e.add(POLISHED_TAINTED_ROCK_SLAB);
		e.add(TAINTED_ROCK_BRICKS_SLAB);
		e.add(CRACKED_TAINTED_ROCK_BRICKS_SLAB);
		e.add(TAINTED_ROCK_TILES_SLAB);
		e.add(CRACKED_TAINTED_ROCK_TILES_SLAB);
		e.add(SMALL_TAINTED_ROCK_BRICKS_SLAB);
		e.add(CRACKED_SMALL_TAINTED_ROCK_BRICKS_SLAB);
		e.add(TAINTED_ROCK_STAIRS);
		e.add(SMOOTH_TAINTED_ROCK_STAIRS);
		e.add(POLISHED_TAINTED_ROCK_STAIRS);
		e.add(TAINTED_ROCK_BRICKS_STAIRS);
		e.add(CRACKED_TAINTED_ROCK_BRICKS_STAIRS);
		e.add(TAINTED_ROCK_TILES_STAIRS);
		e.add(CRACKED_TAINTED_ROCK_TILES_STAIRS);
		e.add(SMALL_TAINTED_ROCK_BRICKS_STAIRS);
		e.add(CRACKED_SMALL_TAINTED_ROCK_BRICKS_STAIRS);
		e.add(TAINTED_ROCK_ITEM_STAND);
		e.add(TAINTED_ROCK_ITEM_PEDESTAL);

		e.add(TWISTED_ROCK);
		e.add(SMOOTH_TWISTED_ROCK);
		e.add(POLISHED_TWISTED_ROCK);
		e.add(TWISTED_ROCK_BRICKS);
		e.add(CRACKED_TWISTED_ROCK_BRICKS);
		e.add(TWISTED_ROCK_TILES);
		e.add(CRACKED_TWISTED_ROCK_TILES);
		e.add(SMALL_TWISTED_ROCK_BRICKS);
		e.add(CRACKED_SMALL_TWISTED_ROCK_BRICKS);
		e.add(TWISTED_ROCK_COLUMN);
		e.add(TWISTED_ROCK_COLUMN_CAP);
		e.add(CUT_TWISTED_ROCK);
		e.add(CHISELED_TWISTED_ROCK);
		e.add(TWISTED_ROCK_PRESSURE_PLATE);
		e.add(TWISTED_ROCK_BUTTON);
		e.add(TWISTED_ROCK_WALL);
		e.add(TWISTED_ROCK_BRICKS_WALL);
		e.add(CRACKED_TWISTED_ROCK_BRICKS_WALL);
		e.add(TWISTED_ROCK_TILES_WALL);
		e.add(CRACKED_TWISTED_ROCK_TILES_WALL);
		e.add(SMALL_TWISTED_ROCK_BRICKS_WALL);
		e.add(CRACKED_SMALL_TWISTED_ROCK_BRICKS_WALL);
		e.add(TWISTED_ROCK_SLAB);
		e.add(SMOOTH_TWISTED_ROCK_SLAB);
		e.add(POLISHED_TWISTED_ROCK_SLAB);
		e.add(TWISTED_ROCK_BRICKS_SLAB);
		e.add(CRACKED_TWISTED_ROCK_BRICKS_SLAB);
		e.add(TWISTED_ROCK_TILES_SLAB);
		e.add(CRACKED_TWISTED_ROCK_TILES_SLAB);
		e.add(SMALL_TWISTED_ROCK_BRICKS_SLAB);
		e.add(CRACKED_SMALL_TWISTED_ROCK_BRICKS_SLAB);
		e.add(TWISTED_ROCK_STAIRS);
		e.add(SMOOTH_TWISTED_ROCK_STAIRS);
		e.add(POLISHED_TWISTED_ROCK_STAIRS);
		e.add(TWISTED_ROCK_BRICKS_STAIRS);
		e.add(CRACKED_TWISTED_ROCK_BRICKS_STAIRS);
		e.add(TWISTED_ROCK_TILES_STAIRS);
		e.add(CRACKED_TWISTED_ROCK_TILES_STAIRS);
		e.add(SMALL_TWISTED_ROCK_BRICKS_STAIRS);
		e.add(CRACKED_SMALL_TWISTED_ROCK_BRICKS_STAIRS);
		e.add(TWISTED_ROCK_ITEM_STAND);
		e.add(TWISTED_ROCK_ITEM_PEDESTAL);

	}

	private static void spiritsGroup(FabricItemGroupEntries entries) {
		ITEMS.forEach((item, identifier) -> {
			if(item instanceof MalumSpiritItem){
				entries.add(item);
			}
		});
	}

	private static void malumGroup(FabricItemGroupEntries entries) {
		entries.add(ENCYCLOPEDIA_ARCANA);

		entries.add(ARCANE_CHARCOAL);
		entries.add(ARCANE_CHARCOAL_FRAGMENT);
		entries.add(BLOCK_OF_ARCANE_CHARCOAL);

		entries.add(BLAZING_QUARTZ_ORE);
		entries.add(BLAZING_QUARTZ);
		entries.add(BLAZING_QUARTZ_FRAGMENT);
		entries.add(BLOCK_OF_BLAZING_QUARTZ);

		entries.add(NATURAL_QUARTZ_ORE);
		entries.add(NATURAL_QUARTZ_CLUSTER_BLOCK);
		entries.add(BUDDING_NATURAL_QUARTZ);
		entries.add(DEEPSLATE_QUARTZ_ORE);
		entries.add(NATURAL_QUARTZ);

		entries.add(BRILLIANT_STONE);
		entries.add(BRILLIANT_DEEPSLATE);
		entries.add(CLUSTER_OF_BRILLIANCE);
		entries.add(CRUSHED_BRILLIANCE);
		entries.add(BLOCK_OF_BRILLIANCE);

		entries.add(SOULSTONE_ORE);
		entries.add(DEEPSLATE_SOULSTONE_ORE);
		entries.add(RAW_SOULSTONE);
		entries.add(CRUSHED_SOULSTONE);
		entries.add(BLOCK_OF_RAW_SOULSTONE);
		entries.add(PROCESSED_SOULSTONE);
		entries.add(BLOCK_OF_SOULSTONE);

		entries.add(SPIRIT_ALTAR);
		entries.add(SPIRIT_JAR);
		entries.add(RUNEWOOD_OBELISK);
		entries.add(BRILLIANT_OBELISK);
		entries.add(SPIRIT_CRUCIBLE);
		entries.add(TWISTED_TABLET);
		entries.add(SPIRIT_CATALYZER);
		entries.add(RUNEWOOD_TOTEM_BASE);
		entries.add(SOULWOOD_TOTEM_BASE);

		entries.add(ROTTING_ESSENCE);
		entries.add(GRIM_TALC);
		entries.add(ALCHEMICAL_CALX);
		entries.add(ASTRAL_WEAVE);
		entries.add(CTHONIC_GOLD);
		entries.add(HEX_ASH);
		entries.add(CURSED_GRIT);
		entries.add(BLOCK_OF_ROTTING_ESSENCE);
		entries.add(BLOCK_OF_GRIM_TALC);
		entries.add(BLOCK_OF_ALCHEMICAL_CALX);
		entries.add(BLOCK_OF_ASTRAL_WEAVE);
		entries.add(BLOCK_OF_CTHONIC_GOLD);
		entries.add(BLOCK_OF_HEX_ASH);
		entries.add(BLOCK_OF_CURSED_GRIT);

		entries.add(SPIRIT_FABRIC);
		entries.add(SPECTRAL_LENS);
		entries.add(POPPET);
		entries.add(CORRUPTED_RESONANCE);
		entries.add(HALLOWED_GOLD_INGOT);
		entries.add(HALLOWED_GOLD_NUGGET);
		entries.add(BLOCK_OF_HALLOWED_GOLD);
		entries.add(HALLOWED_SPIRIT_RESONATOR);

		entries.add(SOUL_STAINED_STEEL_INGOT);
		entries.add(SOUL_STAINED_STEEL_NUGGET);
		entries.add(BLOCK_OF_SOUL_STAINED_STEEL);
		entries.add(STAINED_SPIRIT_RESONATOR);

		entries.add(ETHER);
		entries.add(ETHER_TORCH);
		entries.add(TAINTED_ETHER_BRAZIER);
		entries.add(TWISTED_ETHER_BRAZIER);
		entries.add(IRIDESCENT_ETHER);
		entries.add(IRIDESCENT_ETHER_TORCH);
		entries.add(TAINTED_IRIDESCENT_ETHER_BRAZIER);
		entries.add(TWISTED_IRIDESCENT_ETHER_BRAZIER);
		entries.add(SPIRIT_POUCH);

		entries.add(CRUDE_SCYTHE);
		entries.add(SOUL_STAINED_STEEL_SCYTHE);
		entries.add(SOUL_STAINED_STEEL_SWORD);
		entries.add(SOUL_STAINED_STEEL_PICKAXE);
		entries.add(SOUL_STAINED_STEEL_AXE);
		entries.add(SOUL_STAINED_STEEL_SHOVEL);
		entries.add(SOUL_STAINED_STEEL_HOE);

		entries.add(SOUL_STAINED_STEEL_HELMET);
		entries.add(SOUL_STAINED_STEEL_CHESTPLATE);
		entries.add(SOUL_STAINED_STEEL_LEGGINGS);
		entries.add(SOUL_STAINED_STEEL_BOOTS);
		entries.add(SOUL_HUNTER_CLOAK);
		entries.add(SOUL_HUNTER_ROBE);
		entries.add(SOUL_HUNTER_LEGGINGS);
		entries.add(SOUL_HUNTER_BOOTS);

		entries.add(TYRVING);
		entries.add(ETHERIC_NITRATE);
		entries.add(VIVID_NITRATE);

		entries.add(GILDED_RING);
		entries.add(GILDED_BELT);
		entries.add(ORNATE_RING);
		entries.add(ORNATE_NECKLACE);
		entries.add(RING_OF_ESOTERIC_SPOILS);
		entries.add(RING_OF_CURATIVE_TALENT);
		entries.add(RING_OF_ARCANE_PROWESS);
		entries.add(RING_OF_ALCHEMICAL_MASTERY);
		entries.add(RING_OF_DESPERATE_VORACITY);
		entries.add(RING_OF_THE_HOARDER);
		entries.add(RING_OF_THE_DEMOLITIONIST);
		entries.add(NECKLACE_OF_THE_MYSTIC_MIRROR);
		entries.add(NECKLACE_OF_TIDAL_AFFINITY);
		entries.add(NECKLACE_OF_THE_NARROW_EDGE);
		entries.add(NECKLACE_OF_THE_HIDDEN_BLADE);
		entries.add(NECKLACE_OF_BLISSFUL_HARMONY);
		entries.add(BELT_OF_THE_STARVED);
		entries.add(BELT_OF_THE_PROSPECTOR);
		entries.add(BELT_OF_THE_MAGEBANE);
	}
}

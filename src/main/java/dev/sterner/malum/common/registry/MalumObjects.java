package dev.sterner.malum.common.registry;

import dev.sterner.lodestone.systems.block.LodestoneLogBlock;
import dev.sterner.lodestone.systems.item.tools.magic.*;
import dev.sterner.lodestone.systems.multiblock.MultiBlockItem;
import dev.sterner.lodestone.systems.multiblock.MultiBlockStructure;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.block.*;
import dev.sterner.malum.common.block.alteration_plinth.AlterationPlinthBlock;
import dev.sterner.malum.common.block.blight.*;
import dev.sterner.malum.common.block.ether.*;
import dev.sterner.malum.common.block.fusion_plate.FusionPlateComponentBlock;
import dev.sterner.malum.common.block.fusion_plate.FusionPlateCoreBlock;
import dev.sterner.malum.common.block.mirror.EmitterMirrorBlock;
import dev.sterner.malum.common.block.obelisk.BrillianceObeliskCoreBlock;
import dev.sterner.malum.common.block.obelisk.ObeliskComponentBlock;
import dev.sterner.malum.common.block.obelisk.RunewoodObeliskCoreBlock;
import dev.sterner.malum.common.block.sapling.RunewoodSaplingGenerator;
import dev.sterner.malum.common.block.sapling.SoulwoodSaplingGenerator;
import dev.sterner.malum.common.block.spirit_altar.SpiritAltarBlock;
import dev.sterner.malum.common.block.spirit_crucible.SpiritCatalyzerComponentBlock;
import dev.sterner.malum.common.block.spirit_crucible.SpiritCatalyzerCoreBlock;
import dev.sterner.malum.common.block.spirit_crucible.SpiritCrucibleComponentBlock;
import dev.sterner.malum.common.block.spirit_crucible.SpiritCrucibleCoreBlock;
import dev.sterner.malum.common.block.storage.*;
import dev.sterner.malum.common.block.tablet.TwistedTabletBlock;
import dev.sterner.malum.common.block.totem.TotemBaseBlock;
import dev.sterner.malum.common.block.totem.TotemPoleBlock;
import dev.sterner.malum.common.block.weeping_well.PrimordialSoupBlock;
import dev.sterner.malum.common.block.weeping_well.VoidConduitBlock;
import dev.sterner.malum.common.block.weeping_well.WeepingWellBlock;
import dev.sterner.malum.common.blockentity.FusionPlateBlockEntity;
import dev.sterner.malum.common.blockentity.crucible.SpiritCatalyzerCoreBlockEntity;
import dev.sterner.malum.common.blockentity.crucible.SpiritCrucibleCoreBlockEntity;
import dev.sterner.malum.common.blockentity.obelisk.BrilliantObeliskBlockEntity;
import dev.sterner.malum.common.blockentity.obelisk.RunewoodObeliskBlockEntity;
import dev.sterner.malum.common.blockentity.storage.PlinthCoreBlockEntity;
import dev.sterner.malum.common.item.*;
import dev.sterner.malum.common.item.cosmetic.AncientWeaveItem;
import dev.sterner.malum.common.item.equipment.armor.SoulHunterArmorItem;
import dev.sterner.malum.common.item.equipment.armor.SoulStainedSteelArmorItem;
import dev.sterner.malum.common.item.equipment.trinket.*;
import dev.sterner.malum.common.item.ether.EtherBrazierItem;
import dev.sterner.malum.common.item.ether.EtherItem;
import dev.sterner.malum.common.item.ether.EtherTorchItem;
import dev.sterner.malum.common.item.food.HolySyrupItem;
import dev.sterner.malum.common.item.food.UnholySyrupItem;
import dev.sterner.malum.common.item.impedus.CrackedImpetusItem;
import dev.sterner.malum.common.item.impedus.ImpetusItem;
import dev.sterner.malum.common.item.nitrate.EthericNitrateItem;
import dev.sterner.malum.common.item.nitrate.VividNitrateItem;
import dev.sterner.malum.common.item.spirit.*;
import dev.sterner.malum.common.item.tools.MalumScytheItem;
import dev.sterner.malum.common.item.tools.magic.MagicScytheItem;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static dev.sterner.malum.common.item.ItemTiers.ItemTierEnum.SOUL_STAINED_STEEL;
import static dev.sterner.malum.common.registry.MalumBlockProperties.*;
import static dev.sterner.malum.common.registry.MalumObjects.*;
import static net.minecraft.item.Items.GLASS_BOTTLE;

public interface MalumObjects {
	Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
	Map<Supplier<MalumSpiritItem>, String> SPIRITS = new LinkedHashMap<>();
	Set<MalumScytheItem> SCYTHES = new ReferenceOpenHashSet<>();
	ArrayList<WoodType> SIGN_TYPES = new ArrayList<>();

	Item ENCYCLOPEDIA_ARCANA = register("encyclopedia_arcana", new EncyclopediaArcanaItem(settings().rarity(Rarity.UNCOMMON)));

	//region runewood
	Item HOLY_SAP = register("holy_sap", new Item(settings().recipeRemainder(GLASS_BOTTLE)));
	Item HOLY_SAPBALL = register("holy_sapball", new Item(settings()));
	Item HOLY_SYRUP = register("holy_syrup", new HolySyrupItem(settings().recipeRemainder(GLASS_BOTTLE).food((new FoodComponent.Builder()).hunger(6).saturationModifier(0.1F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0), 1).build())));


	Item UNHOLY_SAP = register("unholy_sap", new Item(settings().recipeRemainder(GLASS_BOTTLE)));
	Item UNHOLY_SAPBALL = register("unholy_sapball", new Item(settings()));
	Item UNHOLY_SYRUP = register("unholy_syrup", new UnholySyrupItem(settings().recipeRemainder(GLASS_BOTTLE).food((new FoodComponent.Builder()).hunger(6).saturationModifier(0.1F).statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 0), 1).build())));
	//endregion

	//region spirits
	MalumSpiritItem SACRED_SPIRIT = registerSpirit("sacred_spirit", new MalumSpiritItem(settings(), MalumSpiritTypeRegistry.SACRED_SPIRIT));
	MalumSpiritItem WICKED_SPIRIT = registerSpirit("wicked_spirit", new MalumSpiritItem(settings(), MalumSpiritTypeRegistry.WICKED_SPIRIT));
	MalumSpiritItem ARCANE_SPIRIT = registerSpirit("arcane_spirit", new MalumSpiritItem(settings(), MalumSpiritTypeRegistry.ARCANE_SPIRIT));
	MalumSpiritItem ELDRITCH_SPIRIT = registerSpirit("eldritch_spirit", new MalumSpiritItem(settings(), MalumSpiritTypeRegistry.ELDRITCH_SPIRIT));
	MalumSpiritItem EARTHEN_SPIRIT = registerSpirit("earthen_spirit", new MalumSpiritItem(settings(), MalumSpiritTypeRegistry.EARTHEN_SPIRIT));
	MalumSpiritItem INFERNAL_SPIRIT = registerSpirit("infernal_spirit", new MalumSpiritItem(settings(), MalumSpiritTypeRegistry.INFERNAL_SPIRIT));
	MalumSpiritItem AERIAL_SPIRIT = registerSpirit("aerial_spirit", new MalumSpiritItem(settings(), MalumSpiritTypeRegistry.AERIAL_SPIRIT));
	MalumSpiritItem AQUEOUS_SPIRIT = registerSpirit("aqueous_spirit", new MalumSpiritItem(settings(), MalumSpiritTypeRegistry.AQUEOUS_SPIRIT));
	//endregion

	//region ores
	Item COPPER_NUGGET = register("copper_nugget", new Item(new Item.Settings()));
	Item COAL_FRAGMENT = register("coal_fragment", new Item(new Item.Settings()));
	Item CHARCOAL_FRAGMENT = register("charcoal_fragment", new Item(new Item.Settings()));
	Item ARCANE_CHARCOAL = register("arcane_charcoal", new Item(new Item.Settings()));
	Item ARCANE_CHARCOAL_FRAGMENT = register("arcane_charcoal_fragment", new Item(new Item.Settings()));
	Item BLAZING_QUARTZ = register("blazing_quartz", new Item(new Item.Settings()));
	Item BLAZING_QUARTZ_FRAGMENT = register("blazing_quartz_fragment", new Item(new Item.Settings()));



	Item CLUSTER_OF_BRILLIANCE = register("cluster_of_brilliance", new Item(new Item.Settings()));
	Item CRUSHED_BRILLIANCE = register("crushed_brilliance", new Item(new Item.Settings()));
	Item CHUNK_OF_BRILLIANCE = register("chunk_of_brilliance", new BrillianceChunkItem(new Item.Settings().food((new FoodComponent.Builder()).snack().alwaysEdible().build())));

	Item RAW_SOULSTONE = register("raw_soulstone", new Item(settings()));
	Item CRUSHED_SOULSTONE = register("crushed_soulstone", new Item(settings()));
	Item PROCESSED_SOULSTONE = register("processed_soulstone", new Item(settings()));

	Item ROTTING_ESSENCE = register("rotting_essence", new Item(settings().food((new FoodComponent.Builder()).hunger(6).saturationModifier(0.2F).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 1), 0.95f).build())));
	Item GRIM_TALC = register("grim_talc", new Item(settings()));
	Item ALCHEMICAL_CALX = register("alchemical_calx", new Item(settings()));
	Item ASTRAL_WEAVE = register("astral_weave", new Item(settings()));
	Item RARE_EARTHS = register("rare_earths", new Item(settings()));
	Item HEX_ASH = register("hex_ash", new Item(settings()));
	Item CTHONIC_GOLD = register("cthonic_gold", new Item(settings()));

	Item SPIRIT_FABRIC = register("spirit_fabric", new Item(settings()));
	Item SPECTRAL_LENS = register("spectral_lens", new Item(settings()));
	Item POPPET = register("poppet", new Item(settings()));
	Item CURSED_GRIT = register("cursed_grit", new Item(settings()));
	Item CORRUPTED_RESONANCE = register("corrupted_resonance", new CorruptResonanceItem(settings()));

	Item HALLOWED_GOLD_INGOT = register("hallowed_gold_ingot", new Item(settings()));
	Item HALLOWED_GOLD_NUGGET = register("hallowed_gold_nugget", new Item(settings()));
	Item HALLOWED_SPIRIT_RESONATOR = register("hallowed_spirit_resonator", new Item(settings()));

	Item SOUL_STAINED_STEEL_INGOT = register("soul_stained_steel_ingot", new Item(settings()));
	Item SOUL_STAINED_STEEL_NUGGET = register("soul_stained_steel_nugget", new Item(settings()));
	Item STAINED_SPIRIT_RESONATOR = register("stained_spirit_resonator", new Item(settings()));

	CrackedImpetusItem CRACKED_IRON_IMPETUS = register("cracked_iron_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem IRON_IMPETUS = register("iron_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_IRON_IMPETUS));
	Item IRON_NODE = register("iron_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_COPPER_IMPETUS = register("cracked_copper_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem COPPER_IMPETUS = register("copper_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_COPPER_IMPETUS));
	Item COPPER_NODE = register("copper_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_GOLD_IMPETUS = register("cracked_gold_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem GOLD_IMPETUS = register("gold_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_GOLD_IMPETUS));
	Item GOLD_NODE = register("gold_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_LEAD_IMPETUS = register("cracked_lead_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem LEAD_IMPETUS = register("lead_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_LEAD_IMPETUS));
	Item LEAD_NODE = register("lead_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_SILVER_IMPETUS = register("cracked_silver_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem SILVER_IMPETUS = register("silver_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_SILVER_IMPETUS));
	Item SILVER_NODE = register("silver_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_ALUMINUM_IMPETUS = register("cracked_aluminum_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem ALUMINUM_IMPETUS = register("aluminum_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_ALUMINUM_IMPETUS));
	Item ALUMINUM_NODE = register("aluminum_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_NICKEL_IMPETUS = register("cracked_nickel_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem NICKEL_IMPETUS = register("nickel_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_NICKEL_IMPETUS));
	Item NICKEL_NODE = register("nickel_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_URANIUM_IMPETUS = register("cracked_uranium_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem URANIUM_IMPETUS = register("uranium_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_URANIUM_IMPETUS));
	Item URANIUM_NODE = register("uranium_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_OSMIUM_IMPETUS = register("cracked_osmium_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem OSMIUM_IMPETUS = register("osmium_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_OSMIUM_IMPETUS));
	Item OSMIUM_NODE = register("osmium_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_ZINC_IMPETUS = register("cracked_zinc_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem ZINC_IMPETUS = register("zinc_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_ZINC_IMPETUS));
	Item ZINC_NODE = register("zinc_node", new NodeItem(settings()));
	CrackedImpetusItem CRACKED_TIN_IMPETUS = register("cracked_tin_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem TIN_IMPETUS = register("tin_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_TIN_IMPETUS));
	Item TIN_NODE = register("tin_node", new NodeItem(settings()));

	CrackedImpetusItem CRACKED_ALCHEMICAL_IMPETUS = register("cracked_alchemical_impetus", new CrackedImpetusItem(settings()));
	ImpetusItem ALCHEMICAL_IMPETUS = register("alchemical_impetus", new ImpetusItem(settings().maxDamage(100)).setCrackedVariant(CRACKED_ALCHEMICAL_IMPETUS));



	Item SPIRIT_POUCH = register("spirit_pouch", new SpiritPouchItem(settings()));
	Item CRUDE_SCYTHE = registerScytheItem("crude_scythe", new MalumScytheItem(ToolMaterials.IRON, 0, 0.1f, settings().maxDamage(350)));
	Item SOUL_STAINED_STEEL_SCYTHE = registerScytheItem("soul_stained_steel_scythe", new MagicScytheItem(SOUL_STAINED_STEEL, -2.5f, 0.1f, 4, settings()));
	Item SOULWOOD_STAVE = register("soulwood_stave", new SoulStaveItem(settings()));

	Item SOUL_STAINED_STEEL_SWORD = register("soul_stained_steel_sword", new MagicSwordItem(SOUL_STAINED_STEEL, -3, 0, 3, settings()));
	Item SOUL_STAINED_STEEL_PICKAXE = register("soul_stained_steel_pickaxe", new MagicPickaxeItem(SOUL_STAINED_STEEL, -2, 0, 2, settings()));
	Item SOUL_STAINED_STEEL_AXE = register("soul_stained_steel_axe", new MagicAxeItem(SOUL_STAINED_STEEL, -3f, 0, 4, settings()));
	Item SOUL_STAINED_STEEL_SHOVEL = register("soul_stained_steel_shovel", new MagicShovelItem(SOUL_STAINED_STEEL, -2, 0, 2, settings()));
	Item SOUL_STAINED_STEEL_HOE = register("soul_stained_steel_hoe", new MagicHoeItem(SOUL_STAINED_STEEL, 0, -1.5f, 1, settings()));

	Item SOUL_STAINED_STEEL_HELMET = register("soul_stained_steel_helmet", new SoulStainedSteelArmorItem(ArmorItem.Type.HELMET, settings()));
	Item SOUL_STAINED_STEEL_CHESTPLATE = register("soul_stained_steel_chestplate", new SoulStainedSteelArmorItem(ArmorItem.Type.CHESTPLATE, settings()));
	Item SOUL_STAINED_STEEL_LEGGINGS = register("soul_stained_steel_leggings", new SoulStainedSteelArmorItem(ArmorItem.Type.LEGGINGS, settings()));
	Item SOUL_STAINED_STEEL_BOOTS = register("soul_stained_steel_boots", new SoulStainedSteelArmorItem(ArmorItem.Type.BOOTS, settings()));

	Item SOUL_HUNTER_CLOAK = register("soul_hunter_cloak", new SoulHunterArmorItem(ArmorItem.Type.HELMET, settings()));
	Item SOUL_HUNTER_ROBE = register("soul_hunter_robe", new SoulHunterArmorItem(ArmorItem.Type.CHESTPLATE, settings()));
	Item SOUL_HUNTER_LEGGINGS = register("soul_hunter_leggings", new SoulHunterArmorItem(ArmorItem.Type.LEGGINGS, settings()));
	Item SOUL_HUNTER_BOOTS = register("soul_hunter_boots", new SoulHunterArmorItem(ArmorItem.Type.BOOTS, settings()));

	Item ETHERIC_NITRATE = register("etheric_nitrate", new EthericNitrateItem(settings()));
	Item VIVID_NITRATE = register("vivid_nitrate", new VividNitrateItem(settings()));

	Item TYRVING = register("tyrving", new TyrvingItem(ItemTiers.ItemTierEnum.TYRVING, 0, -0.3f, settings()));

	Item GILDED_RING = register("gilded_ring", new CurioGildedRing(settings()));
	Item GILDED_BELT = register("gilded_belt", new CurioGildedBelt(settings()));
	Item ORNATE_RING = register("ornate_ring", new CurioOrnateRing(settings()));
	Item ORNATE_NECKLACE = register("ornate_necklace", new CurioOrnateNecklace(settings()));

	Item RING_OF_ESOTERIC_SPOILS = register("ring_of_esoteric_spoils", new CurioArcaneSpoilRing(settings()));
	Item RING_OF_CURATIVE_TALENT = register("ring_of_curative_talent", new CurioCurativeRing(settings()));
	Item RING_OF_ARCANE_PROWESS = register("ring_of_arcane_prowess", new CurioRingOfProwess(settings()));
	Item RING_OF_ALCHEMICAL_MASTERY = register("ring_of_alchemical_mastery", new CurioAlchemicalRing(settings()));
	Item RING_OF_DESPERATE_VORACITY = register("ring_of_desperate_voracity", new CurioVoraciousRing(settings()));
	Item RING_OF_THE_HOARDER = register("ring_of_the_hoarder", new CurioHoarderRing(settings()));
	Item RING_OF_THE_DEMOLITIONIST = register("ring_of_the_demolitionist", new CurioDemolitionistRing(settings()));

	Item NECKLACE_OF_THE_MYSTIC_MIRROR = register("necklace_of_the_mystic_mirror", new CurioMirrorNecklace(settings()));
	Item NECKLACE_OF_TIDAL_AFFINITY = register("necklace_of_tidal_affinity", new CurioWaterNecklace(settings()));
	Item NECKLACE_OF_THE_NARROW_EDGE = register("necklace_of_the_narrow_edge", new CurioNarrowNecklace(settings()));
	Item NECKLACE_OF_THE_HIDDEN_BLADE = register("necklace_of_the_hidden_blade", new CurioHiddenBladeNecklace(settings()));
	Item NECKLACE_OF_BLISSFUL_HARMONY = register("necklace_of_blissful_harmony", new CurioHarmonyNecklace(settings()));

	Item BELT_OF_THE_STARVED = register("belt_of_the_starved", new CurioStarvedBelt(settings()));
	Item BELT_OF_THE_PROSPECTOR = register("belt_of_the_prospector", new CurioProspectorBelt(settings()));
	Item BELT_OF_THE_MAGEBANE = register("belt_of_the_magebane", new CurioMagebaneBelt(settings()));


	Item ESOTERIC_SPOOL = register("esoteric_spool", new Item(settings()));
	Item ANCIENT_WEAVE = register("ancient_weave", new AncientWeaveItem(settings()));


	Item CREATIVE_SCYTHE = register("creative_scythe", new MagicScytheItem(ToolMaterials.IRON, 9993, 9.1f, 999f, settings().maxDamage(-1)));
	Item TOKEN_OF_GRATITUDE = register("token_of_gratitude", new CurioTokenOfGratitude(settings()));



	//BLOCKS


	Block SPIRIT_ALTAR = register("spirit_altar", new SpiritAltarBlock<>(RUNEWOOD_PROPERTIES().nonOpaque()), true);
	Block SPIRIT_JAR = registerJar("spirit_jar", new SpiritJarBlock<>(SPIRIT_JAR_PROPERTIES().nonOpaque()), true);
	Block ALTERATION_PLINTH = register("alteration_plinth", new AlterationPlinthBlock<>(SOULWOOD_PROPERTIES().nonOpaque()), true);

	Block SOUL_VIAL = registerVial("soul_vial", new SoulVialBlock<>(SOUL_VIAL_PROPERTIES().nonOpaque()), true);
	Block EMITTER_MIRROR = register("emitter_mirror", new EmitterMirrorBlock<>(HALLOWED_GOLD_PROPERTIES().nonOpaque()), true);

	Block MOTE_OF_MANA = register("mote_of_mana", new Block(TAINTED_ROCK_PROPERTIES().nonOpaque()), true);

	Block TWISTED_TABLET = register("twisted_tablet", new TwistedTabletBlock<>(TAINTED_ROCK_PROPERTIES().nonOpaque()), true);

	Block RUNEWOOD_OBELISK = registerMultiBlock("runewood_obelisk", new RunewoodObeliskCoreBlock(RUNEWOOD_PROPERTIES().nonOpaque()), RunewoodObeliskBlockEntity.STRUCTURE, true);
	Block RUNEWOOD_OBELISK_COMPONENT = register("runewood_obelisk_component", new ObeliskComponentBlock(RUNEWOOD_PROPERTIES().nonOpaque(), Items.AIR), false);

	Block BRILLIANT_OBELISK = registerMultiBlock("brilliant_obelisk", new BrillianceObeliskCoreBlock(RUNEWOOD_PROPERTIES().nonOpaque()), BrilliantObeliskBlockEntity.STRUCTURE, true);
	Block BRILLIANT_OBELISK_COMPONENT = register("brilliant_obelisk_component", new ObeliskComponentBlock(RUNEWOOD_PROPERTIES().nonOpaque(), Items.AIR), false);

	Block SPIRIT_CRUCIBLE = registerMultiBlock("spirit_crucible", new SpiritCrucibleCoreBlock<>(TAINTED_ROCK_PROPERTIES().nonOpaque()), SpiritCrucibleCoreBlockEntity.STRUCTURE, true);
	Block SPIRIT_CRUCIBLE_COMPONENT = register("spirit_crucible_component", new SpiritCrucibleComponentBlock(TAINTED_ROCK_PROPERTIES().nonOpaque()), false);

	Block SPIRIT_CATALYZER = registerMultiBlock("spirit_catalyzer", new SpiritCatalyzerCoreBlock<>(TAINTED_ROCK_PROPERTIES().nonOpaque()), SpiritCatalyzerCoreBlockEntity.STRUCTURE, true);
	Block SPIRIT_CATALYZER_COMPONENT = register("spirit_catalyzer_component", new SpiritCatalyzerComponentBlock(TAINTED_ROCK_PROPERTIES().nonOpaque(), Items.AIR), false);

	Block SOULWOOD_PLINTH = registerMultiBlock("soulwood_plinth", new PlinthCoreBlock<>(SOULWOOD_PROPERTIES().nonOpaque()), PlinthCoreBlockEntity.STRUCTURE, true);
	Block SOULWOOD_PLINTH_COMPONENT = register("soulwood_plinth_component", new PlinthComponentBlock(SOULWOOD_PROPERTIES().nonOpaque()), false);

	Block SOULWOOD_FUSION_PLATE = registerMultiBlock("soulwood_fusion_plate", new FusionPlateCoreBlock<>(SOULWOOD_PROPERTIES().nonOpaque()), FusionPlateBlockEntity.STRUCTURE, true);
	Block SOULWOOD_FUSION_PLATE_COMPONENT = register("soulwood_fusion_plate_component", new FusionPlateComponentBlock(SOULWOOD_PROPERTIES().nonOpaque(), Items.AIR), false);

	Block VOID_CONDUIT = register("void_conduit",new VoidConduitBlock<>(PRIMORDIAL_SOUP_PROPERTIES().nonOpaque()), true);
	Block PRIMORDIAL_SOUP = register("primordial_soup", new PrimordialSoupBlock(PRIMORDIAL_SOUP_PROPERTIES().nonOpaque()), true);
	Block WEEPING_WELL_CORNER = register("weeping_well_corner", new WeepingWellBlock(WEEPING_WELL_PROPERTIES()), true);
	Block WEEPING_WELL_SIDE = register("weeping_well_side", new WeepingWellBlock(WEEPING_WELL_PROPERTIES()), true);
	Block WEEPING_WELL_CORE = register("weeping_well_core", new WeepingWellBlock(WEEPING_WELL_PROPERTIES()), true);

	Block BLOCK_OF_CTHONIC_GOLD = register("block_of_cthonic_gold", new Block(AURUM_PROPERTIES()), true);
	//endregion

	//region tainted rock
	Block TAINTED_ROCK = register("tainted_rock", new Block(TAINTED_ROCK_PROPERTIES()), true);
	Block SMOOTH_TAINTED_ROCK = register("smooth_tainted_rock", new Block(TAINTED_ROCK_PROPERTIES()),true);
	Block POLISHED_TAINTED_ROCK = register("polished_tainted_rock", new Block(TAINTED_ROCK_PROPERTIES()),true);

	Block TAINTED_ROCK_BRICKS = register("tainted_rock_bricks", new Block(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TAINTED_ROCK_BRICKS = register("cracked_tainted_rock_bricks", new Block(TAINTED_ROCK_PROPERTIES()),true);
	Block SMALL_TAINTED_ROCK_BRICKS = register("small_tainted_rock_bricks", new Block(TAINTED_ROCK_PROPERTIES()),true);

	Block TAINTED_ROCK_TILES = register("tainted_rock_tiles", new Block(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TAINTED_ROCK_TILES = register("cracked_tainted_rock_tiles", new Block(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_SMALL_TAINTED_ROCK_BRICKS = register("cracked_small_tainted_rock_bricks", new Block(TAINTED_ROCK_PROPERTIES()),true);

	Block TAINTED_ROCK_COLUMN = register("tainted_rock_column", new PillarBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block TAINTED_ROCK_COLUMN_CAP = register("tainted_rock_column_cap", new PillarCapBlock(TAINTED_ROCK_PROPERTIES()),true);

	Block CUT_TAINTED_ROCK = register("cut_tainted_rock", new Block(TAINTED_ROCK_PROPERTIES()),true);
	Block CHISELED_TAINTED_ROCK = register("chiseled_tainted_rock", new Block(TAINTED_ROCK_PROPERTIES()),true);

	Block TAINTED_ROCK_SLAB = register("tainted_rock_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()), true);
	Block SMOOTH_TAINTED_ROCK_SLAB = register("smooth_tainted_rock_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block POLISHED_TAINTED_ROCK_SLAB = register("polished_tainted_rock_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block TAINTED_ROCK_BRICKS_SLAB = register("tainted_rock_bricks_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TAINTED_ROCK_BRICKS_SLAB = register("cracked_tainted_rock_bricks_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block SMALL_TAINTED_ROCK_BRICKS_SLAB = register("small_tainted_rock_bricks_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block TAINTED_ROCK_TILES_SLAB = register("tainted_rock_tiles_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TAINTED_ROCK_TILES_SLAB = register("cracked_tainted_rock_tiles_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_SMALL_TAINTED_ROCK_BRICKS_SLAB = register("cracked_small_tainted_rock_bricks_slab", new SlabBlock(TAINTED_ROCK_PROPERTIES()),true);

	Block TAINTED_ROCK_STAIRS = register("tainted_rock_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);
	Block SMOOTH_TAINTED_ROCK_STAIRS = register("smooth_tainted_rock_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);
	Block POLISHED_TAINTED_ROCK_STAIRS = register("polished_tainted_rock_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);
	Block TAINTED_ROCK_BRICKS_STAIRS = register("tainted_rock_bricks_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TAINTED_ROCK_BRICKS_STAIRS = register("cracked_tainted_rock_bricks_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);
	Block SMALL_TAINTED_ROCK_BRICKS_STAIRS = register("small_tainted_rock_bricks_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);
	Block TAINTED_ROCK_TILES_STAIRS = register("tainted_rock_tiles_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TAINTED_ROCK_TILES_STAIRS = register("cracked_tainted_rock_tiles_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_SMALL_TAINTED_ROCK_BRICKS_STAIRS = register("cracked_small_tainted_rock_bricks_stairs", new StairsBlock(TAINTED_ROCK.getDefaultState(), TAINTED_ROCK_PROPERTIES()),true);

	Block TAINTED_ROCK_PRESSURE_PLATE = register("tainted_rock_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, TAINTED_ROCK_PROPERTIES(), BlockSetType.OAK),true);
	Block TAINTED_ROCK_BUTTON = register("tainted_rock_button", new ButtonBlock(TAINTED_ROCK_PROPERTIES(), BlockSetType.OAK, 1, true),true);

	Block TAINTED_ROCK_WALL = register("tainted_rock_wall", new WallBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block TAINTED_ROCK_BRICKS_WALL = register("tainted_rock_bricks_wall", new WallBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block SMALL_TAINTED_ROCK_BRICKS_WALL = register("small_tainted_rock_bricks_wall", new WallBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TAINTED_ROCK_BRICKS_WALL = register("cracked_tainted_rock_bricks_wall", new WallBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block TAINTED_ROCK_TILES_WALL = register("tainted_rock_tiles_wall", new WallBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_SMALL_TAINTED_ROCK_BRICKS_WALL = register("cracked_small_tainted_rock_bricks_wall", new WallBlock(TAINTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TAINTED_ROCK_TILES_WALL = register("cracked_tainted_rock_tiles_wall", new WallBlock(TAINTED_ROCK_PROPERTIES()),true);

	Block TAINTED_ROCK_ITEM_STAND = register("tainted_rock_item_stand", new ItemStandBlock<>(TAINTED_ROCK_PROPERTIES().nonOpaque()),true);
	Block TAINTED_ROCK_ITEM_PEDESTAL = register("tainted_rock_item_pedestal", new ItemPedestalBlock<>(TAINTED_ROCK_PROPERTIES().nonOpaque()),true);
	//endregion

	//region twisted rock
	Block TWISTED_ROCK = register("twisted_rock", new Block(TWISTED_ROCK_PROPERTIES()),true);
	Block SMOOTH_TWISTED_ROCK = register("smooth_twisted_rock", new Block(TWISTED_ROCK_PROPERTIES()),true);
	Block POLISHED_TWISTED_ROCK = register("polished_twisted_rock", new Block(TWISTED_ROCK_PROPERTIES()),true);

	Block TWISTED_ROCK_BRICKS = register("twisted_rock_bricks", new Block(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TWISTED_ROCK_BRICKS = register("cracked_twisted_rock_bricks", new Block(TWISTED_ROCK_PROPERTIES()),true);
	Block SMALL_TWISTED_ROCK_BRICKS = register("small_twisted_rock_bricks", new Block(TWISTED_ROCK_PROPERTIES()),true);

	Block TWISTED_ROCK_TILES = register("twisted_rock_tiles", new Block(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TWISTED_ROCK_TILES = register("cracked_twisted_rock_tiles", new Block(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_SMALL_TWISTED_ROCK_BRICKS = register("cracked_small_twisted_rock_bricks", new Block(TWISTED_ROCK_PROPERTIES()),true);

	Block TWISTED_ROCK_COLUMN = register("twisted_rock_column", new PillarBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block TWISTED_ROCK_COLUMN_CAP = register("twisted_rock_column_cap", new PillarCapBlock(TWISTED_ROCK_PROPERTIES()),true);

	Block CUT_TWISTED_ROCK = register("cut_twisted_rock", new Block(TWISTED_ROCK_PROPERTIES()),true);
	Block CHISELED_TWISTED_ROCK = register("chiseled_twisted_rock", new Block(TWISTED_ROCK_PROPERTIES()),true);

	Block TWISTED_ROCK_SLAB = register("twisted_rock_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block SMOOTH_TWISTED_ROCK_SLAB = register("smooth_twisted_rock_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block POLISHED_TWISTED_ROCK_SLAB = register("polished_twisted_rock_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block TWISTED_ROCK_BRICKS_SLAB = register("twisted_rock_bricks_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TWISTED_ROCK_BRICKS_SLAB = register("cracked_twisted_rock_bricks_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block SMALL_TWISTED_ROCK_BRICKS_SLAB = register("small_twisted_rock_bricks_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block TWISTED_ROCK_TILES_SLAB = register("twisted_rock_tiles_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TWISTED_ROCK_TILES_SLAB = register("cracked_twisted_rock_tiles_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_SMALL_TWISTED_ROCK_BRICKS_SLAB = register("cracked_small_twisted_rock_bricks_slab", new SlabBlock(TWISTED_ROCK_PROPERTIES()),true);

	Block TWISTED_ROCK_STAIRS = register("twisted_rock_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);
	Block SMOOTH_TWISTED_ROCK_STAIRS = register("smooth_twisted_rock_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);
	Block POLISHED_TWISTED_ROCK_STAIRS = register("polished_twisted_rock_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);
	Block TWISTED_ROCK_BRICKS_STAIRS = register("twisted_rock_bricks_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TWISTED_ROCK_BRICKS_STAIRS = register("cracked_twisted_rock_bricks_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);
	Block SMALL_TWISTED_ROCK_BRICKS_STAIRS = register("small_twisted_rock_bricks_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);
	Block TWISTED_ROCK_TILES_STAIRS = register("twisted_rock_tiles_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TWISTED_ROCK_TILES_STAIRS = register("cracked_twisted_rock_tiles_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_SMALL_TWISTED_ROCK_BRICKS_STAIRS = register("cracked_small_twisted_rock_bricks_stairs", new StairsBlock(TWISTED_ROCK.getDefaultState(), TWISTED_ROCK_PROPERTIES()),true);

	Block TWISTED_ROCK_PRESSURE_PLATE = register("twisted_rock_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, TWISTED_ROCK_PROPERTIES(),BlockSetType.STONE),true);
	Block TWISTED_ROCK_BUTTON = register("twisted_rock_button",  new ButtonBlock(TWISTED_ROCK_PROPERTIES(), BlockSetType.STONE,1,false),true);


	Block TWISTED_ROCK_WALL = register("twisted_rock_wall", new WallBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block TWISTED_ROCK_BRICKS_WALL = register("twisted_rock_bricks_wall", new WallBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block SMALL_TWISTED_ROCK_BRICKS_WALL = register("small_twisted_rock_bricks_wall", new WallBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TWISTED_ROCK_BRICKS_WALL = register("cracked_twisted_rock_bricks_wall", new WallBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block TWISTED_ROCK_TILES_WALL = register("twisted_rock_tiles_wall", new WallBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_SMALL_TWISTED_ROCK_BRICKS_WALL = register("cracked_small_twisted_rock_bricks_wall", new WallBlock(TWISTED_ROCK_PROPERTIES()),true);
	Block CRACKED_TWISTED_ROCK_TILES_WALL = register("cracked_twisted_rock_tiles_wall", new WallBlock(TWISTED_ROCK_PROPERTIES()),true);

	Block TWISTED_ROCK_ITEM_STAND = register("twisted_rock_item_stand", new ItemStandBlock<>(TWISTED_ROCK_PROPERTIES().nonOpaque()),true);
	Block TWISTED_ROCK_ITEM_PEDESTAL = register("twisted_rock_item_pedestal", new ItemPedestalBlock<>(TWISTED_ROCK_PROPERTIES().nonOpaque()),true);
	//endregion

	//region runewood
	Block RUNEWOOD_SAPLING = register("runewood_sapling", new SaplingBlock(new RunewoodSaplingGenerator(), RUNEWOOD_PLANTS_PROPERTIES().ticksRandomly()),true);
	Block RUNEWOOD_LEAVES = register("runewood_leaves", new MalumLeavesBlock(RUNEWOOD_LEAVES_PROPERTIES(), new Color(175, 65, 48), new Color(251, 193, 76)),true);

	Block STRIPPED_RUNEWOOD_LOG = register("stripped_runewood_log", new PillarBlock(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_LOG = register("runewood_log", new MalumLogBlock(STRIPPED_RUNEWOOD_LOG, RUNEWOOD_PROPERTIES(), false),true);
	Block STRIPPED_RUNEWOOD = register("stripped_runewood", new PillarBlock(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD = register("runewood", new LodestoneLogBlock(STRIPPED_RUNEWOOD, RUNEWOOD_PROPERTIES()),true);

	Block REVEALED_RUNEWOOD_LOG = register("revealed_runewood_log", new SapFilledLogBlock(RUNEWOOD_PROPERTIES(), STRIPPED_RUNEWOOD_LOG, HOLY_SAP, MalumSpiritTypeRegistry.INFERNAL_SPIRIT.getColor()),true);
	Block EXPOSED_RUNEWOOD_LOG = register("exposed_runewood_log", new LodestoneLogBlock(REVEALED_RUNEWOOD_LOG, RUNEWOOD_PROPERTIES()),true);

	Block RUNEWOOD_PLANKS = register("runewood_planks", new Block(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_PLANKS_SLAB = register("runewood_planks_slab", new SlabBlock(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_PLANKS_STAIRS = register("runewood_planks_stairs", new StairsBlock(RUNEWOOD_PLANKS.getDefaultState(), RUNEWOOD_PROPERTIES()),true);

	Block VERTICAL_RUNEWOOD_PLANKS = register("vertical_runewood_planks", new Block(RUNEWOOD_PROPERTIES()),true);
	Block VERTICAL_RUNEWOOD_PLANKS_SLAB = register("vertical_runewood_planks_slab", new SlabBlock(RUNEWOOD_PROPERTIES()),true);
	Block VERTICAL_RUNEWOOD_PLANKS_STAIRS = register("vertical_runewood_planks_stairs", new StairsBlock(VERTICAL_RUNEWOOD_PLANKS.getDefaultState(), RUNEWOOD_PROPERTIES()),true);

	Block RUNEWOOD_PANEL = register("runewood_panel", new Block(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_PANEL_SLAB = register("runewood_panel_slab", new SlabBlock(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_PANEL_STAIRS = register("runewood_panel_stairs", new StairsBlock(RUNEWOOD_PANEL.getDefaultState(), RUNEWOOD_PROPERTIES()),true);

	Block RUNEWOOD_TILES = register("runewood_tiles", new Block(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_TILES_SLAB = register("runewood_tiles_slab", new SlabBlock(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_TILES_STAIRS = register("runewood_tiles_stairs", new StairsBlock(RUNEWOOD_TILES.getDefaultState(), RUNEWOOD_PROPERTIES()),true);

	Block CUT_RUNEWOOD_PLANKS = register("cut_runewood_planks", new Block(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_BEAM = register("runewood_beam", new PillarBlock(RUNEWOOD_PROPERTIES()),true);

	Block RUNEWOOD_DOOR = register("runewood_door", new DoorBlock(RUNEWOOD_PROPERTIES().nonOpaque(), BlockSetType.OAK),true);
	Block RUNEWOOD_TRAPDOOR = register("runewood_trapdoor", new TrapdoorBlock(RUNEWOOD_PROPERTIES().nonOpaque(), BlockSetType.OAK),true);
	Block SOLID_RUNEWOOD_TRAPDOOR = register("solid_runewood_trapdoor", new TrapdoorBlock(RUNEWOOD_PROPERTIES().nonOpaque(), BlockSetType.OAK),true);

	Block RUNEWOOD_PLANKS_BUTTON = register("runewood_planks_button", new ButtonBlock(RUNEWOOD_PROPERTIES(),BlockSetType.OAK, 1 , false),true);
	Block RUNEWOOD_PLANKS_PRESSURE_PLATE = register("runewood_planks_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, RUNEWOOD_PROPERTIES(), BlockSetType.OAK),true);

	Block RUNEWOOD_PLANKS_FENCE = register("runewood_planks_fence", new FenceBlock(RUNEWOOD_PROPERTIES()),true);
	Block RUNEWOOD_PLANKS_FENCE_GATE = register("runewood_planks_fence_gate", new FenceGateBlock(RUNEWOOD_PROPERTIES(), WoodType.OAK),true);

	Block RUNEWOOD_ITEM_STAND = register("runewood_item_stand", new ItemStandBlock<>(RUNEWOOD_PROPERTIES().nonOpaque()),true);
	Block RUNEWOOD_ITEM_PEDESTAL = register("runewood_item_pedestal", new WoodItemPedestalBlock<>(RUNEWOOD_PROPERTIES().nonOpaque()),true);

	//Block RUNEWOOD_SIGN = register("runewood_sign", new LodestoneStandingSignBlock(RUNEWOOD_PROPERTIES().nonOpaque().noCollision(), RUNEWOOD_SIGN_TYPE), true);
	//Block RUNEWOOD_WALL_SIGN = register("runewood_wall_sign", new LodestoneWallSignBlock(RUNEWOOD_PROPERTIES().nonOpaque().noCollision(), RUNEWOOD_SIGN_TYPE), false);
	//endregion

	//region soulwood
	Block SOULWOOD_GROWTH = register("soulwood_growth", new SoulwoodGrowthBlock(new SoulwoodSaplingGenerator(), BLIGHT_PLANTS_PROPERTIES().ticksRandomly()),true);
	Block SOULWOOD_LEAVES = register("soulwood_leaves", new MalumLeavesBlock(SOULWOOD_LEAVES_PROPERTIES(), new Color(152, 6, 45), new Color(224, 30, 214)),true);

	Block STRIPPED_SOULWOOD_LOG = register("stripped_soulwood_log", new PillarBlock(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_LOG = register("soulwood_log", new SoulwoodLogBlock(STRIPPED_SOULWOOD_LOG,SOULWOOD_PROPERTIES(), true),true);
	Block STRIPPED_SOULWOOD = register("stripped_soulwood", new PillarBlock(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD = register("soulwood", new SoulwoodBlock(STRIPPED_SOULWOOD, SOULWOOD_PROPERTIES()),true);

	Block REVEALED_SOULWOOD_LOG = register("revealed_soulwood_log", new SapFilledSoulwoodLogBlock(SOULWOOD_PROPERTIES(), STRIPPED_SOULWOOD_LOG, UNHOLY_SAP, new Color(214, 46, 83)),true);
	Block EXPOSED_SOULWOOD_LOG = register("exposed_soulwood_log", new SoulwoodBlock(REVEALED_SOULWOOD_LOG, SOULWOOD_PROPERTIES()),true);

	Block SOULWOOD_PLANKS = register("soulwood_planks", new Block(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_PLANKS_SLAB = register("soulwood_planks_slab", new SlabBlock(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_PLANKS_STAIRS = register("soulwood_planks_stairs", new StairsBlock(SOULWOOD_PLANKS.getDefaultState(), SOULWOOD_PROPERTIES()),true);

	Block VERTICAL_SOULWOOD_PLANKS = register("vertical_soulwood_planks", new Block(SOULWOOD_PROPERTIES()),true);
	Block VERTICAL_SOULWOOD_PLANKS_SLAB = register("vertical_soulwood_planks_slab", new SlabBlock(SOULWOOD_PROPERTIES()),true);
	Block VERTICAL_SOULWOOD_PLANKS_STAIRS = register("vertical_soulwood_planks_stairs", new StairsBlock(VERTICAL_SOULWOOD_PLANKS.getDefaultState(), SOULWOOD_PROPERTIES()),true);

	Block SOULWOOD_PANEL = register("soulwood_panel", new Block(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_PANEL_SLAB = register("soulwood_panel_slab", new SlabBlock(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_PANEL_STAIRS = register("soulwood_panel_stairs", new StairsBlock(SOULWOOD_PANEL.getDefaultState(), SOULWOOD_PROPERTIES()),true);

	Block SOULWOOD_TILES = register("soulwood_tiles", new Block(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_TILES_SLAB = register("soulwood_tiles_slab", new SlabBlock(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_TILES_STAIRS = register("soulwood_tiles_stairs", new StairsBlock(SOULWOOD_TILES.getDefaultState(), SOULWOOD_PROPERTIES()),true);

	Block CUT_SOULWOOD_PLANKS = register("cut_soulwood_planks", new Block(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_BEAM = register("soulwood_beam", new PillarBlock(SOULWOOD_PROPERTIES()),true);

	Block SOULWOOD_DOOR = register("soulwood_door", new DoorBlock(SOULWOOD_PROPERTIES().nonOpaque(), BlockSetType.OAK),true);
	Block SOULWOOD_TRAPDOOR = register("soulwood_trapdoor", new TrapdoorBlock(SOULWOOD_PROPERTIES().nonOpaque(), BlockSetType.OAK),true);
	Block SOLID_SOULWOOD_TRAPDOOR = register("solid_soulwood_trapdoor", new TrapdoorBlock(SOULWOOD_PROPERTIES().nonOpaque(), BlockSetType.OAK),true);

	Block SOULWOOD_PLANKS_BUTTON = register("soulwood_planks_button", new ButtonBlock(SOULWOOD_PROPERTIES(), BlockSetType.OAK,1, true),true);
	Block SOULWOOD_PLANKS_PRESSURE_PLATE = register("soulwood_planks_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, SOULWOOD_PROPERTIES(), BlockSetType.OAK),true);

	Block SOULWOOD_PLANKS_FENCE = register("soulwood_planks_fence", new FenceBlock(SOULWOOD_PROPERTIES()),true);
	Block SOULWOOD_PLANKS_FENCE_GATE = register("soulwood_planks_fence_gate", new FenceGateBlock(SOULWOOD_PROPERTIES(), WoodType.OAK),true);

	Block SOULWOOD_ITEM_STAND = register("soulwood_item_stand", new ItemStandBlock<>(SOULWOOD_PROPERTIES().nonOpaque()),true);
	Block SOULWOOD_ITEM_PEDESTAL = register("soulwood_item_pedestal", new WoodItemPedestalBlock<>(SOULWOOD_PROPERTIES().nonOpaque()),true);

	//Block SOULWOOD_SIGN = register("soulwood_sign", new LodestoneStandingSignBlock(SOULWOOD_PROPERTIES().nonOpaque().noCollision(), SOULWOOD_SIGN_TYPE), true);
	//Block SOULWOOD_WALL_SIGN = register("soulwood_wall_sign", new LodestoneWallSignBlock(SOULWOOD_PROPERTIES().nonOpaque().noCollision(), SOULWOOD_SIGN_TYPE), false);
	//endregion



	//region blight
	Block BLIGHTED_EARTH = register("blighted_earth", new BlightedSoilBlock(BLIGHT_PROPERTIES()), true);
	Block BLIGHTED_SOIL = register("blighted_soil", new BlightedSoilBlock(BLIGHT_PROPERTIES()), true);
	Block BLIGHTED_WEED = register("blighted_weed", new BlightedGrassBlock(BLIGHT_PLANTS_PROPERTIES()), true);
	Block BLIGHTED_TUMOR = register("blighted_tumor", new BlightedGrassBlock(BLIGHT_PLANTS_PROPERTIES()), true);
	Block BLIGHTED_SOULWOOD = register("blighted_soulwood", new BlightedSoulwoodBlock(SOULWOOD_PROPERTIES()), true);
	Item BLIGHTED_GUNK = register("blighted_gunk", new Item(settings()));
	//endregion

	Block RUNEWOOD_TOTEM_BASE = register("runewood_totem_base", new TotemBaseBlock<>(RUNEWOOD_PROPERTIES().nonOpaque(), false), true);
	Block RUNEWOOD_TOTEM_POLE = register("runewood_totem_pole", new TotemPoleBlock<>(RUNEWOOD_LOG, RUNEWOOD_PROPERTIES().nonOpaque(), false), false);

	Block SOULWOOD_TOTEM_BASE = register("soulwood_totem_base", new TotemBaseBlock<>(SOULWOOD_PROPERTIES().nonOpaque(), true), true);
	Block SOULWOOD_TOTEM_POLE = register("soulwood_totem_pole", new TotemPoleBlock<>(SOULWOOD_LOG, SOULWOOD_PROPERTIES().nonOpaque(), true), false);

	//region ether
	Block WALL_ETHER_TORCH = register("wall_ether_torch", new EtherWallTorchBlock<>(RUNEWOOD_PROPERTIES().noCollision().breakInstantly().luminance((b) -> 14)), false);
	Block ETHER_TORCH = registerEtherTorch("ether_torch", new EtherTorchBlock<>(RUNEWOOD_PROPERTIES().noCollision().breakInstantly().luminance((b) -> 14)), WALL_ETHER_TORCH, false, true);

	Block ETHER = registerEther("ether", new EtherBlock<>(ETHER_BLOCK_PROPERTIES()), false, true);
	Block TAINTED_ETHER_BRAZIER = registerEtherBrazier("tainted_ether_brazier", new EtherBrazierBlock<>(TAINTED_ROCK_PROPERTIES().luminance((b) -> 14).nonOpaque()), false, true);
	Block TWISTED_ETHER_BRAZIER = registerEtherBrazier("twisted_ether_brazier", new EtherBrazierBlock<>(TWISTED_ROCK_PROPERTIES().luminance((b) -> 14).nonOpaque()), false, true);

	Block IRIDESCENT_WALL_ETHER_TORCH = register("iridescent_wall_ether_torch", new EtherWallTorchBlock<>(RUNEWOOD_PROPERTIES().noCollision().breakInstantly().luminance((b) -> 14)), false);
	Block IRIDESCENT_ETHER_TORCH = registerEtherTorch("iridescent_ether_torch", new EtherTorchBlock<>(RUNEWOOD_PROPERTIES().noCollision().breakInstantly().luminance((b) -> 14)), IRIDESCENT_WALL_ETHER_TORCH, true, true);

	Block IRIDESCENT_ETHER = registerEther("iridescent_ether", new EtherBlock<>(ETHER_BLOCK_PROPERTIES()), true , true);
	Block TAINTED_IRIDESCENT_ETHER_BRAZIER = registerEtherBrazier("tainted_iridescent_ether_brazier", new EtherBrazierBlock<>(TAINTED_ROCK_PROPERTIES().luminance((b) -> 14).nonOpaque()), true, true);
	Block TWISTED_IRIDESCENT_ETHER_BRAZIER = registerEtherBrazier("twisted_iridescent_ether_brazier", new EtherBrazierBlock<>(TWISTED_ROCK_PROPERTIES().luminance((b) -> 14).nonOpaque()), true, true);


	//endregion
	Block BLAZING_TORCH = register("blazing_torch", new TorchBlock(RUNEWOOD_PROPERTIES().noCollision().breakInstantly().luminance((b) -> 14), ParticleTypes.FLAME), true);
	Block WALL_BLAZING_TORCH = register("wall_blazing_torch", new WallTorchBlock(RUNEWOOD_PROPERTIES().noCollision().breakInstantly().luminance((b) -> 14), ParticleTypes.FLAME), true);

	Block BLOCK_OF_ARCANE_CHARCOAL = register("block_of_arcane_charcoal", new Block(ARCANE_CHARCOAL_PROPERTIES()),true);

	Block BLAZING_QUARTZ_ORE = register("blazing_quartz_ore", new ExperienceDroppingBlock(BLAZE_QUARTZ_ORE_PROPERTIES().luminance((b) -> 6), UniformIntProvider.create(4, 7)),true);
	Block BLOCK_OF_BLAZING_QUARTZ = register("block_of_blazing_quartz", new Block(BLAZE_QUARTZ_PROPERTIES().luminance((b) -> 14)),true);

	Block NATURAL_QUARTZ_ORE = register("natural_quartz_ore", new ExperienceDroppingBlock(NATURAL_QUARTZ_PROPERTIES(), UniformIntProvider.create(1, 4)),true);
	Block DEEPSLATE_QUARTZ_ORE = register("deepslate_quartz_ore", new ExperienceDroppingBlock(DEEPSLATE_QUARTZ_PROPERTIES(), UniformIntProvider.create(2, 5)),true);
	Block NATURAL_QUARTZ_CLUSTER = register("natural_quartz_cluster", new AmethystClusterBlock(7, 3, NATURAL_QUARTZ_CLUSTER_PROPERTIES().nonOpaque().ticksRandomly().strength(1.5F).luminance(state -> 5)),true);
	Item NATURAL_QUARTZ = register("natural_quartz", new AliasedBlockItem(NATURAL_QUARTZ_CLUSTER, settings()));

	Block NATURAL_QUARTZ_CLUSTER_BLOCK = register("natural_quartz_cluster_block", new NaturalQuartzBlock(NATURAL_QUARTZ_CLUSTER_PROPERTIES().nonOpaque()), true);
	Block LARGE_QUARTZ_BUD = register("large_quartz_bud", new AmethystClusterBlock(5, 3, AbstractBlock.Settings.copy(NATURAL_QUARTZ_CLUSTER).luminance(state -> 4)), false);
	Block MEDIUM_QUARTZ_BUD = register("medium_quartz_bud", new AmethystClusterBlock(4, 3, AbstractBlock.Settings.copy(NATURAL_QUARTZ_CLUSTER).luminance(state -> 2)), false);
	Block SMALL_QUARTZ_BUD = register("small_quartz_bud", new AmethystClusterBlock(3, 4, AbstractBlock.Settings.copy(NATURAL_QUARTZ_CLUSTER).luminance(state -> 1)), false);
	Block BUDDING_NATURAL_QUARTZ = register("budding_natural_quartz_cluster_block", new BuddingNaturalQuartzBlock( NATURAL_QUARTZ_CLUSTER_PROPERTIES().nonOpaque().ticksRandomly()), true);

	Block BLOCK_OF_RARE_EARTHS = register("block_of_rare_earths", new ExperienceDroppingBlock(RARE_EARTH_PROPERTIES(), UniformIntProvider.create(10, 100)),true);

	Block BRILLIANT_STONE = register("brilliant_stone", new ExperienceDroppingBlock(BRILLIANCE_PROPERTIES(), UniformIntProvider.create(14, 18)),true);
	Block BRILLIANT_DEEPSLATE = register("brilliant_deepslate", new ExperienceDroppingBlock(DEEPSLATE_BRILLIANCE_PROPERTIES(), UniformIntProvider.create(16, 26)),true);
	Block BLOCK_OF_BRILLIANCE = register("block_of_brilliance", new Block(BRILLIANCE_PROPERTIES()),true);

	Block SOULSTONE_ORE = register("soulstone_ore", new ExperienceDroppingBlock(SOULSTONE_PROPERTIES()),true);
	Block DEEPSLATE_SOULSTONE_ORE = register("deepslate_soulstone_ore", new ExperienceDroppingBlock(DEEPSLATE_SOULSTONE_PROPERTIES().strength(6f, 4f)),true);


	Block BLOCK_OF_RAW_SOULSTONE = register("block_of_raw_soulstone", new Block(SOULSTONE_PROPERTIES()),true);
	Block BLOCK_OF_SOULSTONE = register("block_of_soulstone", new Block(SOULSTONE_PROPERTIES()),true);

	Block BLOCK_OF_HALLOWED_GOLD = register("block_of_hallowed_gold", new Block(HALLOWED_GOLD_PROPERTIES()),true);
	Block BLOCK_OF_SOUL_STAINED_STEEL = register("block_of_soul_stained_steel", new Block(SOUL_STAINED_STEEL_BLOCK_PROPERTIES()),true);

	Block THE_DEVICE = register("the_device", new TheDevice(TAINTED_ROCK_PROPERTIES()),true);

	// Block BLOCK_OF_RARE_EARTHS = register("block_of_rare_earths", new OreBlock(RARE_EARTH_PROPERTIES(), UniformIntProvider.create(10, 100)));
	Block BLOCK_OF_ROTTING_ESSENCE = register("block_of_rotting_essence", new Block(AbstractBlock.Settings.copy(Blocks.FIRE_CORAL_BLOCK)),true);
	Block BLOCK_OF_GRIM_TALC = register("block_of_grim_talc", new Block(AbstractBlock.Settings.copy(Blocks.BONE_BLOCK)),true);
	Block BLOCK_OF_ALCHEMICAL_CALX = register("block_of_alchemical_calx", new Block(AbstractBlock.Settings.copy(Blocks.CALCITE)),true);
	Block BLOCK_OF_ASTRAL_WEAVE = register("block_of_astral_weave", new Block(AbstractBlock.Settings.copy(Blocks.LIGHT_BLUE_WOOL)),true);
	Block BLOCK_OF_HEX_ASH = register("block_of_hex_ash", new Block(AbstractBlock.Settings.copy(Blocks.PURPLE_CONCRETE_POWDER)),true);
	Block BLOCK_OF_CURSED_GRIT = register("block_of_cursed_grit", new Block(AbstractBlock.Settings.copy(Blocks.RED_CONCRETE_POWDER)),true);

	static <T extends Block> T registerEther(String name, T block, boolean isIri, boolean createItem) {
		BLOCKS.put(block, new Identifier(Malum.MODID, name));
		if (createItem) {
			ITEMS.put(new EtherItem(block, settings(), isIri), BLOCKS.get(block));
		}
		return block;
	}

	static <T extends Block> T registerEtherTorch(String name, T block, T wallBlock, boolean isIri, boolean createItem) {
		BLOCKS.put(block, new Identifier(Malum.MODID, name));
		if (createItem) {
			ITEMS.put(new EtherTorchItem(block, wallBlock, settings(), isIri), BLOCKS.get(block));
		}
		return block;
	}

	static <T extends Block> T registerEtherBrazier(String name, T block, boolean isIri, boolean createItem) {
		BLOCKS.put(block, new Identifier(Malum.MODID, name));
		if (createItem) {
			ITEMS.put(new EtherBrazierItem(block, settings(), isIri), BLOCKS.get(block));
		}
		return block;
	}


	static <T extends MalumScytheItem> T registerScytheItem(String id, T item) {
		SCYTHES.add(item);
		return register(id, item);
	}

	static <T extends Block> T registerJar(String name, T block, boolean createItem) {
		BLOCKS.put(block, new Identifier(Malum.MODID, name));
		if (createItem) {
			ITEMS.put(new SpiritJarItem(block, settings()), BLOCKS.get(block));
		}
		return block;
	}

	static <T extends Block> T registerVial(String name, T block, boolean createItem) {
		BLOCKS.put(block, new Identifier(Malum.MODID, name));
		if (createItem) {
			ITEMS.put(new SoulVialItem(block, settings()), BLOCKS.get(block));
		}
		return block;
	}

	static <T extends Block> T register(String name, T block, boolean createItem) {
		BLOCKS.put(block, new Identifier(Malum.MODID, name));
		if (createItem) {
			ITEMS.put(new BlockItem(block, settings()), BLOCKS.get(block));
		}
		return block;
	}

	static <T extends Block> T registerMultiBlock(String name, T block, Supplier<? extends MultiBlockStructure> structure, boolean createItem) {
		BLOCKS.put(block, new Identifier(Malum.MODID, name));
		if (createItem) {
			ITEMS.put(new MultiBlockItem(block, settings(), structure), BLOCKS.get(block));
		}
		return block;
	}

	static <T extends Item> T register(String name, T item) {
		ITEMS.put(item, new Identifier(Malum.MODID, name));
		return item;
	}

	static MalumSpiritItem registerSpirit(String name, MalumSpiritItem item) {
		ITEMS.put(item, new Identifier(Malum.MODID, name));
		SPIRITS.put(() -> item, name);
		return item;
	}

	static Item.Settings settings() {
		return new Item.Settings();
	}

	static void init(){
		SIGN_TYPES.forEach(WoodType::register);
		BLOCKS.keySet().forEach(block -> Registry.register(Registries.BLOCK, BLOCKS.get(block), block));
		ITEMS.keySet().forEach(item -> Registry.register(Registries.ITEM, ITEMS.get(item), item));

		FuelRegistry fuelRegistry = FuelRegistry.INSTANCE;
		fuelRegistry.add(ARCANE_CHARCOAL, 3200);
		fuelRegistry.add(BLOCK_OF_ARCANE_CHARCOAL.asItem(), 28800);
		fuelRegistry.add(ARCANE_CHARCOAL_FRAGMENT, 400);
		fuelRegistry.add(BLAZING_QUARTZ, 1600);
		fuelRegistry.add(BLAZING_QUARTZ_FRAGMENT, 200);
		fuelRegistry.add(BLOCK_OF_BLAZING_QUARTZ.asItem(), 14400);
		fuelRegistry.add(CHARCOAL_FRAGMENT, 200);
		fuelRegistry.add(COAL_FRAGMENT, 200);

	}
}

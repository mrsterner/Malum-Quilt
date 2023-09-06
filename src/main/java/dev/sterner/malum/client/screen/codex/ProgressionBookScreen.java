package dev.sterner.malum.client.screen.codex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sammy.lodestone.handlers.screenparticle.ScreenParticleHandler;
import com.sammy.lodestone.setup.LodestoneShaderRegistry;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.recipe.IRecipeComponent;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import com.sammy.lodestone.systems.rendering.shader.ExtendedShader;
import dev.sterner.malum.api.event.ProgressionBookEntriesSetEvent;
import dev.sterner.malum.client.screen.codex.objects.*;
import dev.sterner.malum.client.screen.codex.page.*;
import dev.sterner.malum.common.registry.MalumRiteRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.sterner.malum.Malum.id;
import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderTexture;
import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderTransparentTexture;
import static dev.sterner.malum.common.registry.MalumObjects.*;
import static net.minecraft.item.Items.*;
import static org.lwjgl.opengl.GL11C.GL_SCISSOR_TEST;

public class ProgressionBookScreen extends AbstractProgressionCodexScreen {

	public static ProgressionBookScreen screen;

	public static final Identifier FRAME_TEXTURE = id("textures/gui/book/frame.png");
	public static final Identifier FADE_TEXTURE = id("textures/gui/book/fade.png");
	public static final Identifier BACKGROUND_TEXTURE = id("textures/gui/book/background.png");

	public static List<BookEntry> ENTRIES = new ArrayList<>();

	protected ProgressionBookScreen() {
		super(1024, 2560);
		client = MinecraftClient.getInstance();
		setupEntries();
		ProgressionBookEntriesSetEvent.EVENT.invoker().addExtraEntry(ENTRIES);
		setupObjects();
	}

	@Override
	public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks) {
		MatrixStack poseStack = guiGraphics.getMatrices();
		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;

		renderBackground(BACKGROUND_TEXTURE, poseStack, 0.1f, 0.4f);
		GL11.glEnable(GL_SCISSOR_TEST);
		cut();

		renderEntries(poseStack, mouseX, mouseY, partialTicks);
		ScreenParticleHandler.renderEarlyParticles();
		GL11.glDisable(GL_SCISSOR_TEST);

		renderTransparentTexture(FADE_TEXTURE, poseStack, guiLeft, guiTop, 1, 1, bookWidth, bookHeight, 512, 512);
		renderTexture(FRAME_TEXTURE, poseStack, guiLeft, guiTop, 1, 1, bookWidth, bookHeight, 512, 512);
		lateEntryRender(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public Collection<BookEntry> getEntries() {
		return ENTRIES;
	}

	@Override
	public SoundEvent getSweetenerSound() {
		return MalumSoundRegistry.ARCANA_SWEETENER_NORMAL;
	}

	@Override
	public void close() {
		super.close();
		playSweetenedSound(MalumSoundRegistry.ARCANA_CODEX_CLOSE, 0.75f);
	}

	public static ProgressionBookScreen getInstance() {
		if (screen == null) {
			screen = new ProgressionBookScreen();
		}
		return screen;
	}

	public static void openScreen(boolean ignoreNextMouseClick) {
		if (screen == null) {
			screen = new ProgressionBookScreen();
		}
		MinecraftClient.getInstance().setScreen(screen);
		ScreenParticleHandler.clearParticles();
		screen.ignoreNextMouseInput = ignoreNextMouseClick;
	}

	public static void openCodexViaItem() {
		openScreen(true);
		screen.playSweetenedSound(MalumSoundRegistry.ARCANA_CODEX_OPEN, 1.25f);
	}

	public static void setupEntries() {
		ENTRIES.clear();
		Item EMPTY = ItemStack.EMPTY.getItem();

		ENTRIES.add(new BookEntry(
				"introduction", ENCYCLOPEDIA_ARCANA, 0, 0)
				.setObjectSupplier(ImportantEntryObject::new)
				.addPage(new HeadlineTextPage("introduction", "introduction.1"))
				.addPage(new TextPage("introduction.2"))
				.addPage(new TextPage("introduction.3"))
				.addPage(new TextPage("introduction.4"))
				.addPage(new TextPage("introduction.5"))
		);

		ENTRIES.add(new BookEntry(
				"spirit_crystals", 0, 1)
				.setObjectSupplier((e, x, y) -> new IconObject(e, id("textures/gui/book/icons/soul_shard.png"), x, y))
				.addPage(new HeadlineTextPage("spirit_crystals", "spirit_crystals.1"))
				.addPage(new TextPage("spirit_crystals.2"))
				.addPage(new TextPage("spirit_crystals.3"))
		);

		ENTRIES.add(new BookEntry(
				"runewood", RUNEWOOD_SAPLING.asItem(), 1, 2)
				.addPage(new HeadlineTextItemPage("runewood", "runewood.1", RUNEWOOD_SAPLING.asItem()))
				.addPage(new HeadlineTextPage("runewood.arcane_charcoal", "runewood.arcane_charcoal.1"))
				.addPage(new SmeltingBookPage(RUNEWOOD_LOG.asItem(), ARCANE_CHARCOAL.asItem()))
				.addPage(CraftingBookPage.fullPage(BLOCK_OF_ARCANE_CHARCOAL.asItem(), ARCANE_CHARCOAL.asItem()))
				.addPage(new HeadlineTextPage("runewood.holy_sap", "runewood.holy_sap.1"))
				.addPage(new SmeltingBookPage(HOLY_SAP, HOLY_SYRUP))
				.addPage(new CraftingBookPage(new ItemStack(HOLY_SAPBALL, 3), Items.SLIME_BALL, HOLY_SAP))
		);


		ENTRIES.add(new BookEntry(
				"natural_quartz", NATURAL_QUARTZ, 3, 1)
				.setObjectSupplier(MinorEntryObject::new)
				.addPage(new HeadlineTextItemPage("natural_quartz", "natural_quartz.1", NATURAL_QUARTZ))
		);



		ENTRIES.add(new BookEntry(
				"blazing_quartz", BLAZING_QUARTZ, 4, 2)
				.setObjectSupplier(MinorEntryObject::new)
				.addPage(new HeadlineTextItemPage("blazing_quartz", "blazing_quartz.1", BLAZING_QUARTZ))
				.addPage(CraftingBookPage.fullPage(BLOCK_OF_BLAZING_QUARTZ.asItem(), BLAZING_QUARTZ.asItem()))
		);

		ENTRIES.add(new BookEntry(
				"brilliance", CLUSTER_OF_BRILLIANCE, -3, 1)
				.setObjectSupplier(MinorEntryObject::new)
				.addPage(new HeadlineTextItemPage("brilliance", "brilliance.1", CLUSTER_OF_BRILLIANCE))
				.addPage(new TextPage("brilliance.2"))
				.addPage(CraftingBookPage.fullPage(BLOCK_OF_BRILLIANCE.asItem(), CLUSTER_OF_BRILLIANCE))
				.addPage(new SmeltingBookPage(new ItemStack(CLUSTER_OF_BRILLIANCE), new ItemStack(CHUNK_OF_BRILLIANCE, 2)))
		);

		ENTRIES.add(new BookEntry(
				"rare_earths", RARE_EARTHS, -4, 2)
				.setObjectSupplier(MinorEntryObject::new)
				.addPage(new HeadlineTextItemPage("rare_earths", "rare_earths", RARE_EARTHS))
		);

		ENTRIES.add(new BookEntry(
				"soulstone", PROCESSED_SOULSTONE, -1, 2)
				.addPage(new HeadlineTextItemPage("soulstone", "soulstone.1", PROCESSED_SOULSTONE))
				.addPage(new TextPage("soulstone.2"))
				.addPage(new SmeltingBookPage(new ItemStack(RAW_SOULSTONE), new ItemStack(PROCESSED_SOULSTONE, 2)))
				.addPage(CraftingBookPage.fullPage(BLOCK_OF_SOULSTONE.asItem(), PROCESSED_SOULSTONE))
				.addPage(CraftingBookPage.fullPage(BLOCK_OF_RAW_SOULSTONE.asItem(), RAW_SOULSTONE))
		);

		ENTRIES.add(new BookEntry(
				"scythes", CRUDE_SCYTHE, 0, 3)
				.addPage(new HeadlineTextPage("scythes", "scythes.1"))
				.addPage(CraftingBookPage.scythePage(CRUDE_SCYTHE, Items.IRON_INGOT, PROCESSED_SOULSTONE))
				.addPage(new TextPage("scythes.2"))
				.addPage(new HeadlineTextPage("scythes.enchanting", "scythes.enchanting.1"))
				.addPage(new HeadlineTextPage("scythes.enchanting.haunted", "scythes.enchanting.haunted.1"))
				.addPage(new HeadlineTextPage("scythes.enchanting.spirit_plunder", "scythes.enchanting.spirit_plunder.1"))
				.addPage(new HeadlineTextPage("scythes.enchanting.rebound", "scythes.enchanting.rebound.1"))
		);

		ENTRIES.add(new BookEntry(
				"spirit_infusion", SPIRIT_ALTAR.asItem(), 0, 5)
				.setObjectSupplier(ImportantEntryObject::new)
				.addPage(new HeadlineTextPage("spirit_infusion", "spirit_infusion.1"))
				.addPage(new CraftingBookPage(SPIRIT_ALTAR.asItem(), AIR, PROCESSED_SOULSTONE, AIR, GOLD_INGOT, RUNEWOOD_PLANKS.asItem(), GOLD_INGOT, RUNEWOOD_PLANKS.asItem(), RUNEWOOD_PLANKS.asItem(), RUNEWOOD_PLANKS.asItem()))
				.addPage(new TextPage("spirit_infusion.2"))
				.addPage(new TextPage("spirit_infusion.3"))
				.addPage(CraftingBookPage.itemPedestalPage(RUNEWOOD_ITEM_PEDESTAL.asItem(), RUNEWOOD_PLANKS.asItem(), RUNEWOOD_PLANKS_SLAB.asItem()))
				.addPage(CraftingBookPage.itemStandPage(RUNEWOOD_ITEM_STAND.asItem(), RUNEWOOD_PLANKS.asItem(), RUNEWOOD_PLANKS_SLAB.asItem()))
				.addPage(new HeadlineTextPage("spirit_infusion.hex_ash", "spirit_infusion.hex_ash.1"))
				.addPage(SpiritInfusionPage.fromOutput(HEX_ASH))
		);



		ENTRIES.add(new BookEntry(
				"esoteric_reaping", ROTTING_ESSENCE, 0, 6)
				.setObjectSupplier(MinorEntryObject::new)
				.addPage(new HeadlineTextPage("esoteric_reaping", "esoteric_reaping.1"))
				.addPage(new TextPage("esoteric_reaping.2"))
				.addPage(new TextPage("esoteric_reaping.3"))
				.addPage(new HeadlineTextItemPage("esoteric_reaping.rotting_essence", "esoteric_reaping.rotting_essence.1", ROTTING_ESSENCE))
				.addPage(new HeadlineTextItemPage("esoteric_reaping.grim_talc", "esoteric_reaping.grim_talc.1", GRIM_TALC))
				.addPage(new HeadlineTextItemPage("esoteric_reaping.astral_weave", "esoteric_reaping.astral_weave.1", ASTRAL_WEAVE))
				.addPage(new HeadlineTextItemPage("esoteric_reaping.calx", "esoteric_reaping.calx.1", ALCHEMICAL_CALX))
		);

		ENTRIES.add(new BookEntry(
				"primary_arcana", SACRED_SPIRIT, -2, 4)
				.addPage(new HeadlineTextItemPage("primary_arcana.sacred", "primary_arcana.sacred.1", SACRED_SPIRIT))
				.addPage(new TextPage("primary_arcana.sacred.2"))
				.addPage(new HeadlineTextItemPage("primary_arcana.wicked", "primary_arcana.wicked.1", WICKED_SPIRIT))
				.addPage(new TextPage("primary_arcana.wicked.2"))
				.addPage(new HeadlineTextItemPage("primary_arcana.arcane", "primary_arcana.arcane.1", ARCANE_SPIRIT))
				.addPage(new TextPage("primary_arcana.arcane.2"))
				.addPage(new TextPage("primary_arcana.arcane.3"))
		);

		ENTRIES.add(new BookEntry(
				"elemental_arcana", EARTHEN_SPIRIT, 2, 4)
				.addPage(new HeadlineTextItemPage("elemental_arcana.aerial", "elemental_arcana.aerial.1", AERIAL_SPIRIT))
				.addPage(new TextPage("elemental_arcana.aerial.2"))
				.addPage(new HeadlineTextItemPage("elemental_arcana.earthen", "elemental_arcana.earthen.1", EARTHEN_SPIRIT))
				.addPage(new TextPage("elemental_arcana.earthen.2"))
				.addPage(new HeadlineTextItemPage("elemental_arcana.infernal", "elemental_arcana.infernal.1", INFERNAL_SPIRIT))
				.addPage(new TextPage("elemental_arcana.infernal.2"))
				.addPage(new HeadlineTextItemPage("elemental_arcana.aqueous", "elemental_arcana.aqueous.1", AQUEOUS_SPIRIT))
				.addPage(new TextPage("elemental_arcana.aqueous.2"))
		);

		ENTRIES.add(new BookEntry(
				"eldritch_arcana", ELDRITCH_SPIRIT, 0, 7)
				.addPage(new HeadlineTextItemPage("eldritch_arcana", "eldritch_arcana.1", ELDRITCH_SPIRIT))
				.addPage(new TextPage("eldritch_arcana.2"))
		);

		ENTRIES.add(new BookEntry(
				"spirit_stones", TAINTED_ROCK.asItem(), 3, 6)
				.addPage(new HeadlineTextPage("spirit_stones.tainted_rock", "spirit_stones.tainted_rock.1"))
				.addPage(SpiritInfusionPage.fromOutput(TAINTED_ROCK.asItem()))
				.addPage(CraftingBookPage.itemPedestalPage(TAINTED_ROCK_ITEM_PEDESTAL.asItem(), TAINTED_ROCK.asItem(), TAINTED_ROCK_SLAB.asItem()))
				.addPage(CraftingBookPage.itemStandPage(TAINTED_ROCK_ITEM_STAND.asItem(), TAINTED_ROCK.asItem(), TAINTED_ROCK_SLAB.asItem()))
				.addPage(new HeadlineTextPage("spirit_stones.twisted_rock", "spirit_stones.twisted_rock.1"))
				.addPage(SpiritInfusionPage.fromOutput(TWISTED_ROCK.asItem()))
				.addPage(CraftingBookPage.itemPedestalPage(TWISTED_ROCK_ITEM_PEDESTAL.asItem(), TWISTED_ROCK.asItem(), TWISTED_ROCK_SLAB.asItem()))
				.addPage(CraftingBookPage.itemStandPage(TWISTED_ROCK_ITEM_STAND.asItem(), TWISTED_ROCK.asItem(), TWISTED_ROCK_SLAB.asItem()))
		);
		ENTRIES.add(new BookEntry(
				"ether", ETHER.asItem(), 5, 6)
				.addPage(new HeadlineTextPage("ether", "ether.1"))
				.addPage(SpiritInfusionPage.fromOutput(ETHER.asItem()))
				.addPage(new TextPage("ether.2"))
				.addPage(new CraftingBookPage(ETHER_TORCH.asItem(), EMPTY, EMPTY, EMPTY, EMPTY, ETHER.asItem(), EMPTY, EMPTY, STICK, EMPTY))
				.addPage(new CraftingBookPage(TAINTED_ETHER_BRAZIER.asItem(), EMPTY, EMPTY, EMPTY, TAINTED_ROCK.asItem(), ETHER.asItem(), TAINTED_ROCK.asItem(), STICK, TAINTED_ROCK.asItem(), STICK))
				.addPage(new CraftingBookPage(TWISTED_ETHER_BRAZIER.asItem(), EMPTY, EMPTY, EMPTY, TWISTED_ROCK.asItem(), ETHER.asItem(), TWISTED_ROCK.asItem(), STICK, TWISTED_ROCK.asItem(), STICK))
				.addPage(new HeadlineTextPage("ether.iridescent", "ether.iridescent.1"))
				.addPage(new TextPage("ether.iridescent.2"))
				.addPage(SpiritInfusionPage.fromOutput(IRIDESCENT_ETHER.asItem()))
				.addPage(new CraftingBookPage(IRIDESCENT_ETHER_TORCH.asItem(), EMPTY, EMPTY, EMPTY, EMPTY, IRIDESCENT_ETHER.asItem(), EMPTY, EMPTY, STICK, EMPTY))
				.addPage(new CraftingBookPage(TAINTED_IRIDESCENT_ETHER_BRAZIER.asItem(), EMPTY, EMPTY, EMPTY, TAINTED_ROCK.asItem(), IRIDESCENT_ETHER.asItem(), TAINTED_ROCK.asItem(), STICK, TAINTED_ROCK.asItem(), STICK))
				.addPage(new CraftingBookPage(TWISTED_IRIDESCENT_ETHER_BRAZIER.asItem(), EMPTY, EMPTY, EMPTY, TWISTED_ROCK.asItem(), IRIDESCENT_ETHER.asItem(), TWISTED_ROCK.asItem(), STICK, TWISTED_ROCK.asItem(), STICK))
		);

		ENTRIES.add(new BookEntry(
				"spirit_fabric", SPIRIT_FABRIC, 4, 5)
				.addPage(new HeadlineTextPage("spirit_fabric", "spirit_fabric.1"))
				.addPage(SpiritInfusionPage.fromOutput(SPIRIT_FABRIC))
				.addPage(new HeadlineTextPage("spirit_fabric.pouch", "spirit_fabric.pouch.1"))
				.addPage(new CraftingBookPage(SPIRIT_POUCH, EMPTY, STRING, EMPTY, SPIRIT_FABRIC, SOUL_SAND, SPIRIT_FABRIC, EMPTY, SPIRIT_FABRIC, EMPTY))
		);

		ENTRIES.add(new BookEntry(
				"soulhunter_gear", SOUL_HUNTER_CLOAK, 4, 7)
				.addPage(new HeadlineTextPage("soulhunter_gear", "soulhunter_gear.1"))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_HUNTER_CLOAK))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_HUNTER_ROBE))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_HUNTER_LEGGINGS))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_HUNTER_BOOTS))
		);


		ENTRIES.add(new BookEntry(
				"soul_something", 3, 8)
				.setObjectSupplier((e, x, y) -> new IconObject(e, id("textures/gui/book/icons/soul_blade.png"), x, y))
		);

		ENTRIES.add(new BookEntry(
				"spirit_focusing", SPIRIT_CRUCIBLE.asItem(), 7, 6)
				.addPage(new HeadlineTextItemPage("spirit_focusing", "spirit_focusing.1", SPIRIT_CRUCIBLE.asItem()))
				.addPage(new TextPage("spirit_focusing.2"))
				.addPage(SpiritInfusionPage.fromOutput(SPIRIT_CRUCIBLE.asItem()))
				.addPage(SpiritInfusionPage.fromOutput(ALCHEMICAL_IMPETUS))
		);

		ENTRIES.add(new BookEntry(
				"focus_ashes", GUNPOWDER, 6, 5)
				.addPage(new HeadlineTextPage("focus_ashes", "focus_ashes.1"))
				.addPage(SpiritCruciblePage.fromOutput(GUNPOWDER))
				.addPage(SpiritCruciblePage.fromOutput(GLOWSTONE_DUST))
				.addPage(SpiritCruciblePage.fromOutput(REDSTONE))
		);

		ENTRIES.add(new BookEntry(
				"focus_metals", IRON_NODE, 8, 7)
				.addPage(new HeadlineTextItemPage("focus_metals", "focus_metals.1", IRON_NODE))
				.addPage(new TextPage("focus_metals.2"))
				.addPage(SpiritInfusionPage.fromOutput(IRON_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(IRON_NODE))
				.addPage(SpiritInfusionPage.fromOutput(GOLD_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(GOLD_NODE))
				.addPage(SpiritInfusionPage.fromOutput(COPPER_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(COPPER_NODE))
				.addPage(SpiritInfusionPage.fromOutput(LEAD_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(LEAD_NODE))
				.addPage(SpiritInfusionPage.fromOutput(SILVER_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(SILVER_NODE))
				.addPage(SpiritInfusionPage.fromOutput(ALUMINUM_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(ALUMINUM_NODE))
				.addPage(SpiritInfusionPage.fromOutput(NICKEL_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(NICKEL_NODE))
				.addPage(SpiritInfusionPage.fromOutput(URANIUM_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(URANIUM_NODE))
				.addPage(SpiritInfusionPage.fromOutput(OSMIUM_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(OSMIUM_NODE))
				.addPage(SpiritInfusionPage.fromOutput(ZINC_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(ZINC_NODE))
				.addPage(SpiritInfusionPage.fromOutput(TIN_IMPETUS))
				.addPage(SpiritCruciblePage.fromOutput(TIN_NODE))
		);

		ENTRIES.add(new BookEntry(
				"focus_crystals", QUARTZ, 9, 5)
				.addPage(new HeadlineTextPage("focus_crystals", "focus_crystals.1"))
				.addPage(SpiritCruciblePage.fromOutput(QUARTZ))
				.addPage(SpiritCruciblePage.fromOutput(AMETHYST_SHARD))
				.addPage(SpiritCruciblePage.fromOutput(BLAZING_QUARTZ))
				.addPage(SpiritCruciblePage.fromOutput(PRISMARINE))
		);

		ENTRIES.add(new BookEntry(
				"crucible_acceleration", SPIRIT_CATALYZER.asItem(), 7, 4)
				.addPage(new HeadlineTextPage("crucible_acceleration", "crucible_acceleration.1"))
				.addPage(new TextPage("crucible_acceleration.2"))
				.addPage(new TextPage("crucible_acceleration.3"))
				.addPage(SpiritInfusionPage.fromOutput(SPIRIT_CATALYZER.asItem()))
		);

		ENTRIES.add(new BookEntry(
				"arcane_restoration", TWISTED_TABLET.asItem(), 7, 8)
				.addPage(new HeadlineTextPage("arcane_restoration", "arcane_restoration.1"))
				.addPage(new TextPage("arcane_restoration.2"))
				.addPage(SpiritInfusionPage.fromOutput(TWISTED_TABLET.asItem()))
				.addPage(SpiritRepairPage.fromInput(CRACKED_ALCHEMICAL_IMPETUS))
				.addPage(SpiritRepairPage.fromInput(CRACKED_COPPER_IMPETUS))
				.addPage(SpiritRepairPage.fromInput(WOODEN_PICKAXE))
				.addPage(SpiritRepairPage.fromInput(STONE_PICKAXE))
				.addPage(SpiritRepairPage.fromInput(IRON_PICKAXE))
				.addPage(SpiritRepairPage.fromInput(DIAMOND_PICKAXE))
				.addPage(SpiritRepairPage.fromInput(GOLDEN_PICKAXE))
				.addPage(SpiritRepairPage.fromInput(NETHERITE_PICKAXE))
				.addPage(new TextPage("arcane_restoration.3"))
				.addPage(SpiritRepairPage.fromInput(SOUL_STAINED_STEEL_PICKAXE))
				.addPage(SpiritRepairPage.fromInput(SOUL_STAINED_STEEL_SCYTHE))
				.addPage(SpiritRepairPage.fromInput(SOUL_HUNTER_BOOTS))
		);

		ENTRIES.add(new BookEntry(
				"spirit_metals", SOUL_STAINED_STEEL_INGOT, -3, 6)
				.addPage(new HeadlineTextItemPage("spirit_metals.soulstained_steel", "spirit_metals.soulstained_steel.1", SOUL_STAINED_STEEL_INGOT))
				.addPage(new TextPage("spirit_metals.soulstained_steel.2"))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_STAINED_STEEL_INGOT))
				.addPage(CraftingBookPage.resonatorPage(STAINED_SPIRIT_RESONATOR, QUARTZ, SOUL_STAINED_STEEL_INGOT, RUNEWOOD_PLANKS.asItem()))
				.addPage(CraftingBookPage.toolPage(SOUL_STAINED_STEEL_PICKAXE, SOUL_STAINED_STEEL_INGOT))
				.addPage(CraftingBookPage.toolPage(SOUL_STAINED_STEEL_AXE, SOUL_STAINED_STEEL_INGOT))
				.addPage(CraftingBookPage.toolPage(SOUL_STAINED_STEEL_HOE, SOUL_STAINED_STEEL_INGOT))
				.addPage(CraftingBookPage.toolPage(SOUL_STAINED_STEEL_SHOVEL, SOUL_STAINED_STEEL_INGOT))
				.addPage(CraftingBookPage.toolPage(SOUL_STAINED_STEEL_SWORD, SOUL_STAINED_STEEL_INGOT))
				//.addModCompatPage(new CraftingBookPage(SOUL_STAINED_STEEL_KNIFE, EMPTY, EMPTY, EMPTY, EMPTY, SOUL_STAINED_STEEL_INGOT, EMPTY, STICK), "farmersdelight")
				.addPage(new HeadlineTextItemPage("spirit_metals.hallowed_gold", "spirit_metals.hallowed_gold.1", HALLOWED_GOLD_INGOT))
				.addPage(new TextPage("spirit_metals.hallowed_gold.2"))
				.addPage(SpiritInfusionPage.fromOutput(HALLOWED_GOLD_INGOT))
				.addPage(CraftingBookPage.resonatorPage(HALLOWED_SPIRIT_RESONATOR, QUARTZ, HALLOWED_GOLD_INGOT, RUNEWOOD_PLANKS.asItem()))
				.addPage(new HeadlineTextPage("spirit_metals.hallowed_gold.spirit_jar", "spirit_metals.hallowed_gold.spirit_jar.1"))
				.addPage(new CraftingBookPage(SPIRIT_JAR.asItem(), GLASS_PANE, HALLOWED_GOLD_INGOT, GLASS_PANE, GLASS_PANE, EMPTY, GLASS_PANE, GLASS_PANE, GLASS_PANE, GLASS_PANE))
		);

		ENTRIES.add(new BookEntry(
				"soulstained_scythe", SOUL_STAINED_STEEL_SCYTHE, -4, 5)
				.addPage(new HeadlineTextPage("soulstained_scythe", "soulstained_scythe.1"))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_STAINED_STEEL_SCYTHE))
		);

		ENTRIES.add(new BookEntry(
				"soulstained_armor", SOUL_STAINED_STEEL_HELMET, -4, 7)
				.addPage(new HeadlineTextPage("soulstained_armor", "soulstained_armor.1"))
				.addPage(new TextPage("soulstained_armor.2"))
				.addPage(new TextPage("soulstained_armor.3"))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_STAINED_STEEL_HELMET))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_STAINED_STEEL_CHESTPLATE))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_STAINED_STEEL_LEGGINGS))
				.addPage(SpiritInfusionPage.fromOutput(SOUL_STAINED_STEEL_BOOTS))
		);

		ENTRIES.add(new BookEntry(
				"soul_ward", -3, 8)
				.setObjectSupplier((e, x, y) -> new IconObject(e, id("textures/gui/book/icons/soul_ward.png"), x, y))
		);

		ENTRIES.add(new BookEntry(
				"spirit_trinkets", ORNATE_RING, -5, 6)
				.addPage(new HeadlineTextPage("spirit_trinkets", "spirit_trinkets.1"))
				.addPage(new TextPage("spirit_trinkets.2"))
				.addPage(CraftingBookPage.ringPage(GILDED_RING, LEATHER, HALLOWED_GOLD_INGOT))
				.addPage(new CraftingBookPage(GILDED_BELT, LEATHER, LEATHER, LEATHER, HALLOWED_GOLD_INGOT, PROCESSED_SOULSTONE, HALLOWED_GOLD_INGOT, EMPTY, HALLOWED_GOLD_INGOT, EMPTY))
				.addPage(CraftingBookPage.ringPage(ORNATE_RING, LEATHER, SOUL_STAINED_STEEL_INGOT))
				.addPage(new CraftingBookPage(ORNATE_NECKLACE, EMPTY, STRING, EMPTY, STRING, EMPTY, STRING, EMPTY, SOUL_STAINED_STEEL_INGOT, EMPTY))
		);

		ENTRIES.add(new BookEntry(
				"reactive_trinkets", RING_OF_ALCHEMICAL_MASTERY, -7, 6)
				.addPage(new HeadlineTextPage("reactive_trinkets.ring_of_alchemical_mastery", "reactive_trinkets.ring_of_alchemical_mastery.1"))
				.addPage(SpiritInfusionPage.fromOutput(RING_OF_ALCHEMICAL_MASTERY))
				.addPage(new HeadlineTextPage("reactive_trinkets.ring_of_curative_talent", "reactive_trinkets.ring_of_curative_talent.1"))
				.addPage(SpiritInfusionPage.fromOutput(RING_OF_CURATIVE_TALENT))
				.addPage(new HeadlineTextPage("reactive_trinkets.ring_of_prowess", "reactive_trinkets.ring_of_prowess.1"))
				.addPage(new TextPage("reactive_trinkets.ring_of_prowess.2"))
				.addPage(SpiritInfusionPage.fromOutput(RING_OF_ARCANE_PROWESS))
		);

		ENTRIES.add(new BookEntry(
				"ring_of_esoteric_spoils", RING_OF_ESOTERIC_SPOILS, -9, 5)
				.addPage(new HeadlineTextPage("ring_of_esoteric_spoils", "ring_of_esoteric_spoils.1"))
				.addPage(SpiritInfusionPage.fromOutput(RING_OF_ESOTERIC_SPOILS))
		);

		ENTRIES.add(new BookEntry(
				"belt_of_the_starved", BELT_OF_THE_STARVED, -8, 7)
				.addPage(new HeadlineTextPage("belt_of_the_starved", "belt_of_the_starved.1"))
				.addPage(new TextPage("belt_of_the_starved.2"))
				.addPage(SpiritInfusionPage.fromOutput(BELT_OF_THE_STARVED))
				.addPage(new HeadlineTextPage("belt_of_the_starved.ring_of_desperate_voracity", "belt_of_the_starved.ring_of_desperate_voracity.1"))
				.addPage(SpiritInfusionPage.fromOutput(RING_OF_DESPERATE_VORACITY))
		);

		ENTRIES.add(new BookEntry(
				"necklace_of_the_narrow_edge", NECKLACE_OF_THE_NARROW_EDGE, -7, 8)
				.addPage(new HeadlineTextPage("necklace_of_the_narrow_edge", "necklace_of_the_narrow_edge.1"))
				.addPage(SpiritInfusionPage.fromOutput(NECKLACE_OF_THE_NARROW_EDGE))
		);

		ENTRIES.add(new BookEntry(
				"belt_of_the_prospector", BELT_OF_THE_PROSPECTOR, -6, 5)
				.addPage(new HeadlineTextPage("belt_of_the_prospector", "belt_of_the_prospector.1"))
				.addPage(SpiritInfusionPage.fromOutput(BELT_OF_THE_PROSPECTOR))
				.addPage(new HeadlineTextPage("belt_of_the_prospector.ring_of_the_hoarder", "belt_of_the_prospector.ring_of_the_hoarder.1"))
				.addPage(SpiritInfusionPage.fromOutput(RING_OF_THE_HOARDER))
		);

//        ENTRIES.add(new BookEntry(
//            "necklace_of_elemental_shielding", GILDED_BELT, -7, 4)
//            .addPage(new HeadlineTextPage("necklace_of_elemental_shielding", "necklace_of_elemental_shielding.1"))
//            .addPage(SpiritInfusionPage.fromOutput(NECKLACE_OF_TIDAL_AFFINITY))
//        );

		ENTRIES.add(new BookEntry(
				"necklace_of_the_mystic_mirror", NECKLACE_OF_THE_MYSTIC_MIRROR, 6, 12)
				.addPage(new HeadlineTextPage("necklace_of_the_mystic_mirror", "necklace_of_the_mystic_mirror.1"))
				.addPage(SpiritInfusionPage.fromOutput(NECKLACE_OF_THE_MYSTIC_MIRROR))
		);

		ENTRIES.add(new BookEntry(
				"mirror_magic", SPECTRAL_LENS, 6, 10)
				.setObjectSupplier(ImportantEntryObject::new)
				.addPage(new HeadlineTextPage("mirror_magic", "mirror_magic.1"))
				.addPage(SpiritInfusionPage.fromOutput(SPECTRAL_LENS))
		);

		ENTRIES.add(new BookEntry(
				"voodoo_magic", POPPET, -6, 10)
				.setObjectSupplier(ImportantEntryObject::new)
				.addPage(new HeadlineTextPage("voodoo_magic", "voodoo_magic.1"))
				.addPage(SpiritInfusionPage.fromOutput(POPPET))
		);

		ENTRIES.add(new BookEntry(
				"altar_acceleration", RUNEWOOD_OBELISK.asItem(), -1, 8)
				.addPage(new HeadlineTextPage("altar_acceleration.runewood_obelisk", "altar_acceleration.runewood_obelisk.1"))
				.addPage(SpiritInfusionPage.fromOutput(RUNEWOOD_OBELISK.asItem()))
				.addPage(new HeadlineTextPage("altar_acceleration.brilliant_obelisk", "altar_acceleration.brilliant_obelisk.1"))
				.addPage(SpiritInfusionPage.fromOutput(BRILLIANT_OBELISK.asItem()))
		);

		ENTRIES.add(new BookEntry(
				"totem_magic", RUNEWOOD_TOTEM_BASE.asItem(), 0, 9)
				.setObjectSupplier(ImportantEntryObject::new)
				.addPage(new HeadlineTextItemPage("totem_magic", "totem_magic.1", RUNEWOOD_TOTEM_BASE.asItem()))
				.addPage(new TextPage("totem_magic.2"))
				.addPage(new TextPage("totem_magic.3"))
				.addPage(new TextPage("totem_magic.4"))
				.addPage(new TextPage("totem_magic.5"))
				.addPage(SpiritInfusionPage.fromOutput(RUNEWOOD_TOTEM_BASE.asItem()))
		);

		ENTRIES.add(new BookEntry(
				"sacred_rite", -2, 10)
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.SACRED_RITE, "sacred_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.SACRED_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_SACRED_RITE, "sacred_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_SACRED_RITE))
		);

		ENTRIES.add(new BookEntry(
				"corrupt_sacred_rite", -3, 10).setSoulwood()
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.SACRED_RITE, "corrupt_sacred_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.SACRED_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_SACRED_RITE, "corrupt_sacred_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_SACRED_RITE))
		);

		ENTRIES.add(new BookEntry(
				"infernal_rite", -3, 11)
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.INFERNAL_RITE, "infernal_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.INFERNAL_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_INFERNAL_RITE, "infernal_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_INFERNAL_RITE))
		);

		ENTRIES.add(new BookEntry(
				"corrupt_infernal_rite", -4, 11).setSoulwood()
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.INFERNAL_RITE, "corrupt_infernal_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.INFERNAL_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_INFERNAL_RITE, "corrupt_infernal_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_INFERNAL_RITE))
		);

		ENTRIES.add(new BookEntry(
				"earthen_rite", -3, 12)
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.EARTHEN_RITE, "earthen_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.EARTHEN_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_EARTHEN_RITE, "earthen_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_EARTHEN_RITE))
		);

		ENTRIES.add(new BookEntry(
				"corrupt_earthen_rite", -4, 12).setSoulwood()
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.EARTHEN_RITE, "corrupt_earthen_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.EARTHEN_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_EARTHEN_RITE, "corrupt_earthen_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_EARTHEN_RITE))
		);

		ENTRIES.add(new BookEntry(
				"wicked_rite", 2, 10)
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.WICKED_RITE, "wicked_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.WICKED_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_WICKED_RITE, "wicked_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_WICKED_RITE))
		);

		ENTRIES.add(new BookEntry(
				"corrupt_wicked_rite", 3, 10).setSoulwood()
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.WICKED_RITE, "corrupt_wicked_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.WICKED_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_WICKED_RITE, "corrupt_wicked_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_WICKED_RITE))
		);

		ENTRIES.add(new BookEntry(
				"aerial_rite", 3, 11)
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.AERIAL_RITE, "aerial_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.AERIAL_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_AERIAL_RITE, "aerial_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_AERIAL_RITE))
		);

		ENTRIES.add(new BookEntry(
				"corrupt_aerial_rite", 4, 11).setSoulwood()
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.AERIAL_RITE, "corrupt_aerial_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.AERIAL_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_AERIAL_RITE, "corrupt_aerial_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_AERIAL_RITE))
		);

		ENTRIES.add(new BookEntry(
				"aqueous_rite", 3, 12)
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.AQUEOUS_RITE, "aqueous_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.AQUEOUS_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_AQUEOUS_RITE, "aqueous_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_AQUEOUS_RITE))
		);

		ENTRIES.add(new BookEntry(
				"corrupt_aqueous_rite", 4, 12).setSoulwood()
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.AQUEOUS_RITE, "corrupt_aqueous_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.AQUEOUS_RITE))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ELDRITCH_AQUEOUS_RITE, "corrupt_aqueous_rite.greater"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ELDRITCH_AQUEOUS_RITE))
		);



		ENTRIES.add(new BookEntry(
				"arcane_rite", 0, 11)
				.setObjectSupplier(RiteEntryObject::new)
				.addPage(new HeadlineTextPage("arcane_rite", "arcane_rite.description.1"))
				.addPage(new TextPage("arcane_rite.description.2"))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ARCANE_RITE, "arcane_rite"))
				.addPage(new SpiritRiteRecipePage(MalumRiteRegistry.ARCANE_RITE))
				.addPage(new TextPage("arcane_rite.description.3"))
				.addPage(new SpiritRiteTextPage(MalumRiteRegistry.ARCANE_RITE, "corrupt_arcane_rite"))
				.addPage(new TextPage("arcane_rite.description.4"))
				.addPage(SpiritInfusionPage.fromOutput(SOULWOOD_TOTEM_BASE.asItem()))
		);

		/*
		ENTRIES.add(new BookEntry(
				"blight", BLIGHTED_GUNK, -1, 12).setSoulwood()
				.setObjectSupplier(MinorEntryObject::new)
				.addPage(new HeadlineTextPage("blight.intro", "blight.intro.1"))
				.addPage(new HeadlineTextPage("blight.composition", "blight.composition.1"))
				.addPage(new HeadlineTextPage("blight.spread", "blight.spread.1"))
				.addPage(new HeadlineTextPage("blight.arcane_rite", "blight.arcane_rite.1"))
		);


 */


		ENTRIES.add(new BookEntry(
				"soulwood", SOULWOOD_GROWTH.asItem(), 1, 12).setSoulwood()
				.setObjectSupplier(MinorEntryObject::new)
				.addPage(new HeadlineTextPage("soulwood.intro", "soulwood.intro.1"))
				.addPage(new HeadlineTextPage("soulwood.bonemeal", "soulwood.bonemeal.1"))
				.addPage(new HeadlineTextPage("soulwood.color", "soulwood.color.1"))
				.addPage(new HeadlineTextPage("soulwood.blight", "soulwood.blight.1"))
				.addPage(new HeadlineTextPage("soulwood.sap", "soulwood.sap.1"))
		);
		ENTRIES.add(new BookEntry(
				"transmutation", BLIGHTED_SOIL.asItem(), 0, 13).setSoulwood()
				.addPage(new HeadlineTextPage("transmutation", "transmutation.intro.1"))
				.addPage(new TextPage("transmutation.intro.2"))
				.addPage(new SpiritTransmutationPage("transmutation.stone", STONE))
				.addPage(new SpiritTransmutationPage("transmutation.deepslate", DEEPSLATE))
				.addPage(new SpiritTransmutationPage("transmutation.smooth_basalt", SMOOTH_BASALT))
		);

//        ENTRIES.add(new BookEntry(
//                "alteration_plinth", ALTERATION_PLINTH, 1, 13).setSoulwood()
//                .addPage(new HeadlineTextPage("alteration_plinth", "alteration_plinth.intro.1"))
//                .addPage(SpiritInfusionPage.fromOutput(ALTERATION_PLINTH))
//        );

		ENTRIES.add(new BookEntry(
				"metallurgic_trinkets", NECKLACE_OF_BLISSFUL_HARMONY, -2, 14).setSoulwood()
				.addPage(new HeadlineTextPage("necklace_of_blissful_harmony", "necklace_of_blissful_harmony.1"))
				.addPage(SpiritInfusionPage.fromOutput(NECKLACE_OF_BLISSFUL_HARMONY))
				.addPage(new HeadlineTextPage("ring_of_the_demolitionist", "ring_of_the_demolitionist.1"))
				.addPage(SpiritInfusionPage.fromOutput(RING_OF_THE_DEMOLITIONIST))
				.addPage(new HeadlineTextPage("necklace_of_tidal_affinity", "necklace_of_tidal_affinity.1"))
				.addPage(SpiritInfusionPage.fromOutput(NECKLACE_OF_TIDAL_AFFINITY))
		);

		ENTRIES.add(new BookEntry(
				"etheric_nitrate", ETHERIC_NITRATE, 2, 14).setSoulwood()
				.addPage(new HeadlineTextPage("etheric_nitrate", "etheric_nitrate.1"))
				.addPage(SpiritInfusionPage.fromOutput(ETHERIC_NITRATE))
				.addPage(new HeadlineTextPage("etheric_nitrate.vivid_nitrate", "etheric_nitrate.vivid_nitrate.1"))
				.addPage(SpiritInfusionPage.fromOutput(VIVID_NITRATE))
		);

		ENTRIES.add(new BookEntry(
				"corrupted_resonance", CORRUPTED_RESONANCE, 0, 15).setSoulwood()
				.addPage(new HeadlineTextPage("corrupted_resonance", "corrupted_resonance.1"))
				.addPage(SpiritInfusionPage.fromOutput(CORRUPTED_RESONANCE))
		);

		ENTRIES.add(new BookEntry(
				"magebane_belt", BELT_OF_THE_MAGEBANE, -1, 16).setSoulwood()
				.addPage(new HeadlineTextPage("magebane_belt", "magebane_belt.1"))
				.addPage(SpiritInfusionPage.fromOutput(BELT_OF_THE_MAGEBANE))
		);

		ENTRIES.add(new BookEntry(
				"necklace_of_the_hidden_blade", NECKLACE_OF_THE_HIDDEN_BLADE, 1, 16).setSoulwood()
				.addPage(new HeadlineTextPage("necklace_of_the_hidden_blade", "necklace_of_the_hidden_blade.1"))
				.addPage(SpiritInfusionPage.fromOutput(NECKLACE_OF_THE_HIDDEN_BLADE))
		);

		ENTRIES.add(new BookEntry(
				"tyrving", TYRVING, 0, 17).setSoulwood()
				.addPage(new HeadlineTextPage("tyrving", "tyrving.1"))
				.addPage(SpiritInfusionPage.fromOutput(TYRVING))
				.addPage(new TextPage("tyrving.2"))
				.addPage(SpiritRepairPage.fromInput(TYRVING))
		);

		ENTRIES.add(new BookEntry(
				"the_device", THE_DEVICE.asItem(), 0, -10)
				.setObjectSupplier(VanishingEntryObject::new)
				.addPage(new HeadlineTextPage("the_device", "the_device"))
				.addPage(new CraftingBookPage(THE_DEVICE.asItem(), TWISTED_ROCK.asItem(), TAINTED_ROCK.asItem(), TWISTED_ROCK.asItem(), TAINTED_ROCK.asItem(), TWISTED_ROCK.asItem(), TAINTED_ROCK.asItem(), TWISTED_ROCK.asItem(), TAINTED_ROCK.asItem(), TWISTED_ROCK.asItem()))
		);
	}



}

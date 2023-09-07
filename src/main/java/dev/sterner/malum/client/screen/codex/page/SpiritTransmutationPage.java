package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.lodestone.helpers.DataHelper;
import dev.sterner.lodestone.systems.recipe.WrappedIngredient;
import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import dev.sterner.malum.common.recipe.SpiritTransmutationRecipe;
import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.*;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.screen;

public class SpiritTransmutationPage extends BookPage {
	private final String headlineTranslationKey;
	private final List<WrappedIngredient> itemTree = new ArrayList<>();

	public SpiritTransmutationPage(String headlineTranslationKey, Item start) {
		super(Malum.id("textures/gui/book/pages/spirit_transmutation_page.png"));
		this.headlineTranslationKey = headlineTranslationKey;
		if (MinecraftClient.getInstance() == null) //this is null during datagen
		{
			return;
		}
		SpiritTransmutationRecipe recipe = SpiritTransmutationRecipe.getRecipe(MinecraftClient.getInstance().world, start);
		while (true) {
			if (recipe == null) {
				itemTree.add(new WrappedIngredient(Ingredient.ofItems(MalumObjects.BLIGHTED_SOIL)));
				break;
			}
			itemTree.add(new WrappedIngredient(recipe.ingredient));
			ItemStack output = recipe.output;
			recipe = SpiritTransmutationRecipe.getRecipe(MinecraftClient.getInstance().world, s -> s.ingredient.test(output));
		}
	}

	public String headlineTranslationKey() {
		return "malum.gui.book.entry.page.headline." + headlineTranslationKey;
	}

	@Override
	public boolean isValid() {
		return !itemTree.isEmpty();
	}

	@Override
	public void renderLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		renderText(ctx, component, guiLeft + 75 - minecraft.textRenderer.getWidth(component.getString()) / 2, guiTop + 10);
		List<WrappedIngredient> copy = new ArrayList<>(itemTree);
		renderComponent(screen, ctx, copy.remove(0), guiLeft + 67, guiTop + 44, mouseX, mouseY);
		renderComponent(screen, ctx, copy.remove(copy.size() - 1), guiLeft + 67, guiTop + 126, mouseX, mouseY);
		renderComponents(screen, ctx, copy, guiLeft + 65, guiTop + 82, mouseX, mouseY, false);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		renderText(ctx, component, guiLeft + 218 - minecraft.textRenderer.getWidth(component.getString()) / 2, guiTop + 10);
		List<WrappedIngredient> copy = new ArrayList<>(itemTree);
		renderComponent(screen, ctx, copy.remove(0), guiLeft + 209, guiTop + 44, mouseX, mouseY);
		renderComponent(screen, ctx, copy.remove(copy.size() - 1), guiLeft + 209, guiTop + 126, mouseX, mouseY);
		renderComponents(screen, ctx, DataHelper.reverseOrder(new ArrayList<>(), copy), guiLeft + 207, guiTop + 82, mouseX, mouseY, false);
	}
}

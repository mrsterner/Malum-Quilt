package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import dev.sterner.malum.common.recipe.SpiritInfusionRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;

import java.util.function.Predicate;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.*;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.screen;

public class SpiritInfusionPage extends BookPage {
	private final SpiritInfusionRecipe recipe;

	public SpiritInfusionPage(Predicate<SpiritInfusionRecipe> predicate) {
		super(Malum.id("textures/gui/book/pages/spirit_infusion_page.png"));
		if (MinecraftClient.getInstance() == null) //this is null during datagen
		{
			this.recipe = null;
			return;
		}
		this.recipe = SpiritInfusionRecipe.getRecipe(MinecraftClient.getInstance().world, predicate);
	}

	public SpiritInfusionPage(SpiritInfusionRecipe recipe) {
		super(Malum.id("textures/gui/book/pages/spirit_infusion_page.png"));
		this.recipe = recipe;
	}

	public static SpiritInfusionPage fromInput(Item inputItem) {
		return new SpiritInfusionPage(s -> s.doesInputMatch(inputItem.getDefaultStack()));
	}

	public static SpiritInfusionPage fromOutput(Item outputItem) {
		return new SpiritInfusionPage(s -> s.doesOutputMatch(outputItem.getDefaultStack()));
	}

	@Override
	public boolean isValid() {
		return recipe != null;
	}

	@Override
	public void renderLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Runnable renderSpirits = renderBufferedComponents(screen, ctx, recipe.spirits, guiLeft + 23, guiTop + 59, mouseX, mouseY, true);
		if (!recipe.extraItems.isEmpty()) {
			renderComponents(screen, ctx, recipe.extraItems, guiLeft + 107, guiTop + 59, mouseX, mouseY, true);
		}
		renderSpirits.run();
		renderComponent(screen, ctx, recipe.input, guiLeft + 67, guiTop + 59, mouseX, mouseY);
		renderItem(screen, ctx, recipe.output, guiLeft + 67, guiTop + 126, mouseX, mouseY);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Runnable renderSpirits = renderBufferedComponents(screen, ctx, recipe.spirits, guiLeft + 165, guiTop + 59, mouseX, mouseY, true);
		if (!recipe.extraItems.isEmpty()) {
			Runnable renderExtraItems = renderBufferedComponents(screen, ctx, recipe.extraItems, guiLeft + 249, guiTop + 59, mouseX, mouseY, true);
			renderExtraItems.run();
		}
		renderSpirits.run();
		renderComponent(screen, ctx, recipe.input, guiLeft + 209, guiTop + 59, mouseX, mouseY);
		renderItem(screen, ctx, recipe.output, guiLeft + 209, guiTop + 126, mouseX, mouseY);
	}
}

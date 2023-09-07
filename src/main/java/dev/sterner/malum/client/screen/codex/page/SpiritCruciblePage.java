package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import dev.sterner.malum.common.recipe.SpiritFocusingRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;

import java.util.function.Predicate;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderComponents;
import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderItem;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.screen;

public class SpiritCruciblePage extends BookPage {
	private final SpiritFocusingRecipe recipe;

	public SpiritCruciblePage(Predicate<SpiritFocusingRecipe> predicate) {
		super(Malum.id("textures/gui/book/pages/spirit_crucible_page.png"));
		if (MinecraftClient.getInstance() == null) //this is null during datagen
		{
			this.recipe = null;
			return;
		}
		this.recipe = SpiritFocusingRecipe.getRecipe(MinecraftClient.getInstance().world, predicate);
	}

	public SpiritCruciblePage(SpiritFocusingRecipe recipe) {
		super(Malum.id("textures/gui/book/pages/spirit_crucible_page.png"));
		this.recipe = recipe;
	}

	@Override
	public boolean isValid() {
		return recipe != null;
	}

	public static SpiritCruciblePage fromInput(Item inputItem) {
		return new SpiritCruciblePage(s -> s.doesInputMatch(inputItem.getDefaultStack()));
	}

	public static SpiritCruciblePage fromOutput(Item outputItem) {
		return new SpiritCruciblePage(s -> s.doesOutputMatch(outputItem.getDefaultStack()));
	}

	@Override
	public void renderLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		renderItem(screen, ctx, recipe.input, guiLeft + 67, guiTop + 59, mouseX, mouseY);
		renderItem(screen, ctx, recipe.output, guiLeft + 67, guiTop + 126, mouseX, mouseY);
		renderComponents(screen, ctx, recipe.spirits, guiLeft + 65, guiTop + 16, mouseX, mouseY, false);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		renderItem(screen, ctx, recipe.input, guiLeft + 209, guiTop + 59, mouseX, mouseY);
		renderItem(screen, ctx, recipe.output, guiLeft + 209, guiTop + 126, mouseX, mouseY);
		renderComponents(screen, ctx, recipe.spirits, guiLeft + 207, guiTop + 16, mouseX, mouseY, false);
	}
}

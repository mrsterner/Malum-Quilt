package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.client.screen.codex.EntryScreen;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderTexture;

public class BookPage {
	public final Identifier BACKGROUND;

	public BookPage(Identifier background) {
		this.BACKGROUND = background;
	}

	public boolean isValid() {
		return true;
	}

	public void renderLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {

	}

	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {

	}

	public void renderBackgroundLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		renderTexture(BACKGROUND, ctx, guiLeft, guiTop, 1, 1, EntryScreen.screen.bookWidth - 147, EntryScreen.screen.bookHeight, 512, 512);
	}

	public void renderBackgroundRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		renderTexture(BACKGROUND, ctx, guiLeft + 147, guiTop, 148, 1, EntryScreen.screen.bookWidth - 147, EntryScreen.screen.bookHeight, 512, 512);
	}

	public int guiLeft() {
		return (EntryScreen.screen.width - EntryScreen.screen.bookWidth) / 2;
	}

	public int guiTop() {
		return (EntryScreen.screen.height - EntryScreen.screen.bookHeight) / 2;
	}
}

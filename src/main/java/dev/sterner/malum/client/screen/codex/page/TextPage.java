package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderWrappingText;

public class TextPage extends BookPage {
	public final String translationKey;

	public TextPage(String translationKey) {
		super(Malum.id("textures/gui/book/pages/blank_page.png"));
		this.translationKey = translationKey;
	}



	public String translationKey() {
		return "malum.gui.book.entry.page.text." + translationKey;
	}

	@Override
	public void renderLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		renderWrappingText(ctx, translationKey(), guiLeft + 14, guiTop + 10, 126);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		renderWrappingText(ctx, translationKey(), guiLeft + 156, guiTop + 10, 126);
	}
}

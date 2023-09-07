package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderText;
import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderWrappingText;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.screen;

public class HeadlineTextPage extends BookPage{
	private final String headlineTranslationKey;
	private final String translationKey;

	public HeadlineTextPage(String headlineTranslationKey, String translationKey) {
		super(Malum.id("textures/gui/book/pages/headline_page.png"));
		this.headlineTranslationKey = headlineTranslationKey;
		this.translationKey = translationKey;
	}

	public String headlineTranslationKey() {
		return "malum.gui.book.entry.page.headline." + headlineTranslationKey;
	}

	public String translationKey() {
		return "malum.gui.book.entry.page.text." + translationKey;
	}

	@Override
	public void renderLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		renderText(ctx, component, guiLeft + 75 - minecraft.textRenderer.getWidth(component.getString()) / 2, guiTop + 10);
		renderWrappingText(ctx, translationKey(), guiLeft + 14, guiTop + 31, 125);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		renderText(ctx, component, guiLeft + 218 - minecraft.textRenderer.getWidth(component.getString()) / 2, guiTop + 10);
		renderWrappingText(ctx, translationKey(), guiLeft + 156, guiTop + 31, 125);
	}
}

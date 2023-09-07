package dev.sterner.malum.client.screen.codex.page;


import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.*;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.screen;

public class SpiritRiteTextPage extends BookPage {
	public final MalumRiteType riteType;
	private final String translationKey;

	public SpiritRiteTextPage(MalumRiteType riteType, String translationKey) {
		super(Malum.id("textures/gui/book/pages/spirit_rite_page.png"));
		this.riteType = riteType;
		this.translationKey = translationKey;
	}

	public String headlineTranslationKey() {
		return riteType.translationIdentifier(isCorrupted());
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
		renderWrappingText(ctx, translationKey(), guiLeft + 14, guiTop + 76, 126);
		renderRiteIcon(riteType, ctx, isCorrupted(), guiLeft + 67, guiTop + 44);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		renderText(ctx, component, guiLeft + 218 - minecraft.textRenderer.getWidth(component.getString()) / 2, guiTop + 10);
		renderWrappingText(ctx, translationKey(), guiLeft + 156, guiTop + 76, 126);
		renderRiteIcon(riteType, ctx, isCorrupted(), guiLeft + 209, guiTop + 44);
	}

	public boolean isCorrupted() {
		return translationKey.contains("corrupt");
	}
}

package dev.sterner.malum.client.screen.codex.objects;

import dev.sterner.malum.client.screen.codex.BookEntry;
import dev.sterner.malum.client.screen.codex.EntryScreen;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderTexture;
import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderTransparentTexture;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.*;

public class EntryObject extends BookObject{
	public final BookEntry entry;

	public EntryObject(BookEntry entry, int posX, int posY) {
		super(posX, posY, 32, 32);
		this.entry = entry;
	}

	@Override
	public void click(float xOffset, float yOffset, double mouseX, double mouseY) {
		EntryScreen.openScreen(this);
	}

	@Override
	public void render(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int posX = offsetPosX(xOffset);
		int posY = offsetPosY(yOffset);
		renderTransparentTexture(FADE_TEXTURE, ctx, posX - 13, posY - 13, 1, 252, 58, 58, 512, 512);
		renderTexture(FRAME_TEXTURE, ctx, posX, posY, 1, getFrameTextureV(), width, height, 512, 512);
		renderTexture(FRAME_TEXTURE, ctx, posX, posY, 100, getBackgroundTextureV(), width, height, 512, 512);
		ctx.drawItem(entry.iconStack, posX + 8, posY + 8);
	}

	@Override
	public void lateRender(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		if (isHovering) {
			ctx.drawTooltip(minecraft.textRenderer, Arrays.asList(Text.translatable(entry.translationKey()), Text.translatable(entry.descriptionTranslationKey()).formatted(Formatting.GRAY)), mouseX, mouseY);
		}
	}

	public int getFrameTextureV() {
		return entry.isSoulwood ? 285 : 252;
	}

	public int getBackgroundTextureV() {
		return entry.isDark ? 285 : 252;
	}
}

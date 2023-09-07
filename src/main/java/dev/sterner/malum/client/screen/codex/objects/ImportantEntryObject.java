package dev.sterner.malum.client.screen.codex.objects;

import dev.sterner.malum.client.screen.codex.BookEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderTexture;
import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderTransparentTexture;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.*;

public class ImportantEntryObject extends EntryObject{
	public ImportantEntryObject(BookEntry entry, int posX, int posY) {
		super(entry, posX, posY);
	}

	@Override
	public void render(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int posX = offsetPosX(xOffset);
		int posY = offsetPosY(yOffset);
		renderTransparentTexture(FADE_TEXTURE, ctx, posX-13, posY-13, 1, 252, 58, 58, 512, 512);
		renderTexture(FRAME_TEXTURE, ctx, posX, posY, 34, getFrameTextureV(), width, height, 512, 512);
		renderTexture(FRAME_TEXTURE, ctx, posX, posY, 133, getBackgroundTextureV(), width, height, 512, 512);
		ctx.drawItem(entry.iconStack, posX + 8, posY + 8);
	}
}

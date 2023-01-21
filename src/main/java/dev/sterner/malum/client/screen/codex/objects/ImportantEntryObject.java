package dev.sterner.malum.client.screen.codex.objects;

import dev.sterner.malum.client.screen.codex.BookEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.*;

public class ImportantEntryObject extends EntryObject{
	public ImportantEntryObject(BookEntry entry, int posX, int posY) {
		super(entry, posX, posY);
	}

	@Override
	public void render(MinecraftClient minecraft, MatrixStack poseStack, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int posX = offsetPosX(xOffset);
		int posY = offsetPosY(yOffset);
		renderTransparentTexture(FADE_TEXTURE, poseStack, posX-13, posY-13, 1, 252, 58, 58, 512, 512);
		renderTexture(FRAME_TEXTURE, poseStack, posX, posY, 34, getFrameTextureV(), width, height, 512, 512);
		renderTexture(FRAME_TEXTURE, poseStack, posX, posY, 133, getBackgroundTextureV(), width, height, 512, 512);
		minecraft.getItemRenderer().renderInGuiWithOverrides(entry.iconStack, posX + 8, posY + 8);
	}
}

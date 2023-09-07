package dev.sterner.malum.client.screen.codex.objects;

import dev.sterner.malum.client.screen.codex.BookEntry;
import dev.sterner.malum.client.screen.codex.EntryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.*;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.*;

public class IconObject extends EntryObject{
	public final Identifier textureLocation;

	public IconObject(BookEntry entry, Identifier textureLocation, int posX, int posY) {
		super(entry.setDark(), posX, posY);
		this.textureLocation = textureLocation;
	}

	@Override
	public void click(float xOffset, float yOffset, double mouseX, double mouseY) {
		EntryScreen.openScreen(this);
	}

	@Override
	public void render(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int posX = offsetPosX(xOffset);
		int posY = offsetPosY(yOffset);
		renderTransparentTexture(FADE_TEXTURE, ctx, posX-13, posY-13, 1, 252, 58, 58, 512, 512);
		renderTexture(FRAME_TEXTURE, ctx, posX, posY, 67, getFrameTextureV(), width, height, 512, 512);
		renderTexture(FRAME_TEXTURE, ctx, posX, posY, 166, getBackgroundTextureV(), width, height, 512, 512);
		renderWavyIcon(textureLocation, ctx, posX + 8, posY + 8);
	}
}

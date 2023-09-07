package dev.sterner.malum.client.screen.codex.objects;

import dev.sterner.malum.client.screen.codex.BookEntry;
import dev.sterner.malum.client.screen.codex.EntryScreen;
import dev.sterner.malum.client.screen.codex.page.SpiritRiteTextPage;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Optional;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.*;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.*;

public class RiteEntryObject extends EntryObject{
	public final MalumRiteType riteType;

	public RiteEntryObject(BookEntry entry, int posX, int posY) {
		super(entry.setDark(), posX, posY);
		Optional<SpiritRiteTextPage> page = entry.pages.stream().filter(p -> p instanceof SpiritRiteTextPage).map(p -> ((SpiritRiteTextPage) p)).findAny();
		if (page.isPresent()) {
			this.riteType = page.get().riteType;
		} else {
			throw new IllegalArgumentException("Entry " + entry.translationKey() + " lacks a spirit rite page");
		}
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
		renderRiteIcon(riteType, ctx, entry.isSoulwood, posX + 8, posY + 8);
	}
}

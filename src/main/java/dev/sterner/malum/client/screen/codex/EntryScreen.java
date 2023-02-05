package dev.sterner.malum.client.screen.codex;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.objects.EntryObject;
import dev.sterner.malum.client.screen.codex.page.BookPage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.isHovering;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.renderTexture;

public class EntryScreen extends Screen {
	public static final Identifier BOOK_TEXTURE = Malum.id("textures/gui/book/entry.png");

	public static EntryScreen screen;
	public static EntryObject openObject;

	public final int bookWidth = 292;
	public final int bookHeight = 190;

	public int grouping;

	public EntryScreen() {
		super(Text.translatable("malum.gui.entry.title"));
	}


	@Override
	public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
		BookEntry openEntry = openObject.entry;
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		renderTexture(BOOK_TEXTURE, poseStack, guiLeft, guiTop, 1, 1, bookWidth, bookHeight, 512, 512);
		if (!openEntry.pages.isEmpty()) {
			int openPages = grouping * 2;
			for (int i = openPages; i < openPages + 2; i++) {
				if (i < openEntry.pages.size()) {
					BookPage page = openEntry.pages.get(i);
					if (i % 2 == 0) {
						page.renderBackgroundLeft(client, poseStack, ProgressionBookScreen.screen.xOffset, ProgressionBookScreen.screen.yOffset, mouseX, mouseY, partialTicks);
					} else {
						page.renderBackgroundRight(client, poseStack, ProgressionBookScreen.screen.xOffset, ProgressionBookScreen.screen.yOffset, mouseX, mouseY, partialTicks);
					}
				}
			}
		}
		renderTexture(BOOK_TEXTURE, poseStack, guiLeft - 13, guiTop + 150, 1, 193, 28, 18, 512, 512);
		if (isHovering(mouseX, mouseY, guiLeft - 13, guiTop + 150, 28, 18)) {
			renderTexture(BOOK_TEXTURE, poseStack, guiLeft - 13, guiTop + 150, 1, 232, 28, 18, 512, 512);
		} else {
			renderTexture(BOOK_TEXTURE, poseStack, guiLeft - 13, guiTop + 150, 1, 213, 28, 18, 512, 512);
		}
		if (grouping < openEntry.pages.size() / 2f - 1) {
			renderTexture(BOOK_TEXTURE, poseStack, guiLeft + bookWidth - 15, guiTop + 150, 30, 193, 28, 18, 512, 512);
			if (isHovering(mouseX, mouseY, guiLeft + bookWidth - 15, guiTop + 150, 28, 18)) {
				renderTexture(BOOK_TEXTURE, poseStack, guiLeft + bookWidth - 15, guiTop + 150, 30, 232, 28, 18, 512, 512);
			} else {
				renderTexture(BOOK_TEXTURE, poseStack, guiLeft + bookWidth - 15, guiTop + 150, 30, 213, 28, 18, 512, 512);
			}
		}
		if (!openEntry.pages.isEmpty()) {
			int openPages = grouping * 2;
			for (int i = openPages; i < openPages + 2; i++) {
				if (i < openEntry.pages.size()) {
					BookPage page = openEntry.pages.get(i);
					if (i % 2 == 0) {
						page.renderLeft(client, poseStack, ProgressionBookScreen.screen.xOffset, ProgressionBookScreen.screen.yOffset, mouseX, mouseY, partialTicks);
					} else {
						page.renderRight(client, poseStack, ProgressionBookScreen.screen.xOffset, ProgressionBookScreen.screen.yOffset, mouseX, mouseY, partialTicks);
					}
				}
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		if (isHovering(mouseX, mouseY, guiLeft - 13, guiTop + 150, 28, 18)) {
			previousPage(true);
			return true;
		}
		if (isHovering(mouseX, mouseY, guiLeft + bookWidth - 15, guiTop + 150, 28, 18)) {
			nextPage();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		if (false){//TODO ClientConfig.SCROLL_DIRECTION.getConfigValue()) {
			scroll = -scroll;
		}
		if (scroll > 0) {
			nextPage();
		} else {
			previousPage(false);
		}
		return super.mouseScrolled(mouseX, mouseY, scroll);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
			close(false);
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void closeScreen() {
		close(false);
	}

	public void nextPage() {
		if (grouping < openObject.entry.pages.size() / 2f - 1) {
			grouping += 1;
			screen.playSound();
		}
	}

	public void previousPage(boolean ignore) {
		if (grouping > 0) {
			grouping -= 1;
			screen.playSound();
		} else {
			close(ignore);
		}
	}

	public void close(boolean ignoreNextInput) {
		ProgressionBookScreen.openScreen(ignoreNextInput);
		openObject.exit();
	}

	public void playSound() {
		PlayerEntity playerEntity = MinecraftClient.getInstance().player;
		playerEntity.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0f, 1.0f);
	}

	public static void openScreen(EntryObject newObject) {
		MinecraftClient.getInstance().setScreen(getInstance(newObject));
		screen.playSound();
	}

	public static EntryScreen getInstance(EntryObject newObject) {
		if (screen == null || !newObject.equals(openObject)) {
			screen = new EntryScreen();
			openObject = newObject;
		}
		return screen;
	}
}

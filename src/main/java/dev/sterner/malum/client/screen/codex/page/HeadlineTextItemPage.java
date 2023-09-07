package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.*;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.screen;

public class HeadlineTextItemPage extends BookPage{
	private final String headlineTranslationKey;
	private final String translationKey;
	private final ItemStack spiritStack;
	public HeadlineTextItemPage(String headlineTranslationKey, String translationKey, ItemStack spiritStack) {
		super(Malum.id("textures/gui/book/pages/headline_item_page.png"));
		this.headlineTranslationKey = headlineTranslationKey;
		this.translationKey = translationKey;
		this.spiritStack = spiritStack;
	}
	public HeadlineTextItemPage(String headlineTranslationKey, String translationKey, Item spirit)
	{
		this(headlineTranslationKey,translationKey,spirit.getDefaultStack());
	}

	public String headlineTranslationKey()
	{
		return "malum.gui.book.entry.page.headline." + headlineTranslationKey;
	}
	public String translationKey()
	{
		return "malum.gui.book.entry.page.text." + translationKey;
	}
	@Override
	public void renderLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks)
	{
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		renderText(ctx, component, guiLeft+75 - minecraft.textRenderer.getWidth(component.toString())/2, guiTop+10);
		renderWrappingText(ctx, translationKey(), guiLeft+14,guiTop+76,126);
		renderItem(screen,ctx, spiritStack, guiLeft+67, guiTop+44,mouseX,mouseY);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks)
	{
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		renderText(ctx, component, guiLeft+218 - minecraft.textRenderer.getWidth(component.getString())/2,guiTop+10);
		renderWrappingText(ctx, translationKey(), guiLeft+156,guiTop+76,126);
		renderItem(screen, ctx, spiritStack, guiLeft+209, guiTop+44,mouseX,mouseY);
	}
}

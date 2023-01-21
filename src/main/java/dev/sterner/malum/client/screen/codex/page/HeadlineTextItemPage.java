package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
	public void renderLeft(MinecraftClient minecraft, MatrixStack poseStack, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks)
	{
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		ProgressionBookScreen.renderText(poseStack, component, guiLeft+75 - minecraft.textRenderer.getWidth(component.toString())/2, guiTop+10);
		ProgressionBookScreen.renderWrappingText(poseStack, translationKey(), guiLeft+14,guiTop+76,126);
		ProgressionBookScreen.renderItem(poseStack, spiritStack, guiLeft+67, guiTop+44,mouseX,mouseY);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, MatrixStack poseStack, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks)
	{
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		Text component = Text.translatable(headlineTranslationKey());
		ProgressionBookScreen.renderText(poseStack, component, guiLeft+218 - minecraft.textRenderer.getWidth(component.getString())/2,guiTop+10);
		ProgressionBookScreen.renderWrappingText(poseStack, translationKey(), guiLeft+156,guiTop+76,126);
		ProgressionBookScreen.renderItem(poseStack, spiritStack, guiLeft+209, guiTop+44,mouseX,mouseY);
	}
}

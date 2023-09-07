package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.List;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderItem;
import static dev.sterner.malum.client.screen.codex.ProgressionBookScreen.screen;

public class SpiritRiteRecipePage extends BookPage {
	private final MalumRiteType riteType;

	public SpiritRiteRecipePage(MalumRiteType riteType) {
		super(Malum.id("textures/gui/book/pages/spirit_rite_recipe_page.png"));
		this.riteType = riteType;
	}

	@Override
	public void renderLeft(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		renderRite(ctx, guiLeft + 67, guiTop + 123, mouseX, mouseY, riteType.spirits);
	}

	@Override
	public void renderRight(MinecraftClient minecraft, DrawContext ctx, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		renderRite(ctx, guiLeft + 209, guiTop + 123, mouseX, mouseY, riteType.spirits);
	}

	public void renderRite(DrawContext ctx, int left, int top, int mouseX, int mouseY, List<MalumSpiritType> spirits) {
		for (int i = 0; i < spirits.size(); i++) {
			ItemStack stack = spirits.get(i).getSplinterItem().getDefaultStack();
			renderItem(screen, ctx, stack, left, top - 20 * i, mouseX, mouseY);
		}
	}
}

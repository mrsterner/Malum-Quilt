package dev.sterner.malum.client.screen.codex.page;

import dev.sterner.malum.Malum;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class SmeltingBookPage extends BookPage{
	private final ItemStack inputStack;
	private final ItemStack outputStack;

	public SmeltingBookPage(ItemStack inputStack, ItemStack outputStack) {
		super(Malum.id("textures/gui/book/pages/smelting_page.png"));
		this.inputStack = inputStack;
		this.outputStack = outputStack;
	}

	public SmeltingBookPage(Item inputItem, Item outputItem) {
		this(inputItem.getDefaultStack(), outputItem.getDefaultStack());
	}

	public static SmeltingBookPage fromInput(Item input) {
		if (MinecraftClient.getInstance() == null) {
			return new SmeltingBookPage(ItemStack.EMPTY, ItemStack.EMPTY);
		}
		Optional<SmeltingRecipe> optional = MinecraftClient.getInstance().world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(new ItemStack(input, 1)), MinecraftClient.getInstance().world);
		if (optional.isPresent()) {
			SmeltingRecipe recipe = optional.get();
			return new SmeltingBookPage(new ItemStack(input), recipe.getOutput());
		}
		return new SmeltingBookPage(ItemStack.EMPTY, ItemStack.EMPTY);
	}

	@Override
	public boolean isValid() {
		return !inputStack.isEmpty() && !outputStack.isEmpty();
	}

	@Override
	public void renderLeft(MinecraftClient minecraft, MatrixStack poseStack, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		ProgressionBookScreen.renderItem(poseStack, inputStack, guiLeft + 67, guiTop + 59, mouseX, mouseY);
		ProgressionBookScreen.renderItem(poseStack, outputStack, guiLeft + 67, guiTop + 126, mouseX, mouseY);

	}

	@Override
	public void renderRight(MinecraftClient minecraft, MatrixStack poseStack, float xOffset, float yOffset, int mouseX, int mouseY, float partialTicks) {
		int guiLeft = guiLeft();
		int guiTop = guiTop();
		ProgressionBookScreen.renderItem(poseStack, inputStack, guiLeft + 209, guiTop + 59, mouseX, mouseY);
		ProgressionBookScreen.renderItem(poseStack, outputStack, guiLeft + 209, guiTop + 126, mouseX, mouseY);
	}
}

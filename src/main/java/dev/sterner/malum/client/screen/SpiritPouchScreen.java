package dev.sterner.malum.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sterner.malum.common.screen.SpiritPouchScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static dev.sterner.malum.Malum.MODID;

public class SpiritPouchScreen extends HandledScreen<SpiritPouchScreenHandler> {
	public static final Identifier TEXTURE = new Identifier(MODID, "textures/gui/spirit_pouch.png");

	public SpiritPouchScreen(SpiritPouchScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Override
	protected void drawBackground(DrawContext matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		matrices.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}

	@Override
	protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
		if (!this.handler.getCursorStack().isEmpty() || this.focusedSlot == null) {
			return super.handleHotbarKeyPressed(keyCode, scanCode);
		}

		if (!this.client.options.swapHandsKey.matchesKey(keyCode, scanCode)) {
			return super.handleHotbarKeyPressed(keyCode, scanCode);
		}

		if (handler.lockedSlot != -1) {
			return super.handleHotbarKeyPressed(keyCode, scanCode);
		}
		return false;
	}



	@Override
	public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
}

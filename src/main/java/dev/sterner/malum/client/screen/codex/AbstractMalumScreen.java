package dev.sterner.malum.client.screen.codex;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public abstract class AbstractMalumScreen extends Screen {

	protected AbstractMalumScreen(Text pTitle) {
		super(pTitle);
	}

	public abstract boolean isHovering(double mouseX, double mouseY, int posX, int posY, int width, int height);

	public abstract SoundEvent getSweetenerSound();

	public void playPageFlipSound(SoundEvent soundEvent, float pitch) {
		playSound(soundEvent, Math.max(1, pitch * 0.8f));
		playSound(getSweetenerSound(), pitch);
	}

	public void playSweetenedSound(SoundEvent soundEvent, float sweetenerPitch) {
		playSound(soundEvent);
		playSound(getSweetenerSound(), sweetenerPitch);
	}

	public void playSound(SoundEvent soundEvent) {
		playSound(soundEvent, 1);
	}

	public void playSound(SoundEvent soundEvent, float pitch) {
		PlayerEntity playerEntity = MinecraftClient.getInstance().player;
		playerEntity.playSound(soundEvent, SoundCategory.PLAYERS, 1f, pitch);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
			close();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}

package dev.sterner.malum.client.screen.codex.objects;

import dev.sterner.malum.client.screen.codex.BookEntry;
import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;


public class VanishingEntryObject extends EntryObject{
	public VanishingEntryObject(BookEntry entry, int posX, int posY) {
		super(entry, posX, posY);
	}

	@Override
	public void exit() {
		PlayerEntity playerEntity = MinecraftClient.getInstance().player;
		playerEntity.playSound(MalumSoundRegistry.SUSPICIOUS_SOUND, SoundCategory.PLAYERS, 1.0f, 1.0f);
		ProgressionBookScreen.screen.bookObjects.remove(this);
	}
}

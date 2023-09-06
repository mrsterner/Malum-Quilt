package dev.sterner.malum.client.screen.codex;

import dev.sterner.malum.client.screen.codex.objects.EntryObject;
import dev.sterner.malum.client.screen.codex.page.BookPage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BookEntry {
	public final ItemStack iconStack;
	public final String identifier;
	public final int xOffset;
	public final int yOffset;
	public List<BookPage> pages = new ArrayList<>();
	public EntryObjectSupplier objectSupplier = EntryObject::new;

	public boolean isSoulwood;
	public boolean isDark;

	public BookEntry(String identifier, int xOffset, int yOffset) {
		this.iconStack = null;
		this.identifier = identifier;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public BookEntry(String identifier, Item item, int xOffset, int yOffset) {
		this.iconStack = item.getDefaultStack();
		this.identifier = identifier;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public String translationKey() {
		return "malum.gui.book.entry." + identifier;
	}

	public String descriptionTranslationKey() {
		return "malum.gui.book.entry." + identifier + ".description";
	}

	public BookEntry setSoulwood() {
		isSoulwood = true;
		return this;
	}

	public BookEntry setDark() {
		isDark = true;
		return this;
	}

	public BookEntry addPage(BookPage page) {
		if (page.isValid()) {
			pages.add(page);
		}
		return this;
	}

	public BookEntry addModCompatPage(BookPage page, String modId) {
		if (FabricLoader.getInstance().isModLoaded(modId)) {
			pages.add(page);
		}
		return this;
	}

	public BookEntry setObjectSupplier(EntryObjectSupplier objectSupplier)
	{
		this.objectSupplier = objectSupplier;
		return this;
	}
	public interface EntryObjectSupplier {
		EntryObject getBookObject(BookEntry entry, int x, int y);
	}
}

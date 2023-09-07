package dev.sterner.malum.client.screen.codex;

import dev.sterner.malum.client.screen.codex.objects.BookObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

import static dev.sterner.malum.client.screen.codex.ArcanaCodexHelper.renderTexture;

public abstract class AbstractProgressionCodexScreen extends AbstractMalumScreen {
	public float xOffset;
	public float yOffset;
	public float cachedXOffset;
	public float cachedYOffset;
	public boolean ignoreNextMouseInput;

	public final List<BookObject> bookObjects = new ArrayList<>();

	public final int bookWidth;
	public final int bookHeight;
	public final int bookInsideWidth;
	public final int bookInsideHeight;

	public final int backgroundImageWidth;
	public final int backgroundImageHeight;

	protected AbstractProgressionCodexScreen(int backgroundImageWidth, int backgroundImageHeight) {
		this(378, 250, 344, 218, backgroundImageWidth, backgroundImageHeight);
	}

	protected AbstractProgressionCodexScreen(int bookWidth, int bookHeight, int bookInsideWidth, int bookInsideHeight, int backgroundImageWidth, int backgroundImageHeight) {
		super(Text.translatable("malum.gui.book.title"));
		this.bookWidth = bookWidth;
		this.bookHeight = bookHeight;
		this.bookInsideWidth = bookInsideWidth;
		this.bookInsideHeight = bookInsideHeight;
		this.backgroundImageWidth = backgroundImageWidth;
		this.backgroundImageHeight = backgroundImageHeight ;
		client = MinecraftClient.getInstance();
	}

	public void setupObjects() {
		bookObjects.clear();
		this.width = client.getWindow().getScaledWidth();
		this.height = client.getWindow().getScaledHeight();
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		int coreX = guiLeft + bookInsideWidth;
		int coreY = guiTop + bookInsideHeight;
		int width = 40;
		int height = 48;
		for (BookEntry entry : getEntries()) {
			bookObjects.add(entry.objectSupplier.getBookObject(entry, coreX + entry.xOffset * width, coreY - entry.yOffset * height));
		}
		faceObject(bookObjects.get(0));
	}

	public abstract Collection<BookEntry> getEntries();

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		xOffset += dragX;
		yOffset += dragY;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		cachedXOffset = xOffset;
		cachedYOffset = yOffset;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (ignoreNextMouseInput) {
			ignoreNextMouseInput = false;
			return super.mouseReleased(mouseX, mouseY, button);
		}
		if (xOffset != cachedXOffset || yOffset != cachedYOffset) {
			return super.mouseReleased(mouseX, mouseY, button);
		}
		for (BookObject object : bookObjects) {
			if (object.isHovering(this, xOffset, yOffset, mouseX, mouseY)) {
				object.click(xOffset, yOffset, mouseX, mouseY);
				break;
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean isHovering(double mouseX, double mouseY, int posX, int posY, int width, int height) {
		if (!isInView(mouseX, mouseY)) {
			return false;
		}
		return ArcanaCodexHelper.isHovering(mouseX, mouseY, posX, posY, width, height);
	}

	public void faceObject(BookObject object) {
		this.width = client.getWindow().getScaledWidth();
		this.height = client.getWindow().getScaledHeight();
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		xOffset = -object.posX + guiLeft + bookInsideWidth;
		yOffset = -object.posY + guiTop + bookInsideHeight;
	}

	public void renderEntries(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
		for (int i = bookObjects.size() - 1; i >= 0; i--) {
			BookObject object = bookObjects.get(i);
			boolean isHovering = object.isHovering(this, xOffset, yOffset, mouseX, mouseY);
			object.isHovering = isHovering;
			object.hover = isHovering ? Math.min(object.hover+1, object.hoverCap()) : Math.max(object.hover-1, 0);
			object.render(client, ctx, xOffset, yOffset, mouseX, mouseY, partialTicks);
		}
	}

	public void lateEntryRender(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
		for (int i = bookObjects.size() - 1; i >= 0; i--) {
			BookObject object = bookObjects.get(i);
			object.lateRender(client, ctx, xOffset, yOffset, mouseX, mouseY, partialTicks);
		}
	}

	public void renderBackground(Identifier texture, DrawContext ctx, float xModifier, float yModifier) {
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		int insideLeft = guiLeft + 17;
		int insideTop = guiTop + 14;
		float uOffset = (backgroundImageWidth - xOffset) * xModifier;
		float vOffset = Math.min(backgroundImageHeight - bookInsideHeight, (backgroundImageHeight - bookInsideHeight - yOffset * yModifier));
		if (vOffset <= backgroundImageHeight / 2f) {
			vOffset = backgroundImageHeight / 2f;
		}
		if (uOffset <= 0) {
			uOffset = 0;
		}
		if (uOffset > (bookInsideWidth - 8) / 2f) {
			uOffset = (bookInsideWidth - 8) / 2f;
		}
		renderTexture(texture, ctx, insideLeft, insideTop, uOffset, vOffset, bookInsideWidth, bookInsideHeight, backgroundImageWidth / 2, backgroundImageHeight / 2);
	}

	public boolean isInView(double mouseX, double mouseY) {
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		return !(mouseX < guiLeft + 17) && !(mouseY < guiTop + 14) && !(mouseX > guiLeft + (bookWidth - 17)) && !(mouseY > (guiTop + bookHeight - 14));
	}

	public void cut() {
		int scale = (int) client.getWindow().getScaleFactor();
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		int insideLeft = guiLeft + 17;
		int insideTop = guiTop + 18;
		GL11.glScissor(insideLeft * scale, insideTop * scale, bookInsideWidth * scale, (bookInsideHeight + 1) * scale); // do not ask why the 1 is needed please
	}
}

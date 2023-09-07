package dev.sterner.malum.common.spirit;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.malum.Malum;
import dev.sterner.malum.api.event.SpiritTypeAddEvent;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MalumSpiritType {
	public Map<Supplier<MalumSpiritItem>, String> SPIRITS = new LinkedHashMap<>();

	public final Color color;

	public final Color endColor;
	public final String identifier;

	public Supplier<MalumSpiritItem> splinterItem;

	public MalumSpiritType(String identifier, Color color) {
		this(identifier, color, createEndColor(color));
	}

	public MalumSpiritType(String identifier, Color color, Color endColor) {
		SPIRITS.put(() -> MalumObjects.WICKED_SPIRIT, "wicked");
		SPIRITS.put(() -> MalumObjects.ARCANE_SPIRIT, "arcane");
		SPIRITS.put(() -> MalumObjects.ELDRITCH_SPIRIT, "eldritch");
		SPIRITS.put(() -> MalumObjects.AERIAL_SPIRIT, "aerial");
		SPIRITS.put(() -> MalumObjects.AQUEOUS_SPIRIT, "aqueous");
		SPIRITS.put(() -> MalumObjects.INFERNAL_SPIRIT, "infernal");
		SPIRITS.put(() -> MalumObjects.EARTHEN_SPIRIT, "earthen");
		SPIRITS.put(() -> MalumObjects.SACRED_SPIRIT, "sacred");
		SpiritTypeAddEvent.EVENT.invoker().addSpiritType(SPIRITS);

		this.identifier = identifier;
		this.color = color;
		this.endColor = endColor;
		for(Map.Entry<Supplier<MalumSpiritItem>, String> entry : SPIRITS.entrySet().stream().toList()){
			String id = entry.getValue();
			if(id.equals(identifier)){
				this.splinterItem = () -> entry.getKey().get();
			}
		}
	}

	public Color getColor() {
		return color;
	}

	public Color getEndColor() {
		return endColor;
	}

	public Text getCountComponent(int count) {
		return Text.literal(" " + count + " ").append(Text.translatable(getDescription())).setStyle(Style.EMPTY.withColor(color.getRGB()));
	}

	public Text getFlavourComponent(ItemStack stack) {
		return Text.translatable(getFlavourText()).formatted(Formatting.ITALIC).setStyle(Style.EMPTY.withColor(ColorHelper.darker(color, 1, 0.75f).getRGB()));
	}

	public String getDescription() {
		return "malum.spirit.description." + identifier;
	}

	public String getFlavourText() {
		return "malum.spirit.flavour." + identifier;
	}

	public static Color createEndColor(Color color) {
		return new Color(color.getGreen(), color.getBlue(), color.getRed());
	}

	public MalumSpiritItem getSplinterItem() {
		return splinterItem.get();
	}

	public Identifier getOverlayTexture() {
		return Malum.id("spirit/" + identifier + "_glow");
	}

	public BlockState getBlockState(boolean isCorrupt, BlockHitResult hit) {
		Block base = isCorrupt ? MalumObjects.SOULWOOD_TOTEM_POLE : MalumObjects.RUNEWOOD_TOTEM_POLE;
		return base.getDefaultState().with(Properties.HORIZONTAL_FACING, hit.getSide()).with(MalumSpiritTypeRegistry.SPIRIT_TYPE_PROPERTY, identifier);
	}
}

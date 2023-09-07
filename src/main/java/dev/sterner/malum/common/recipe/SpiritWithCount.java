package dev.sterner.malum.common.recipe;

import com.google.gson.JsonObject;
import dev.sterner.lodestone.systems.recipe.IRecipeComponent;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SpiritWithCount implements IRecipeComponent {
	public final MalumSpiritType type;
	public final int count;

	public SpiritWithCount(ItemStack stack) {
		this.type = ((MalumSpiritItem)stack.getItem()).type;
		this.count = stack.getCount();
	}


	public SpiritWithCount(MalumSpiritType type, int count) {
		this.type = type;
		this.count = count;
	}

	public static SpiritWithCount fromJson(JsonObject inputObject) {
		MalumSpiritType type = SpiritHelper.getSpiritType(inputObject.get("type").getAsString());
		int count = 1;
		if (inputObject.has("count")) {
			count = inputObject.get("count").getAsInt();
		}
		return new SpiritWithCount(type, count);
	}

	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("type", type.identifier);
		if (getCount() > 1) {
			object.addProperty("count", getCount());
		}
		return object;
	}
	public Text getComponent() {
		return type.getCountComponent(count);
	}

	public NbtCompound writeNbt(NbtCompound tag) {
		tag.putString("type", type.identifier);
		tag.putInt("count", count);
		return tag;
	}

	public static SpiritWithCount readNbt(NbtCompound tag) {
		MalumSpiritType type = SpiritHelper.getSpiritType(tag.getString("type"));
		int count = tag.getInt("count");
		return new SpiritWithCount(type, count);
	}

	@Override
	public ItemStack getStack() {
		return new ItemStack(getItem(), getCount());
	}

	@Override
	public List<ItemStack> getStacks() {
		return new ArrayList<>(List.of(getStack()));
	}

	@Override
	public Item getItem() {
		return type.getSplinterItem();
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public boolean matches(ItemStack stack) {
		return stack.getItem().equals(getItem()) && stack.getCount() >= getCount();
	}
}

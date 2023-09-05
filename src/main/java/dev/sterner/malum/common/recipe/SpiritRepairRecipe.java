package dev.sterner.malum.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sammy.lodestone.systems.recipe.ILodestoneRecipe;
import com.sammy.lodestone.systems.recipe.IngredientWithCount;
import dev.sterner.malum.common.registry.MalumRecipeSerializerRegistry;
import dev.sterner.malum.common.registry.MalumRecipeTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SpiritRepairRecipe extends ILodestoneRecipe {
	public static final String NAME = "spirit_repair";

	private final Identifier id;

	public final float durabilityPercentage;
	public final List<Item> inputs;
	public final IngredientWithCount repairMaterial;
	public final List<SpiritWithCount> spirits;

	public SpiritRepairRecipe(Identifier id, float durabilityPercentage, List<Item> inputs, IngredientWithCount repairMaterial, List<SpiritWithCount> spirits) {
		this.id = id;
		this.durabilityPercentage = durabilityPercentage;
		this.repairMaterial = repairMaterial;
		this.inputs = inputs;
		this.spirits = spirits;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MalumRecipeSerializerRegistry.SPIRIT_REPAIR_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return MalumRecipeTypeRegistry.SPIRIT_REPAIR;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	public List<ItemStack> getSortedSpirits(List<ItemStack> stacks) {
		List<ItemStack> sortedStacks = new ArrayList<>();
		for (SpiritWithCount item : spirits) {
			for (ItemStack stack : stacks) {
				if (item.matches(stack)) {
					sortedStacks.add(stack);
					break;
				}
			}
		}
		return sortedStacks;
	}

	public boolean doSpiritsMatch(List<ItemStack> spirits) {
		if (this.spirits.size() == 0) {
			return true;
		}
		if (this.spirits.size() != spirits.size()) {
			return false;
		}
		List<ItemStack> sortedStacks = getSortedSpirits(spirits);
		if (sortedStacks.size() < this.spirits.size()) {
			return false;
		}
		for (int i = 0; i < this.spirits.size(); i++) {
			SpiritWithCount item = this.spirits.get(i);
			ItemStack stack = sortedStacks.get(i);
			if (!item.matches(stack)) {
				return false;
			}
		}
		return true;
	}

	public boolean doesInputMatch(ItemStack input) {
		return this.inputs.stream().anyMatch(i -> i.equals(input.getItem()));
	}

	public boolean doesRepairMatch(ItemStack input) {
		return this.repairMaterial.matches(input);
	}

	public static SpiritRepairRecipe getRecipe(World world, ItemStack stack, ItemStack repairStack, List<ItemStack> spirits) {
		if (stack.isDamageable() && !stack.isDamaged()) {
			return null;
		}
		return getRecipe(world, c -> c.doesInputMatch(stack) && c.doesRepairMatch(repairStack) && c.doSpiritsMatch(spirits));
	}

	public static SpiritRepairRecipe getRecipe(World world, Predicate<SpiritRepairRecipe> predicate) {
		List<SpiritRepairRecipe> recipes = getRecipes(world);
		for (SpiritRepairRecipe recipe : recipes) {
			if (predicate.test(recipe)) {
				return recipe;
			}
		}
		return null;
	}

	public static List<SpiritRepairRecipe> getRecipes(World world) {
		return world.getRecipeManager().listAllOfType(MalumRecipeTypeRegistry.SPIRIT_REPAIR);
	}

	public static ItemStack getRepairRecipeOutput(ItemStack input) {
		return input.getItem() instanceof IRepairOutputOverride ? new ItemStack(((IRepairOutputOverride) input.getItem()).overrideRepairResult(), input.getCount(), Optional.ofNullable(input.getNbt())) : input;
	}

	public interface IRepairOutputOverride {
		default Item overrideRepairResult() {
			return Items.AIR;
		}

		default boolean ignoreDuringLookup() {
			return false;
		}
	}

	public static class Serializer implements RecipeSerializer<SpiritRepairRecipe>{

		public static List<Item> REPAIRABLE;

		@Override
		public SpiritRepairRecipe read(Identifier recipeId, JsonObject json) {
			if (REPAIRABLE == null) {
				REPAIRABLE = Registries.ITEM.getEntrySet().stream().map(Map.Entry::getValue).filter(Item::isDamageable).collect(Collectors.toList());
			}
			float durabilityPercentage = json.getAsJsonPrimitive("durabilityPercentage").getAsFloat();
			String itemIdRegex = json.get("itemIdRegex").getAsString();
			String modIdRegex = json.get("modIdRegex").getAsString();
			JsonArray inputsArray = json.getAsJsonArray("inputs");
			List<Item> inputs = new ArrayList<>();
			for (JsonElement jsonElement : inputsArray) {
				Item input = Registries.ITEM.get(new Identifier(jsonElement.getAsString()));
				if (input == null) {
					continue;
				}
				inputs.add(input);
			}
			for (Item item : REPAIRABLE) {
				if (Registries.ITEM.getId(item).getPath().matches(itemIdRegex)) {
					if (!modIdRegex.equals("") && !Registries.ITEM.getId(item).getNamespace().matches(modIdRegex)) {
						continue;
					}
					if (item instanceof IRepairOutputOverride repairOutputOverride && repairOutputOverride.ignoreDuringLookup()) {
						continue;
					}
					if (!inputs.contains(item)) {
						inputs.add(item);
					}
				}
			}

			JsonObject repairObject = json.getAsJsonObject("repairMaterial");
			IngredientWithCount repair = IngredientWithCount.fromJson(repairObject);

			JsonArray spiritsArray = json.getAsJsonArray("spirits");
			List<SpiritWithCount> spirits = new ArrayList<>();
			for (int i = 0; i < spiritsArray.size(); i++) {
				JsonObject spiritObject = spiritsArray.get(i).getAsJsonObject();
				spirits.add(SpiritWithCount.fromJson(spiritObject));
			}
			return new SpiritRepairRecipe(recipeId, durabilityPercentage, inputs, repair, spirits);
		}

		@Nullable
		@Override
		public SpiritRepairRecipe read(Identifier recipeId, PacketByteBuf buffer) {
			float durabilityPercentage = buffer.readFloat();
			int inputCount = buffer.readInt();
			List<Item> inputs = new ArrayList<>();
			for (int i = 0; i < inputCount; i++) {
				inputs.add(buffer.readItemStack().getItem());
			}
			IngredientWithCount repair = IngredientWithCount.read(buffer);
			int spiritCount = buffer.readInt();
			List<SpiritWithCount> spirits = new ArrayList<>();
			for (int i = 0; i < spiritCount; i++) {
				spirits.add(new SpiritWithCount(buffer.readItemStack()));
			}
			return new SpiritRepairRecipe(recipeId, durabilityPercentage, inputs, repair, spirits);
		}

		@Override
		public void write(PacketByteBuf buffer, SpiritRepairRecipe recipe) {
			buffer.writeFloat(recipe.durabilityPercentage);
			buffer.writeInt(recipe.inputs.size());
			for (Item item : recipe.inputs) {
				buffer.writeItemStack(item.getDefaultStack());
			}
			recipe.repairMaterial.write(buffer);
			buffer.writeInt(recipe.spirits.size());
			for (SpiritWithCount item : recipe.spirits) {
				buffer.writeItemStack(item.getStack());
			}
		}
	}
}

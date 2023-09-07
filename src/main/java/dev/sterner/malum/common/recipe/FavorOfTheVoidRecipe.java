package dev.sterner.malum.common.recipe;

import com.google.gson.JsonObject;
import dev.sterner.lodestone.forge.CraftingHelper;
import dev.sterner.lodestone.systems.recipe.ILodestoneRecipe;
import dev.sterner.lodestone.systems.recipe.IngredientWithCount;
import dev.sterner.malum.common.registry.MalumRecipeSerializerRegistry;
import dev.sterner.malum.common.registry.MalumRecipeTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FavorOfTheVoidRecipe extends ILodestoneRecipe {
	public static final String NAME = "favor_of_the_void";

	private final Identifier id;

	public final IngredientWithCount input;

	public final ItemStack output;

	public FavorOfTheVoidRecipe(Identifier id, IngredientWithCount input, ItemStack output) {
		this.id = id;
		this.input = input;
		this.output = output;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MalumRecipeSerializerRegistry.VOID_FAVOR_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return MalumRecipeTypeRegistry.VOID_FAVOR;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	public boolean doesInputMatch(ItemStack input) {
		return this.input.matches(input);
	}

	public boolean doesOutputMatch(ItemStack output) {
		return output.getItem().equals(this.output.getItem());
	}

	public static FavorOfTheVoidRecipe getRecipe(World level, ItemStack stack) {
		return getRecipe(level, c -> c.doesInputMatch(stack));
	}

	public static FavorOfTheVoidRecipe getRecipe(World level, Predicate<FavorOfTheVoidRecipe> predicate) {
		List<FavorOfTheVoidRecipe> recipes = getRecipes(level);
		for (FavorOfTheVoidRecipe recipe : recipes) {
			if (predicate.test(recipe)) {
				return recipe;
			}
		}
		return null;
	}

	public static List<FavorOfTheVoidRecipe> getRecipes(World level) {
		return level.getRecipeManager().listAllOfType(MalumRecipeTypeRegistry.VOID_FAVOR);
	}

	public static class Serializer implements RecipeSerializer<FavorOfTheVoidRecipe> {
		@Override
		public FavorOfTheVoidRecipe read(Identifier recipeId, JsonObject json) {
			JsonObject inputObject = json.getAsJsonObject("input");
			IngredientWithCount input = IngredientWithCount.fromJson(inputObject);

			JsonObject outputObject = json.getAsJsonObject("output");
			ItemStack output = CraftingHelper.getItemStack(outputObject, true);
			return new FavorOfTheVoidRecipe(recipeId, input, output);
		}

		@Nullable
		@Override
		public FavorOfTheVoidRecipe read(Identifier recipeId, PacketByteBuf buffer) {
			IngredientWithCount input = IngredientWithCount.read(buffer);
			ItemStack output = buffer.readItemStack();
			return new FavorOfTheVoidRecipe(recipeId, input, output);
		}

		@Override
		public void write(PacketByteBuf buffer, FavorOfTheVoidRecipe recipe) {
			recipe.input.write(buffer);
			buffer.writeItemStack(recipe.output);
		}
	}
}

package dev.sterner.malum.common.recipe;

import com.google.gson.JsonObject;
import dev.sterner.lodestone.forge.CraftingHelper;
import dev.sterner.lodestone.systems.recipe.ILodestoneRecipe;
import dev.sterner.malum.common.registry.MalumRecipeSerializerRegistry;
import dev.sterner.malum.common.registry.MalumRecipeTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class SpiritTransmutationRecipe extends ILodestoneRecipe {
    public static final String NAME = "spirit_transmutation";

    private final Identifier id;

    public final Ingredient ingredient;
    public final ItemStack output;

    @Nullable
    public final String group;

    public SpiritTransmutationRecipe(Identifier id, Ingredient ingredient, ItemStack output, @Nullable String group) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
        this.group = group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MalumRecipeSerializerRegistry.SPIRIT_TRANSMUTATION_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MalumRecipeTypeRegistry.SPIRIT_TRANSMUTATION;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public static SpiritTransmutationRecipe getRecipe(World world, Item item) {
        return getRecipe(world, item.getDefaultStack());
    }

    public static SpiritTransmutationRecipe getRecipe(World world, ItemStack item) {
        return getRecipe(world, r -> r.ingredient.test(item));
    }

    public static SpiritTransmutationRecipe getRecipe(World world, Predicate<SpiritTransmutationRecipe> predicate) {
        List<SpiritTransmutationRecipe> recipes = getRecipes(world);
        for (SpiritTransmutationRecipe recipe : recipes) {
            if (predicate.test(recipe)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<SpiritTransmutationRecipe> getRecipes(World world) {
        return world.getRecipeManager().listAllOfType(MalumRecipeTypeRegistry.SPIRIT_TRANSMUTATION);
    }

    public static class Serializer implements RecipeSerializer<SpiritTransmutationRecipe> {

        @Override
        public SpiritTransmutationRecipe read(Identifier recipeId, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.getAsJsonObject("input"));
            ItemStack output = CraftingHelper.getItemStack(json.getAsJsonObject("output"), true);
            String group = json.has("group") ? json.get("group").getAsString() : null;
            return new SpiritTransmutationRecipe(recipeId, input, output, group);
        }

        @Nullable
        @Override
        public SpiritTransmutationRecipe read(Identifier recipeId, PacketByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromPacket(buffer);
            ItemStack output = buffer.readItemStack();
            String group = buffer.readBoolean() ? buffer.readString() : null;
            return new SpiritTransmutationRecipe(recipeId, ingredient, output, group);
        }

        @Override
        public void write(PacketByteBuf buffer, SpiritTransmutationRecipe recipe) {
            recipe.ingredient.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeBoolean(recipe.group != null);
            if (recipe.group != null)
                buffer.writeString(recipe.group);
        }
    }
}

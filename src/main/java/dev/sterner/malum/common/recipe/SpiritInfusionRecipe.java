package dev.sterner.malum.common.recipe;

import com.google.gson.JsonArray;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class SpiritInfusionRecipe extends ILodestoneRecipe {
    public static final String NAME = "spirit_infusion";

    private final Identifier id;

    public final IngredientWithCount input;

    public final ItemStack output;

    public final List<SpiritWithCount> spirits;
    public final List<IngredientWithCount> extraItems;

    public SpiritInfusionRecipe(Identifier id, IngredientWithCount input, ItemStack output, List<SpiritWithCount> spirits, List<IngredientWithCount> extraItems) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.spirits = spirits;
        this.extraItems = extraItems;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MalumRecipeSerializerRegistry.SPIRIT_INFUSION_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MalumRecipeTypeRegistry.SPIRIT_INFUSION;
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
        return this.input.matches(input);
    }

    public boolean doesOutputMatch(ItemStack output) {
        return output.getItem().equals(this.output.getItem());
    }

    public static SpiritInfusionRecipe getRecipe(World world, ItemStack stack, List<ItemStack> spirits) {
        return getRecipe(world, c -> c.doesInputMatch(stack) && c.doSpiritsMatch(spirits));
    }

    public static SpiritInfusionRecipe getRecipe(World world, Predicate<SpiritInfusionRecipe> predicate) {
        List<SpiritInfusionRecipe> recipes = getRecipes(world);
        for (SpiritInfusionRecipe recipe : recipes) {
            if (predicate.test(recipe)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<SpiritInfusionRecipe> getRecipes(World world) {
        return world.getRecipeManager().listAllOfType(MalumRecipeTypeRegistry.SPIRIT_INFUSION);
    }

    public static class Serializer implements RecipeSerializer<SpiritInfusionRecipe> {
        @Override
        public SpiritInfusionRecipe read(Identifier recipeId, JsonObject json) {
            JsonObject inputObject = json.getAsJsonObject("input");
            IngredientWithCount input = IngredientWithCount.fromJson(inputObject);

            JsonObject outputObject = json.getAsJsonObject("output");
            ItemStack output = CraftingHelper.getItemStack(outputObject, true);
            JsonArray extraItemsArray = json.getAsJsonArray("extra_items");
            List<IngredientWithCount> extraItems = new ArrayList<>();
            for (int i = 0; i < extraItemsArray.size(); i++) {
                JsonObject extraItemObject = extraItemsArray.get(i).getAsJsonObject();
                extraItems.add(IngredientWithCount.fromJson(extraItemObject));
            }

            JsonArray spiritsArray = json.getAsJsonArray("spirits");
            List<SpiritWithCount> spirits = new ArrayList<>();
            for (int i = 0; i < spiritsArray.size(); i++) {
                JsonObject spiritObject = spiritsArray.get(i).getAsJsonObject();
                spirits.add(SpiritWithCount.fromJson(spiritObject));
            }
            if (spirits.isEmpty()) {
                return null;
            }
            return new SpiritInfusionRecipe(recipeId, input, output, spirits, extraItems);
        }

        @Nullable
        @Override
        public SpiritInfusionRecipe read(Identifier recipeId, PacketByteBuf buffer) {
            IngredientWithCount input = IngredientWithCount.read(buffer);
            ItemStack output = buffer.readItemStack();
            int extraItemCount = buffer.readInt();
            List<IngredientWithCount> extraItems = new ArrayList<>();
            for (int i = 0; i < extraItemCount; i++) {
                extraItems.add(IngredientWithCount.read(buffer));
            }
            int spiritCount = buffer.readInt();
            List<SpiritWithCount> spirits = new ArrayList<>();
            for (int i = 0; i < spiritCount; i++) {
                spirits.add(new SpiritWithCount(buffer.readItemStack()));
            }
            return new SpiritInfusionRecipe(recipeId, input, output, spirits, extraItems);
        }

        @Override
        public void write(PacketByteBuf buffer, SpiritInfusionRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeInt(recipe.extraItems.size());
            for (IngredientWithCount item : recipe.extraItems) {
                item.write(buffer);
            }
            buffer.writeInt(recipe.spirits.size());
            for (SpiritWithCount item : recipe.spirits) {
                buffer.writeItemStack(item.getStack());
            }
        }
    }
}

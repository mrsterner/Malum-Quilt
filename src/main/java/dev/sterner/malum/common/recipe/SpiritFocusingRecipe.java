package dev.sterner.malum.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.sterner.lodestone.forge.CraftingHelper;
import dev.sterner.lodestone.systems.recipe.ILodestoneRecipe;
import dev.sterner.malum.common.registry.MalumRecipeSerializerRegistry;
import dev.sterner.malum.common.registry.MalumRecipeTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpiritFocusingRecipe extends ILodestoneRecipe {
    public static final String NAME = "spirit_focusing";

    public final Identifier id;

    public final int time;
    public final int durabilityCost;

    public final Ingredient input;
    public final ItemStack output;
    public final List<SpiritWithCount> spirits;

    public SpiritFocusingRecipe(Identifier id, int time, int durabilityCost, Ingredient input, ItemStack output, List<SpiritWithCount> spirits) {
        this.id = id;
        this.time = time;
        this.durabilityCost = durabilityCost;
        this.input = input;
        this.output = output;
        this.spirits = spirits;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MalumRecipeSerializerRegistry.SPIRIT_FOCUSING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MalumRecipeTypeRegistry.SPIRIT_FOCUSING;
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
        return this.input.test(input);
    }

    public boolean doesOutputMatch(ItemStack output) {
        return output.getItem().equals(this.output.getItem());
    }

    public static SpiritFocusingRecipe getRecipe(World world, ItemStack stack, List<ItemStack> spirits) {
        return getRecipe(world, c -> c.doesInputMatch(stack) && c.doSpiritsMatch(spirits));
    }

    public static SpiritFocusingRecipe getRecipe(World world, Predicate<SpiritFocusingRecipe> predicate) {
        List<SpiritFocusingRecipe> recipes = getRecipes(world);
        for (SpiritFocusingRecipe recipe : recipes) {
            if (predicate.test(recipe)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<SpiritFocusingRecipe> getRecipes(World world) {
        return world.getRecipeManager().listAllOfType(MalumRecipeTypeRegistry.SPIRIT_FOCUSING);
    }

    public static class Serializer implements RecipeSerializer<SpiritFocusingRecipe> {

        @Override
        public SpiritFocusingRecipe read(Identifier recipeId, JsonObject json) {
            int time = json.getAsJsonPrimitive("time").getAsInt();
            int durabilityCost = json.getAsJsonPrimitive("durabilityCost").getAsInt();

            JsonObject inputObject = json.getAsJsonObject("input");
            Ingredient input = Ingredient.fromJson(inputObject);

            JsonObject outputObject = json.getAsJsonObject("output");
            ItemStack output = CraftingHelper.getItemStack(outputObject, true);

            JsonArray spiritsArray = json.getAsJsonArray("spirits");
            List<SpiritWithCount> spirits = new ArrayList<>();
            for (int i = 0; i < spiritsArray.size(); i++) {
                JsonObject spiritObject = spiritsArray.get(i).getAsJsonObject();
                spirits.add(SpiritWithCount.fromJson(spiritObject));
            }
            if (spirits.isEmpty()) {
                return null;
            }
            return new SpiritFocusingRecipe(recipeId, time, durabilityCost, input, output, spirits);
        }

        @Nullable
        @Override
        public SpiritFocusingRecipe read(Identifier recipeId, PacketByteBuf buffer) {
            int time = buffer.readInt();
            int durabilityCost = buffer.readInt();
            Ingredient input = Ingredient.fromPacket(buffer);
            ItemStack output = buffer.readItemStack();
            int spiritCount = buffer.readInt();
            List<SpiritWithCount> spirits = new ArrayList<>();
            for (int i = 0; i < spiritCount; i++) {
                spirits.add(new SpiritWithCount(buffer.readItemStack()));
            }
            return new SpiritFocusingRecipe(recipeId, time, durabilityCost, input, output, spirits);
        }

        @Override
        public void write(PacketByteBuf buffer, SpiritFocusingRecipe recipe) {
            buffer.writeInt(recipe.time);
            buffer.writeInt(recipe.durabilityCost);
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeInt(recipe.spirits.size());
            for (SpiritWithCount item : recipe.spirits) {
                buffer.writeItemStack(item.getStack());
            }
        }
    }
}

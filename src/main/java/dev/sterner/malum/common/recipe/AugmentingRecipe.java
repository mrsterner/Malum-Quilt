package dev.sterner.malum.common.recipe;

import com.google.gson.JsonObject;
import com.sammy.lodestone.forge.CraftingHelper;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import com.sammy.lodestone.systems.recipe.ILodestoneRecipe;
import dev.sterner.malum.common.blockentity.spirit_altar.IAltarProvider;
import dev.sterner.malum.common.network.packet.s2c.block.blight.BlightTransformItemParticlePacket;
import dev.sterner.malum.common.registry.MalumRecipeSerializerRegistry;
import dev.sterner.malum.common.registry.MalumRecipeTypeRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.ARCANE_SPIRIT;

public class AugmentingRecipe extends ILodestoneRecipe {
    public static final String NAME = "augmenting";

    private final Identifier id;

    public final Ingredient targetItem;
    public final Ingredient augment;
    public final NbtCompound tagAugment;

    public AugmentingRecipe(Identifier id, Ingredient targetItem, Ingredient augment, NbtCompound tagAugment) {
        this.id = id;
        this.targetItem = targetItem;
        this.augment = augment;
        this.tagAugment = tagAugment;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MalumRecipeSerializerRegistry.AUGMENTING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MalumRecipeTypeRegistry.AUGMENTING;
    }

    public boolean doesInputMatch(ItemStack input) {
        return this.targetItem.test(input);
    }

    public boolean doesAugmentMatch(ItemStack input) {
        return this.augment.test(input);
    }

    public static AugmentingRecipe getRecipe(World world, ItemStack stack, ItemStack augment) {
        return getRecipe(world, c -> c.doesInputMatch(stack) && c.doesAugmentMatch(augment));
    }

    public static AugmentingRecipe getRecipe(World world, Predicate<AugmentingRecipe> predicate) {
        List<AugmentingRecipe> recipes = getRecipes(world);
        for (AugmentingRecipe recipe : recipes) {
            if (predicate.test(recipe)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<AugmentingRecipe> getRecipes(World world) {
        return world.getRecipeManager().listAllOfType(MalumRecipeTypeRegistry.AUGMENTING);
    }

	public static ActionResult performAugmentation(IAltarProvider altarProvider, PlayerEntity player, Hand hand) {
		LodestoneBlockEntityInventory inventory = altarProvider.getInventoryForAltar();
		World level = player.getWorld();
		ItemStack target = inventory.getStack(0);
		if (!target.isEmpty()) {
			ItemStack applied = player.getStackInHand(hand);
			AugmentingRecipe recipe = AugmentingRecipe.getRecipe(level, target, applied);
			if (recipe != null) {
				if (!level.isClient) {
					var random = level.random;
					NbtCompound tag = target.getOrCreateNbt();
					NbtCompound modifiedTag = tag.copy().copyFrom(recipe.tagAugment);
					if (tag.equals(modifiedTag)) {
						return ActionResult.FAIL;
					}
					target.setNbt(modifiedTag);
					Vec3d pos = altarProvider.getItemPosForAltar();
					ItemEntity pEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), target);
					pEntity.setVelocity(MathHelper.nextFloat(random, -0.1F, 0.1F), MathHelper.nextFloat(random, 0.25f, 0.5f), MathHelper.nextFloat(random, -0.1F, 0.1F));
					if(level instanceof ServerWorld serverWorld){
						PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(altarProvider.getBlockPosForAltar()).getPos()).forEach(track ->
								BlightTransformItemParticlePacket.send(track, List.of(ARCANE_SPIRIT.identifier), pos.getX(), pos.getY(), pos.getZ()));
					}
					level.playSound(null, altarProvider.getBlockPosForAltar(), MalumSoundRegistry.ALTERATION_PLINTH_ALTERS, SoundCategory.BLOCKS, 1, 0.9f + level.random.nextFloat() * 0.25f);

					level.spawnEntity(pEntity);
					inventory.setStackInSlot(0, ItemStack.EMPTY);
					if (!player.isCreative()) {
						applied.decrement(1);
					}
				}
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}

    public static class Serializer implements RecipeSerializer<AugmentingRecipe> {

        @Override
        public AugmentingRecipe read(Identifier recipeId, JsonObject json) {
            Ingredient targetItem = Ingredient.fromJson(json.get("targetItem"));
            Ingredient input = Ingredient.fromJson(json.get("augment"));

            NbtCompound tagAugment = CraftingHelper.getNBT(json.get("tagAugment"));
            return new AugmentingRecipe(recipeId, targetItem, input, tagAugment);
        }

        @Nullable
        @Override
        public AugmentingRecipe read(Identifier recipeId, PacketByteBuf buffer) {
            Ingredient targetItem = Ingredient.fromPacket(buffer);
            Ingredient input = Ingredient.fromPacket(buffer);
            NbtCompound tagAugment = buffer.readNbt();
            return new AugmentingRecipe(recipeId, targetItem, input, tagAugment);
        }

        @Override
        public void write(PacketByteBuf buffer, AugmentingRecipe recipe) {
            recipe.targetItem.write(buffer);
            recipe.augment.write(buffer);
            buffer.writeNbt(recipe.tagAugment);
        }
    }
}

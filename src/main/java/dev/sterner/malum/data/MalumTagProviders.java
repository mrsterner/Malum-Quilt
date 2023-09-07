package dev.sterner.malum.data;

import dev.sterner.malum.common.item.NodeItem;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumTagRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static dev.sterner.malum.common.registry.MalumObjects.*;
import static dev.sterner.malum.common.registry.MalumTagRegistry.*;
import static net.minecraft.registry.tag.BlockTags.*;

public class MalumTagProviders {

	public static class MalumBlockTags extends FabricTagProvider.BlockTagProvider {
		public MalumBlockTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}


		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {


		}

		@NotNull
		private Block[] getModBlocks(Predicate<Block> predicate) {
			List<Block> ret = new ArrayList<>(Collections.emptyList());
			BLOCKS.keySet().stream().filter(predicate).forEach(ret::add);
			return ret.toArray(new Block[0]);
		}
	}

	public static class MalumItemTags extends FabricTagProvider.ItemTagProvider {
		public MalumItemTags(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(dataOutput, completableFuture, new MalumTagProviders.MalumBlockTags(dataOutput, completableFuture));
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {

		}
	}

	public static class MalumEntityTypeTags extends FabricTagProvider.EntityTypeTagProvider {
		public MalumEntityTypeTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			getOrCreateTagBuilder(SURVIVES_REJECTION).add(EntityType.PLAYER);
		}
	}
}

package dev.sterner.malum.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;

public class MalumModelProvider extends FabricModelProvider {
	public MalumModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {

	}
}

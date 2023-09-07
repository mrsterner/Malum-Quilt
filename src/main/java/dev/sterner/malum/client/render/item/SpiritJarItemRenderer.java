package dev.sterner.malum.client.render.item;

import dev.sterner.malum.common.blockentity.storage.SpiritJarBlockEntity;
import dev.sterner.malum.common.item.spirit.SpiritJarItem;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class SpiritJarItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
	private final SpiritJarBlockEntity jar = new SpiritJarBlockEntity(BlockPos.ORIGIN, MalumObjects.SPIRIT_JAR.getDefaultState());

	public SpiritJarItemRenderer() {
		super();
	}

	@Override
	public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (stack.getItem() instanceof SpiritJarItem) {
			if (stack.hasNbt() && stack.getNbt().contains("spirit")) {
				MalumSpiritType spirit = SpiritHelper.getSpiritType(stack.getNbt().getString("spirit"));
				int count = stack.getNbt().getInt("count");
				jar.type = spirit;
				jar.count = count;
				MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(jar, matrices, vertexConsumers, light, overlay);
			}
		}
	}
}

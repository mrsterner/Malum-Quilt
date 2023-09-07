package dev.sterner.malum.client.render.block;

import dev.sterner.malum.common.blockentity.storage.SpiritJarBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class SpiritJarBlockEntityRenderer implements BlockEntityRenderer<SpiritJarBlockEntity> {
	public SpiritJarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
	}


	@Override
	public void render(SpiritJarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = MinecraftClient.getInstance().world;
		ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		if (entity.type != null && entity.type.getSplinterItem() != null) {
			matrices.push();
			double y = 0.5f + Math.sin((world.getTime() + tickDelta) / 20f) * 0.2f;
			matrices.translate(0.5f, y, 0.5f);
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((world.getTime() % 360) + tickDelta) * 3));
			matrices.scale(0.6f, 0.6f, 0.6f);
			itemRenderer.renderItem(entity.type.getSplinterItem().getDefaultStack(), ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 0);
			matrices.pop();
		}
	}
}

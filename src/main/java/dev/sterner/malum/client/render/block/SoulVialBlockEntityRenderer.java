package dev.sterner.malum.client.render.block;

import dev.sterner.malum.common.blockentity.storage.SoulVialBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class SoulVialBlockEntityRenderer implements BlockEntityRenderer<SoulVialBlockEntity> {
	public SoulVialBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
	}

	@Override
	public void render(SoulVialBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (entity.data != null) {
			matrices.push();
			double y = 0.5f + Math.sin(((entity.getWorld().getTime() % 360) + tickDelta) / 20f) * 0.08f;
			matrices.translate(0.5f, y, 0.5f);
			matrices.scale(0.75f, 0.75f, 0.75f);
			//SoulEntityRenderer.renderSoul(matrices, entity.data.primaryType.getColor().darker());
			matrices.pop();
		}
	}
}

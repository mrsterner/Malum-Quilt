package dev.sterner.malum.client.render.block;

import dev.sterner.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import dev.sterner.malum.common.blockentity.spirit_altar.SpiritAltarBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class SpiritAltarBlockEntityRenderer implements BlockEntityRenderer<SpiritAltarBlockEntity> {
	public SpiritAltarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
	}

	@Override
	public void render(SpiritAltarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = MinecraftClient.getInstance().world;
		ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		LodestoneBlockEntityInventory inventory = entity.spiritInventory;
		if (world == null) {
			return;
		}
		int spiritsRendered = 0;
		for (int i = 0; i < inventory.slotCount; i++) {
			ItemStack item = inventory.getStack(i);
			if (!item.isEmpty()) {
				matrices.push();
				Vector3f offset = new Vector3f(entity.getSpiritOffset(spiritsRendered++, tickDelta).toVector3f());
				matrices.translate(offset.x(), offset.y(), offset.z());
				matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((world.getTime() % 360) + tickDelta) * 3));
				matrices.scale(0.5f, 0.5f, 0.5f);
				itemRenderer.renderItem(item, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 0);
				matrices.pop();
			}
		}
		ItemStack stack = entity.inventory.getStack(0);
		if (!stack.isEmpty()) {
			matrices.push();
			Vec3d offset = entity.itemOffset();
			matrices.translate(offset.x, offset.y, offset.z);
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((world.getTime() % 360) + tickDelta) * 3));
			matrices.scale(0.45f, 0.45f, 0.45f);
			itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 0);
			matrices.pop();
		}
	}
}

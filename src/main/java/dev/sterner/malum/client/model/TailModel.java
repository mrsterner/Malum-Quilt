package dev.sterner.malum.client.model;

import dev.sterner.malum.Malum;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class TailModel extends EntityModel<PlayerEntity> {
	public static EntityModelLayer LAYER = new EntityModelLayer(Malum.id("tail"), "main");
	private final ModelPart tail;

	public TailModel(ModelPart root) {
		this.tail = root.getChild("tail");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData tail = modelPartData.addChild("tail", ModelPartBuilder.create().uv(0, 13).cuboid(-2.0F, -3.5F, -3.0F, 4.0F, 4.0F, 3.0F, new Dilation(0.0F)).uv(14, 13).cuboid(-2.0F, -3.5F, -3.0F, 4.0F, 4.0F, 3.0F, new Dilation(0.4F)), ModelTransform.of(0.0F, 12.0F, 2.75F, -0.7854F, 0.0F, 0.0F));
		ModelPartData tail2 = tail.addChild("tail2", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -7.5F, -2.5F, 6.0F, 8.0F, 5.0F, new Dilation(0.0F)).uv(22, 0).cuboid(-3.0F, -7.5F, -2.5F, 6.0F, 8.0F, 5.0F, new Dilation(0.4F)), ModelTransform.of(0.0F, -4.0F, -1.5F, 0.2182F, 0.0F, 0.0F));
		ModelPartData tailtip = tail2.addChild("tailtip", ModelPartBuilder.create().uv(0, 20).cuboid(-2.5F, -2.0F, -2.0F, 5.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(18, 20).cuboid(-2.5F, -2.0F, -2.0F, 5.0F, 3.0F, 4.0F, new Dilation(0.25F)), ModelTransform.of(0.0F, -7.5F, 0.0F, -0.3491F, 0.0F, 0.0F));
		ModelPartData tailtip1 = tailtip.addChild("tailtip1", ModelPartBuilder.create().uv(0, 28).cuboid(-0.3856F, -6.5303F, -2.561F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, -0.1298F, -0.7769F, 0.1841F));
		ModelPartData tailtip2 = tailtip.addChild("tailtip2", ModelPartBuilder.create().uv(0, 22).cuboid(0.3856F, -6.0303F, -2.561F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, -0.1298F, 0.7769F, -0.1841F));
		return TexturedModelData.of(modelData, 64, 64);
	}



	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		tail.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(PlayerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}

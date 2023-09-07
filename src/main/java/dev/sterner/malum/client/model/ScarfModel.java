package dev.sterner.malum.client.model;

import dev.sterner.malum.Malum;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class ScarfModel extends EntityModel<PlayerEntity> {
	public static EntityModelLayer LAYER = new EntityModelLayer(Malum.id("scarf"), "main");
	private final ModelPart helmet;
	private final ModelPart torso;

	public ScarfModel(ModelPart root) {
		this.helmet = root.getChild("helmet");
		this.torso = root.getChild("torso");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData helmet = modelPartData.addChild("helmet", ModelPartBuilder.create().uv(0, 14).cuboid(-4.5F, -2.25F, -4.5F, 9.0F, 3.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData torso = modelPartData.addChild("torso", ModelPartBuilder.create().uv(0, 0).cuboid(-5.5F, -25.0F, -2.5F, 11.0F, 9.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}



	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		helmet.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(PlayerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}

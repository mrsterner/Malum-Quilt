package dev.sterner.malum.client.model;

import com.google.common.collect.ImmutableList;
import dev.sterner.malum.Malum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class SoulHunterArmorModel extends ArmorModel {
    public static final EntityModelLayer LAYER = new EntityModelLayer(Malum.id("soul_hunter_armor"), "main");

	public ModelPart cape;
	public ModelPart lowered_hood;

	public SoulHunterArmorModel(ModelPart root) {
		super(root);
		this.cape = root.getChild("cape");
		this.lowered_hood = root.getChild("lowered_hood");
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		if (this.slot == EquipmentSlot.CHEST) {
			return ImmutableList.of(this.body, this.leftArm, this.rightArm, this.cape, this.lowered_hood);
		} else if (this.slot == EquipmentSlot.LEGS) {
			return ImmutableList.of(this.leftLegging, this.rightLegging, this.leggings);
		} else {
			return this.slot == EquipmentSlot.FEET ? ImmutableList.of(this.leftFoot, this.rightFoot) : ImmutableList.of();
		}
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.cape.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.lowered_hood.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		super.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(new Dilation(0), 0);
		ModelPartData root = createHumanoidAlias(modelData);
		ModelPartData cape = root.addChild("cape", new ModelPartBuilder(), ModelTransform.NONE);
		ModelPartData lowered_hood = root.addChild("lowered_hood", new ModelPartBuilder(), ModelTransform.NONE);

		ModelPartData body = root.getChild("body");
		ModelPartData torso = body.addChild("torso", ModelPartBuilder.create().uv(0, 18).cuboid(-4.5F, -0.5F, -2.5F, 9.0F, 10.0F, 5.0F, new Dilation(0.0F))
				.uv(28, 18).cuboid(-4.5F, -0.5F, -2.5F, 9.0F, 10.0F, 5.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData left_stripe = torso.addChild("left_stripe", ModelPartBuilder.create().uv(0, 33).cuboid(-1.9074F, 0.0F, -0.6014F, 3.0F, 10.0F, 1.0F, new Dilation(0.01F)), ModelTransform.of(-2.5F, -0.5F, -2.5F, -0.0892F, 0.3487F, -0.0061F));
		ModelPartData right_stripe = torso.addChild("right_stripe", ModelPartBuilder.create().uv(0, 33).mirrored().cuboid(-1.0926F, 0.0F, -0.6014F, 3.0F, 10.0F, 1.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(2.5F, -0.5F, -2.5F, -0.0892F, -0.3487F, 0.0061F));

		ModelPartData leggings = root.getChild("leggings");
		ModelPartData codpiece = leggings.addChild("codpiece", ModelPartBuilder.create().uv(0, 84).cuboid(-5.0F, -14.5F, -3.5F, 10.0F, 3.0F, 7.0F, new Dilation(0.01F))
				.uv(34, 84).cuboid(-5.0F, -14.5F, -3.5F, 10.0F, 3.0F, 7.0F, new Dilation(0.26F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData right_legging = root.getChild("right_legging");
		ModelPartData right_leg = right_legging.addChild("right_leg", ModelPartBuilder.create().uv(39, 103).cuboid(-2.5F, -0.5F, -2.5F, 5.0F, 8.0F, 5.0F, new Dilation(0.01F)), ModelTransform.pivot(0, 0, 0.0F));
		ModelPartData right_leg_robe = right_legging.addChild("right_leg_robe", ModelPartBuilder.create().uv(0, 118).cuboid(-1.0F, 3.0F, -5.0F, 1.0F, 3.0F, 7.0F, new Dilation(0.25F))
				.uv(0, 108).cuboid(-1.0F, 3.0F, -5.0F, 1.0F, 3.0F, 7.0F, new Dilation(0.0F))
				.uv(22, 94).cuboid(0.0F, -1.0F, -5.0F, 4.0F, 7.0F, 7.0F, new Dilation(0.25F))
				.uv(0, 94).cuboid(0.0F, -1.0F, -5.0F, 4.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, -0.5F, 1.5F, 0.0F, 0.0F, 0.1745F));

		ModelPartData right_foot = root.getChild("right_foot");
		ModelPartData right_boot = right_foot.addChild("right_boot", ModelPartBuilder.create().uv(16, 116).cuboid(-3.0F, 7.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F))
				.uv(40, 116).cuboid(-3.0F, 7.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_arm = root.getChild("right_arm");
		ModelPartData right_shoulder = right_arm.addChild("right_shoulder", ModelPartBuilder.create().uv(0, 44).mirrored().cuboid(-4.5F, 5.5F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.01F)).mirrored(false)
				.uv(20, 44).mirrored().cuboid(-4.5F, 5.5F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.26F)).mirrored(false)
				.uv(40, 46).mirrored().cuboid(-5.5F, 4.5F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)).mirrored(false)
				.uv(50, 46).mirrored().cuboid(-5.5F, 4.5F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.25F)).mirrored(false)
				.uv(8, 33).mirrored().cuboid(-4.5F, -2.5F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.01F)).mirrored(false)
				.uv(28, 33).mirrored().cuboid(-4.5F, -2.5F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.26F)).mirrored(false), ModelTransform.pivot(1, 0, 0.0F));

		ModelPartData left_legging = root.getChild("left_legging");
		ModelPartData left_leg = left_legging.addChild("left_leg", ModelPartBuilder.create().uv(39, 103).mirrored().cuboid(-2.5F, -0.5F, -2.5F, 5.0F, 8.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0, 0, 0.0F));
		ModelPartData left_leg_robe = left_legging.addChild("left_leg_robe", ModelPartBuilder.create().uv(0, 118).mirrored().cuboid(0.0F, 3.0F, -5.0F, 1.0F, 3.0F, 7.0F, new Dilation(0.25F)).mirrored(false)
				.uv(0, 108).mirrored().cuboid(0.0F, 3.0F, -5.0F, 1.0F, 3.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
				.uv(22, 94).mirrored().cuboid(-4.0F, -1.0F, -5.0F, 4.0F, 7.0F, 7.0F, new Dilation(0.25F)).mirrored(false)
				.uv(0, 94).mirrored().cuboid(-4.0F, -1.0F, -5.0F, 4.0F, 7.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.0F, -0.5F, 1.5F, 0.0F, 0.0F, -0.1745F));

		ModelPartData left_foot = root.getChild("left_foot");
		ModelPartData left_boot = left_foot.addChild("left_boot", ModelPartBuilder.create().uv(16, 116).mirrored().cuboid(-3.0F, 7.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.01F)).mirrored(false)
				.uv(40, 116).mirrored().cuboid(-3.0F, 7.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.25F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_arm = root.getChild("left_arm");
		ModelPartData left_shoulder = left_arm.addChild("left_shoulder", ModelPartBuilder.create().uv(8, 33).cuboid(-0.5F, -2.5F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.01F))
				.uv(28, 33).cuboid(-0.5F, -2.5F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.26F))
				.uv(0, 44).cuboid(-0.5F, 5.5F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.01F))
				.uv(20, 44).cuboid(-0.5F, 5.5F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.26F))
				.uv(40, 46).cuboid(3.5F, 4.5F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F))
				.uv(50, 46).cuboid(3.5F, 4.5F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.25F)), ModelTransform.pivot(-1, 0, 0.0F));

		ModelPartData head = root.getChild("head");
		ModelPartData helmet = head.addChild("helmet", ModelPartBuilder.create().uv(0, 0).cuboid(-4.5F, -8.5F, -4.5F, 9.0F, 9.0F, 9.0F, new Dilation(0.0F))
				.uv(36, 0).cuboid(-4.5F, -8.5F, -4.5F, 9.0F, 9.0F, 9.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData left_hood_bit = head.addChild("left_hood_bit", ModelPartBuilder.create().uv(53, 59).mirrored().cuboid(-2.0F, -5.0F, -4.0F, 2.0F, 5.0F, 7.0F, new Dilation(0.25F)).mirrored(false)
				.uv(53, 47).mirrored().cuboid(-2.0F, -5.0F, -4.0F, 2.0F, 5.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-4.5F, 0.5F, 0.5F, 0.0F, 0.0F, 0.3927F));
		ModelPartData back_hood_bit = head.addChild("back_hood_bit", ModelPartBuilder.create().uv(48, 33).cuboid(-3.5F, 0.0F, 0.0F, 7.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.5F, 4.5F, -0.7854F, 0.0F, 0.0F));
		ModelPartData right_hood_bit = head.addChild("right_hood_bit", ModelPartBuilder.create().uv(53, 59).cuboid(0.0F, -5.0F, -4.0F, 2.0F, 5.0F, 7.0F, new Dilation(0.25F))
				.uv(53, 47).cuboid(0.0F, -5.0F, -4.0F, 2.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(4.5F, 0.5F, 0.5F, 0.0F, 0.0F, -0.3927F));

		ModelPartData cape_top = cape.addChild("cape_top", ModelPartBuilder.create().uv(0, 54).cuboid(-6.5213F, -0.1479F, -1.0812F, 11.0F, 8.0F, 1.0F, new Dilation(0.0F))
				.uv(24, 54).cuboid(-6.5213F, -0.1479F, -1.0812F, 11.0F, 8.0F, 1.0F, new Dilation(0.25F)), ModelTransform.of(1.0F, -0.5F, 3.5F, 0.0839F, -0.02F, 0.0126F));

		ModelPartData cape_middle = cape_top.addChild("cape_middle", ModelPartBuilder.create().uv(24, 62).cuboid(-6.5213F, -0.2878F, -1.0526F, 11.0F, 7.0F, 1.0F, new Dilation(0.25F))
				.uv(0, 62).cuboid(-6.5213F, -0.2878F, -1.0526F, 11.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 8.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		ModelPartData cape_bottom = cape_middle.addChild("cape_bottom", ModelPartBuilder.create().uv(24, 77).cuboid(-6.5213F, -0.9301F, 2.1414F, 3.0F, 4.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 77).cuboid(-3.5213F, -0.9301F, 2.1414F, 5.0F, 6.0F, 1.0F, new Dilation(0.0F))
				.uv(24, 77).mirrored().cuboid(1.4787F, -0.9301F, 2.1414F, 3.0F, 4.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
				.uv(32, 77).mirrored().cuboid(1.4787F, -0.9301F, 2.1414F, 3.0F, 4.0F, 1.0F, new Dilation(0.25F)).mirrored(false)
				.uv(12, 77).cuboid(-3.5213F, -0.9301F, 2.1414F, 5.0F, 6.0F, 1.0F, new Dilation(0.25F))
				.uv(32, 77).cuboid(-6.5213F, -0.9301F, 2.1414F, 3.0F, 4.0F, 1.0F, new Dilation(0.25F)), ModelTransform.of(0.0F, 8.0F, -3.0F, 0.1745F, 0.0F, 0.0F));

		ModelPartData hood = lowered_hood.addChild("hood", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData overhang = hood.addChild("overhang", ModelPartBuilder.create().uv(26, 70).cuboid(-4.98F, -0.5028F, -2.1358F, 8.0F, 2.0F, 5.0F, new Dilation(0.25F))
				.uv(0, 70).cuboid(-4.98F, -0.5028F, -2.1358F, 8.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -0.5F, 3.5F, -0.6109F, 0.0F, 0.0F));


		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void copyFromDefault(BipedEntityModel model) {
		super.copyFromDefault(model);
		cape.copyTransform(model.body);
		lowered_hood.copyTransform(model.body);
	}

	@Override
	public void setAngles(LivingEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		float pPartialTicks = MinecraftClient.getInstance().getTickDelta();
		lowered_hood.visible = pEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty();
		if (pEntity instanceof AbstractClientPlayerEntity clientPlayer) {
			double d0 = MathHelper.lerp(pPartialTicks, clientPlayer.prevCapeX, clientPlayer.capeX) - MathHelper.lerp(pPartialTicks, pEntity.prevX, pEntity.getX());
			double d1 = MathHelper.lerp(pPartialTicks, clientPlayer.prevCapeY, clientPlayer.capeY) - MathHelper.lerp(pPartialTicks, pEntity.prevY, pEntity.getY());
			double d2 = MathHelper.lerp(pPartialTicks, clientPlayer.prevCapeZ, clientPlayer.capeZ) - MathHelper.lerp(pPartialTicks, pEntity.prevZ, pEntity.getZ());
			float f = pEntity.prevBodyYaw + (pEntity.bodyYaw - pEntity.prevBodyYaw);
			double d3 = MathHelper.sin(f * ((float) Math.PI / 180F));
			double d4 = (-MathHelper.cos(f * ((float) Math.PI / 180F)));
			float f1 = (float) d1 * 10.0F;
			f1 = MathHelper.clamp(f1, -6.0F, 16.0F);
			float f2 = (float) (d0 * d3 + d2 * d4) * 65.0F;
			f2 = MathHelper.clamp(f2, 0.0F, 75.0F);
			float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
			f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
			if (f2 < 0.0F) {
				f2 = 0.0F;
			}
			float f4 = MathHelper.lerp(pPartialTicks, clientPlayer.prevStrideDistance, clientPlayer.strideDistance);
			f1 += MathHelper.sin(MathHelper.lerp(pPartialTicks, pEntity.prevHorizontalSpeed, pEntity.horizontalSpeed) * 6.0F) * 32.0F * f4;
			if (pEntity.isSneaking()) {
				f1 += 25.0F;
			}
			float x = (float) Math.toRadians(6.0F + f2 / 2.0F + f1);
			float y = (float) Math.toRadians(f3 / 2.0F);
			float z = (float) Math.toRadians(f3 / 2.0F);
			cape.pitch = x;
			cape.yaw = y;
			cape.roll = z;
			lowered_hood.pitch = x/3f;
			lowered_hood.yaw = y/3f;
			lowered_hood.roll = z/3f;
		}
		else {
			cape.pitch = 0;
			cape.yaw = 0;
			cape.roll = 0;
			lowered_hood.pitch = 0;
			lowered_hood.yaw = 0;
			lowered_hood.roll = 0;
		}
		super.setAngles(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
	}
}

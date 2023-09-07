package dev.sterner.malum.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class ArmorModel extends BipedEntityModel<LivingEntity> {
	public EquipmentSlot slot;
	public ModelPart root, head, body, leftArm, rightArm, leggings, leftLegging, rightLegging, leftFoot, rightFoot;


	public ArmorModel(ModelPart root) {
		super(root);
		this.root = root;
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.leggings = root.getChild("leggings");
		this.leftArm = root.getChild("left_arm");
		this.rightArm = root.getChild("right_arm");
		this.leftLegging = root.getChild("left_legging");
		this.rightLegging = root.getChild("right_legging");
		this.leftFoot = root.getChild("left_foot");
		this.rightFoot = root.getChild("right_foot");
	}

	public static ModelPartData createHumanoidAlias(ModelData mesh) {
		ModelPartData root = mesh.getRoot();
		root.addChild("body", new ModelPartBuilder(), ModelTransform.NONE);
		root.addChild("leggings", new ModelPartBuilder(), ModelTransform.NONE);
		root.addChild("head", new ModelPartBuilder(), ModelTransform.NONE);
		root.addChild("left_legging", new ModelPartBuilder(), ModelTransform.NONE);
		root.addChild("left_foot", new ModelPartBuilder(), ModelTransform.NONE);
		root.addChild("right_legging", new ModelPartBuilder(), ModelTransform.NONE);
		root.addChild("right_foot", new ModelPartBuilder(), ModelTransform.NONE);
		root.addChild("left_arm", new ModelPartBuilder(), ModelTransform.NONE);
		root.addChild("right_arm", new ModelPartBuilder(), ModelTransform.NONE);
		return root;
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return slot == EquipmentSlot.HEAD ? ImmutableList.of(head) : ImmutableList.of();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		if (slot == EquipmentSlot.CHEST) {
			return ImmutableList.of(body, leftArm, rightArm);
		} else if (slot == EquipmentSlot.LEGS) {
			return ImmutableList.of(leftLegging, rightLegging, leggings);
		} else if (slot == EquipmentSlot.FEET) {
			return ImmutableList.of(leftFoot, rightFoot);
		} else return ImmutableList.of();
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (slot == EquipmentSlot.LEGS) {  //I don't know why this is needed, but it is.
			this.leggings.copyTransform(this.body);
			this.leftLegging.copyTransform(this.leftLeg);
			this.rightLegging.copyTransform(this.rightLeg);
		}
		this.root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		super.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void copyFromDefault(BipedEntityModel model) {
		leggings.copyTransform(model.body);
		body.copyTransform(model.body);
		head.copyTransform(model.head);
		leftArm.copyTransform(model.leftArm);
		rightArm.copyTransform(model.rightArm);
		leftLegging.copyTransform(leftLeg);
		rightLegging.copyTransform(rightLeg);
		leftFoot.copyTransform(leftLeg);
		rightFoot.copyTransform(rightLeg);
	}
}

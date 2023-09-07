package dev.sterner.malum.client.render;

import dev.sterner.malum.client.model.SoulStainedSteelArmorModel;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;


public class SteelArmorRenderer implements ArmorRenderer {
	private static SoulStainedSteelArmorModel armorModel;
	private final Identifier texture;

	public SteelArmorRenderer(Identifier texture) {
		this.texture = texture;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
		final MinecraftClient client = MinecraftClient.getInstance();
		if (armorModel == null) {
			armorModel = new SoulStainedSteelArmorModel(client.getEntityModelLoader().getModelPart(SoulStainedSteelArmorModel.LAYER));
		}

		contextModel.copyBipedStateTo(armorModel);
		armorModel.copyFromDefault(contextModel);
		armorModel.setVisible(false);

		armorModel.head.visible = slot == EquipmentSlot.HEAD;
		armorModel.body.visible = slot == EquipmentSlot.CHEST;
		armorModel.leggings.visible = slot == EquipmentSlot.LEGS;
		armorModel.leftArm.visible = slot == EquipmentSlot.CHEST;
		armorModel.rightArm.visible = slot == EquipmentSlot.CHEST;
		armorModel.leftLegging.visible = slot == EquipmentSlot.LEGS;
		armorModel.rightLegging.visible = slot == EquipmentSlot.LEGS;
		armorModel.leftFoot.visible = slot == EquipmentSlot.FEET;
		armorModel.rightFoot.visible = slot == EquipmentSlot.FEET;

		ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel, texture);
	}
}

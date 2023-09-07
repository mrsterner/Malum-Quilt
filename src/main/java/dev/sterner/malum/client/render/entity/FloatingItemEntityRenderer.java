package dev.sterner.malum.client.render.entity;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.lodestone.helpers.EntityHelper;
import dev.sterner.lodestone.setup.LodestoneRenderLayerRegistry;
import dev.sterner.lodestone.systems.easing.Easing;
import dev.sterner.lodestone.systems.rendering.VFXBuilders;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.entity.FloatingItemEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.sterner.lodestone.handlers.RenderHandler.DELAYED_RENDER;
import static dev.sterner.lodestone.systems.client.ClientTickCounter.partialTicks;

public class FloatingItemEntityRenderer extends EntityRenderer<FloatingItemEntity> {
	public final ItemRenderer itemRenderer;

	private static final Identifier LIGHT_TRAIL = Malum.id("textures/vfx/light_trail.png");
	private static final RenderLayer LIGHT_TYPE = LodestoneRenderLayerRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(LIGHT_TRAIL);

	private static final Identifier MESSY_TRAIL = Malum.id("textures/vfx/messy_trail.png");
	private static final RenderLayer MESSY_TYPE = LodestoneRenderLayerRegistry.SCROLLING_TEXTURE_TRIANGLE.apply(MESSY_TRAIL);

	public FloatingItemEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
		this.itemRenderer = ctx.getItemRenderer();
		this.shadowRadius = 0;
		this.shadowOpacity = 0;
	}

	@Override
	public void render(FloatingItemEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		List<EntityHelper.PastPosition> positions = new ArrayList<>(entity.pastPositions);
		if (positions.size() > 1) {
			for (int i = 0; i < positions.size() - 2; i++) {
				EntityHelper.PastPosition position = positions.get(i);
				EntityHelper.PastPosition nextPosition = positions.get(i + 1);
				float x = (float) MathHelper.lerp(tickDelta, position.position.x, nextPosition.position.x);
				float y = (float) MathHelper.lerp(tickDelta, position.position.y, nextPosition.position.y);
				float z = (float) MathHelper.lerp(tickDelta, position.position.z, nextPosition.position.z);
				positions.set(i, new EntityHelper.PastPosition(new Vec3d(x, y, z), position.time));
			}
		}
		float x = (float) MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
		float y = (float) MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
		float z = (float) MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
		if (positions.size() > 1) {
			positions.set(positions.size() - 1, new EntityHelper.PastPosition(new Vec3d(x, y + entity.getYOffset(partialTicks) + 0.25F, z).add(entity.getVelocity().multiply(partialTicks, partialTicks, partialTicks)), 0));
		}

		List<Vector4f> mappedPastPositions = positions.stream().map(p -> p.position).map(p -> new Vector4f((float) p.x, (float) p.y, (float) p.z, 1)).collect(Collectors.toList());
		VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();
		Vector3f offset = new Vector3f(-x, -y, -z);

		VertexConsumer lightBuffer = DELAYED_RENDER.getBuffer(LIGHT_TYPE);
		for (int i = 0; i < 3; i++) {
			float size = 0.225f + i * 0.15f;
			float alpha = (0.3f - i * 0.12f);
			builder
				.setAlpha(alpha)
				.renderTrail(lightBuffer, matrices, offset, mappedPastPositions, f -> size, f -> builder.setAlpha(alpha * f).setColor(ColorHelper.colorLerp(Easing.SINE_IN, f * 3f, entity.endColor, entity.color)))
				.renderTrail(lightBuffer, matrices, offset, mappedPastPositions, f -> 1.5f * size, f -> builder.setAlpha(alpha * f / 2f).setColor(ColorHelper.colorLerp(Easing.SINE_IN, f * 2f, entity.endColor, entity.color)))
				.renderTrail(lightBuffer, matrices, offset, mappedPastPositions, f -> size * 2.5f, f -> builder.setAlpha(alpha * f / 4f).setColor(ColorHelper.colorLerp(Easing.SINE_IN, f * 2f, entity.endColor, entity.color)));
		}
		matrices.pop();
		matrices.push();
		renderSpirit(entity, itemRenderer, partialTicks, matrices, vertexConsumers, light);
		matrices.pop();
		super.render(entity, yaw, partialTicks, matrices, vertexConsumers, light);
	}

	public static void renderSpirit(FloatingItemEntity entity, ItemRenderer itemRenderer, float tickDelta, MatrixStack matrices, VertexConsumerProvider bufferIn, int packedLightIn) {
		ItemStack itemStack = entity.getItem();
		BakedModel model = itemRenderer.getModel(itemStack, entity.getWorld(), null, entity.getItem().getCount());
		VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat().setColor(entity.color);
		float yOffset = entity.getYOffset(tickDelta);
		float scale = model.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
		float rotation = entity.getRotation(tickDelta);
		matrices.push();
		matrices.translate(0.0D, (yOffset + 0.25F * scale), 0.0D);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation));
		itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrices, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, model);
		matrices.pop();
		matrices.push();
		matrices.translate(0.0D, (yOffset + 0.5F * scale), 0.0D);
		renderSpiritGlimmer(matrices, builder, partialTicks);
		matrices.pop();
	}

	public static void renderSpiritGlimmer(MatrixStack matrices, VFXBuilders.WorldVFXBuilder builder, float tickDelta) {
		ClientWorld world = MinecraftClient.getInstance().world;
		float v = world.getTime() + tickDelta;
		float time = (float) ((Math.sin(v) + v % 15f) / 15f);
		if (time >= 0.5f) {
			time = 1f - time;
		}
		float multiplier = 1 + Easing.BOUNCE_IN_OUT.ease(time*2f, 0, 0.25f, 1);
		matrices.push();
		matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
		for (int i = 0; i < 3; i++) {
			float size = (0.125f + i * 0.13f) * multiplier;
			float alpha = (0.75f - i * 0.3f);
			builder.setAlpha(alpha * 0.6f).renderQuad(DELAYED_RENDER.getBuffer(LodestoneRenderLayerRegistry.ADDITIVE_TEXTURE.applyAndCache(Malum.id("textures/particle/wisp.png"))), matrices, size * 0.75f);
			builder.setAlpha(alpha).renderQuad(DELAYED_RENDER.getBuffer(LodestoneRenderLayerRegistry.ADDITIVE_TEXTURE.applyAndCache(Malum.id("textures/particle/twinkle.png"))), matrices, size);
		}
		matrices.pop();
	}


	@Override
	public Identifier getTexture(FloatingItemEntity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

package dev.sterner.malum.client.render.entity;

import dev.sterner.lodestone.helpers.ColorHelper;
import dev.sterner.lodestone.helpers.EntityHelper;
import dev.sterner.lodestone.setup.LodestoneRenderLayerRegistry;
import dev.sterner.lodestone.systems.easing.Easing;
import dev.sterner.lodestone.systems.rendering.VFXBuilders;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.entity.nitrate.EthericNitrateEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.sterner.lodestone.handlers.RenderHandler.DELAYED_RENDER;

public class EthericNitrateEntityRenderer extends EntityRenderer<EthericNitrateEntity> {
	public final ItemRenderer itemRenderer;

	private static final Identifier LIGHT_TRAIL = Malum.id("textures/vfx/light_trail.png");
	private static final RenderLayer LIGHT_TYPE = LodestoneRenderLayerRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(LIGHT_TRAIL);

	public EthericNitrateEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
		this.itemRenderer = ctx.getItemRenderer();
		this.shadowRadius = 0;
		this.shadowOpacity = 0;
	}

	@Override
	public void render(EthericNitrateEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
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
			positions.set(positions.size() - 1, new EntityHelper.PastPosition(new Vec3d(x, y + entity.getYOffset(tickDelta) + 0.25F, z).add(entity.getVelocity().multiply(tickDelta, tickDelta, tickDelta)), 0));
		}

		List<Vector4f> mappedPastPositions = positions.stream().map(p -> p.position).map(p -> new Vector4f((float) p.x, (float) p.y, (float) p.z, 1)).collect(Collectors.toList());
		VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat().setOffset(-x, -y, -z);
		Vector3f offset = new Vector3f(-x, -y, -z);

		VertexConsumer lightBuffer = DELAYED_RENDER.getBuffer(LIGHT_TYPE);
		float trailVisibility = Math.min(entity.windUp, 1);
		Color firstColor = EthericNitrateEntity.FIRST_COLOR;
		Color secondColor = EthericNitrateEntity.SECOND_COLOR;
		for (int i = 0; i < 3; i++) {
			float size = 0.25f + i * 0.1f;
			float alpha = (0.16f - i * 0.04f) * trailVisibility;
			builder
					.setAlpha(alpha)
					.renderTrail(lightBuffer, matrices, offset, mappedPastPositions, f -> size, f -> builder.setAlpha(alpha * f).setColor(ColorHelper.colorLerp(Easing.SINE_IN, f * 3f, secondColor, firstColor)))
					.renderTrail(lightBuffer, matrices, offset, mappedPastPositions, f -> 1.5f * size, f -> builder.setAlpha(alpha * f * 1.5f).setColor(ColorHelper.colorLerp(Easing.SINE_IN, f * 2f, secondColor, firstColor)))
					.renderTrail(lightBuffer, matrices, offset, mappedPastPositions, f -> size * 2.5f, f -> builder.setAlpha(alpha * f / 4f).setColor(ColorHelper.colorLerp(Easing.SINE_IN, f * 2f, secondColor, firstColor)));
		}
		matrices.translate(0, entity.getYOffset(tickDelta) + 0.25F, 0);
		matrices.scale(1.2f*trailVisibility,1.2f*trailVisibility,1.2f*trailVisibility);
		builder.setColor(firstColor);
		builder.setAlpha(trailVisibility);
		FloatingItemEntityRenderer.renderSpiritGlimmer(matrices, builder, tickDelta);

		matrices.pop();


		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(EthericNitrateEntity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

package dev.sterner.malum.client.render.entity;

import dev.sterner.lodestone.helpers.EntityHelper;
import dev.sterner.lodestone.setup.LodestoneRenderLayerRegistry;
import dev.sterner.lodestone.systems.easing.Easing;
import dev.sterner.lodestone.systems.rendering.VFXBuilders;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.entity.night_terror.NightTerrorSeekerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
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
import static dev.sterner.malum.client.render.entity.FloatingItemEntityRenderer.renderSpiritGlimmer;

public class NightTerrorEntityRenderer extends EntityRenderer<NightTerrorSeekerEntity> {

	private static final Identifier LIGHT_TRAIL = Malum.id("textures/vfx/light_trail.png");
	private static final RenderLayer LIGHT_TYPE = LodestoneRenderLayerRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(LIGHT_TRAIL);
	private static final RenderLayer DARK_TYPE = LodestoneRenderLayerRegistry.TRANSPARENT_TEXTURE_TRIANGLE.apply(LIGHT_TRAIL);

	public NightTerrorEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0f;
		this.shadowOpacity = 0f;
	}

	@Override
	public void render(NightTerrorSeekerEntity entity, float entityYaw, float partialTicks, MatrixStack poseStack, VertexConsumerProvider bufferIn, int packedLightIn) {
		poseStack.push();
		List<EntityHelper.PastPosition> positions = new ArrayList<>(entity.pastPositions);
		if (positions.size() > 1) {
			for (int i = 0; i < positions.size() - 2; i++) {
				EntityHelper.PastPosition position = positions.get(i);
				EntityHelper.PastPosition nextPosition = positions.get(i + 1);
				float x = (float) MathHelper.lerp(partialTicks, position.position.x, nextPosition.position.x);
				float y = (float) MathHelper.lerp(partialTicks, position.position.y, nextPosition.position.y);
				float z = (float) MathHelper.lerp(partialTicks, position.position.z, nextPosition.position.z);
				positions.set(i, new EntityHelper.PastPosition(new Vec3d(x, y, z), position.time));
			}
		}
		float x = (float) MathHelper.lerp(partialTicks, entity.lastRenderX, entity.getX());
		float y = (float) MathHelper.lerp(partialTicks, entity.lastRenderY, entity.getY());
		float z = (float) MathHelper.lerp(partialTicks, entity.lastRenderZ, entity.getZ());
		if (positions.size() > 1) {
			positions.set(positions.size() - 1, new EntityHelper.PastPosition(new Vec3d(x, y + 0.25F, z).add(entity.getVelocity().multiply(partialTicks, partialTicks, partialTicks)), 0));
		}

		List<Vector4f> mappedPastPositions = positions.stream().map(p -> p.position).map(p -> new Vector4f((float) p.x, (float) p.y, (float) p.z, 1)).collect(Collectors.toList());
		VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat().setOffset(-x, -y, -z);
		Vector3f offset = new Vector3f(-x, -y, -z);

		float trailVisibility = MathHelper.clamp(entity.age / 10f,0, 1);
		Color firstColor = NightTerrorSeekerEntity.NIGHT_TERROR_PURPLE;
		Color secondColor = NightTerrorSeekerEntity.NIGHT_TERROR_DARK;

		VertexConsumer lightBuffer = DELAYED_RENDER.getBuffer(LIGHT_TYPE);
		builder.setColor(firstColor);
		builder.renderTrail(lightBuffer, poseStack, offset, mappedPastPositions, f -> 0.4f, f -> builder.setAlpha(Math.max(0, Easing.SINE_IN.ease(f, 0, 0.5f, 1))));
		builder.renderTrail(lightBuffer, poseStack, offset, mappedPastPositions, f -> 0.2f, f -> builder.setAlpha(Math.max(0, Easing.SINE_IN.ease(f, 0, 0.75f, 1))));

		VertexConsumer darkBuffer = DELAYED_RENDER.getBuffer(DARK_TYPE);
		builder.setColor(secondColor);
		builder.renderTrail(darkBuffer, poseStack, offset, mappedPastPositions, f -> 0.45f, f -> builder.setAlpha(Math.max(0, Easing.SINE_IN.ease(f, 0, 0.5f, 1))));
		builder.renderTrail(darkBuffer, poseStack, offset, mappedPastPositions, f -> 0.25f, f -> builder.setAlpha(Math.max(0, Easing.SINE_IN.ease(f, 0, 0.75f, 1))));


		poseStack.translate(0, 0.25F, 0);
		poseStack.scale(1.2f * trailVisibility, 1.2f * trailVisibility, 1.2f * trailVisibility);
		builder.setOffset(0, 0, 0);
		builder.setColor(firstColor);
		builder.setAlpha(trailVisibility);
		renderSpiritGlimmer(poseStack, builder, partialTicks);

		poseStack.pop();

		super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public Identifier getTexture(NightTerrorSeekerEntity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

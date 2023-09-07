package dev.sterner.malum.client.render.block;

import dev.sterner.lodestone.handlers.RenderHandler;
import dev.sterner.lodestone.setup.LodestoneRenderLayerRegistry;
import dev.sterner.lodestone.systems.rendering.VFXBuilders;
import dev.sterner.malum.common.blockentity.totem.TotemPoleBlockEntity;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static dev.sterner.lodestone.helpers.RenderHelper.FULL_BRIGHT;

public class TotemPoleBlockEntityRenderer implements BlockEntityRenderer<TotemPoleBlockEntity> {
	public static Map<MalumSpiritType, SpriteIdentifier> overlayHashmap = new HashMap<>();

	public TotemPoleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		MalumSpiritTypeRegistry.SPIRITS.forEach((s, t) ->
				overlayHashmap.put(t, new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, t.getOverlayTexture()))
		);
	}

	@Override
	public void render(TotemPoleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Direction direction = entity.getCachedState().get(Properties.HORIZONTAL_FACING);
		if (entity.type == null) {
			return;
		}
		renderQuad(overlayHashmap.get(entity.type), entity.type.getColor(), entity.currentColor/20f, direction, matrices);
	}

	public void renderQuad(SpriteIdentifier material, Color color, float alpha, Direction direction, MatrixStack matrices) {
		Sprite sprite = material.getSprite();
		VertexConsumer consumer = RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayerRegistry.ADDITIVE_BLOCK);

		Vector3f[] positions = new Vector3f[]{new Vector3f(0, 0, 2.01f), new Vector3f(2, 0, 2.01f), new Vector3f(2, 2, 2.01f), new Vector3f(0, 2, 2.01f)};

		matrices.push();
		matrices.translate(0.5f, 0.5f, 0.5f);
		matrices.multiply(new Quaternionf().rotateY(-direction.asRotation()*((float)Math.PI/180F)));
		matrices.translate(-0.5f, -0.5f, -0.5f);
		VFXBuilders.createWorld()
				.setPosColorTexLightmapDefaultFormat()
				.setColor(color, alpha)
				.setLight(FULL_BRIGHT)
				.setUV(sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV())
				.renderQuad(consumer, matrices, positions, 0.5f);
		matrices.pop();
	}

	public float rotation(Direction direction) {
		if (direction == Direction.NORTH || direction == Direction.SOUTH) {
			direction = direction.getOpposite();
		}
		return direction.asRotation();
	}
}

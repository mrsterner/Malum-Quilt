package dev.sterner.malum.client.render.block;

import dev.sterner.lodestone.handlers.RenderHandler;
import dev.sterner.lodestone.setup.LodestoneRenderLayerRegistry;
import dev.sterner.lodestone.systems.rendering.VFXBuilders;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.blockentity.VoidConduitBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class VoidConduitBlockEntityRenderer implements BlockEntityRenderer<VoidConduitBlockEntity> {

	public static final Identifier VIGNETTE = Malum.id("textures/block/weeping_well/primordial_soup_vignette.png");

	public VoidConduitBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
	}

	@Override
	public void render(VoidConduitBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		renderQuad(matrices);
	}

	public void renderQuad(MatrixStack matrices) {
		float height = 0.75f;
		float width = 1.5f;
		var textureConsumer = RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayerRegistry.TRANSPARENT_TEXTURE.applyAndCache(VIGNETTE));
		Vector3f[] positions = new Vector3f[]{new Vector3f(-width, height, width), new Vector3f(width, height, width), new Vector3f(width, height, -width), new Vector3f(-width, height, -width)};
		VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();

		matrices.push();
		matrices.translate(0.5f, 0.001f, 0.5f);
		builder.renderQuad(textureConsumer, matrices, positions, 1f);
		builder.setPosColorLightmapDefaultFormat();
		matrices.pop();
	}


}

package dev.sterner.malum.client.render.entity;

import dev.sterner.malum.common.entity.boomerang.ScytheBoomerangEntity;
import dev.sterner.malum.common.item.tools.MalumScytheItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;

public class ScytheBoomerangEntityRenderer extends EntityRenderer<ScytheBoomerangEntity> {
	public final ItemRenderer itemRenderer;

	public ScytheBoomerangEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
		this.itemRenderer = ctx.getItemRenderer();
		this.shadowRadius = 2F;
		this.shadowOpacity = 0.5F;
	}

	@Override
	public void render(ScytheBoomerangEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		ItemStack itemstack = entity.getItem();
		BakedModel model = this.itemRenderer.getHeldItemModel(itemstack, entity.world, null, 1);
		matrices.multiply(Axis.X_POSITIVE.rotationDegrees(90F));
		matrices.scale(2f, 2f, 2f);
		matrices.multiply(Axis.Z_POSITIVE.rotation((entity.age + tickDelta) * 0.9f));
		itemRenderer.renderItem(itemstack, itemstack.getItem() instanceof MalumScytheItem ? ModelTransformation.Mode.NONE : ModelTransformation.Mode.FIXED, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);

		matrices.pop();

		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(ScytheBoomerangEntity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

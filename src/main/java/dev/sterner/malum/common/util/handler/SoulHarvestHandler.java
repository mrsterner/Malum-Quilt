package dev.sterner.malum.common.util.handler;

import dev.sterner.lodestone.setup.LodestoneRenderLayerRegistry;
import dev.sterner.lodestone.systems.rendering.VFXBuilders;
import dev.sterner.malum.Malum;
import dev.sterner.malum.common.component.MalumComponents;
import dev.sterner.malum.common.component.SoulDataComponent;
import dev.sterner.malum.common.item.spirit.SoulStaveItem;
import dev.sterner.malum.common.network.packet.s2c.entity.SuccessfulSoulHarvestParticlePacket;
import dev.sterner.malum.common.spirit.MalumEntitySpiritData;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.sterner.lodestone.handlers.RenderHandler.DELAYED_RENDER;

public class SoulHarvestHandler {

	public static final float PRIMING_END = 10f;
	public static final float HARVEST_DURATION = 90f;

	public UUID targetedSoulUUID;
	public int targetedSoulId;
	public int soulFetchCooldown;

	public NbtCompound serializeNBT() {
		NbtCompound tag = new NbtCompound();
		if (targetedSoulUUID != null) {
			tag.putUuid("targetedSoulUUID", targetedSoulUUID);
		}
		if (targetedSoulId != 0) {
			tag.putInt("targetedSoulId", targetedSoulId);
		}
		if (soulFetchCooldown != 0) {
			tag.putInt("soulFetchCooldown", soulFetchCooldown);
		}
		return tag;
	}

	public void deserializeNBT(NbtCompound tag) {
		if (tag.contains("targetedSoulUUID")) {
			targetedSoulUUID = tag.getUuid("targetedSoulUUID");
		}
		targetedSoulId = tag.getInt("targetedSoulId");
		soulFetchCooldown = tag.getInt("soulFetchCooldown");
	}



	public static void playerTick(LivingEntity p) {
		if (p instanceof PlayerEntity player) {
			SoulHarvestHandler soulHarvestHandler = MalumComponents.PLAYER_COMPONENT.get(player).soulHarvestHandler;

			boolean isHoldingStave = (player.isHolding(s -> s.getItem() instanceof SoulStaveItem));
			boolean isUsingStave = player.isUsingItem();
			if (isHoldingStave) {
				if (!isUsingStave) {
					//Here we try and figure out what entity the player wants to target with their stave.
					//We basically just find all nearby entities, and search for the one with the least angle difference between the angle towards the player, and the look direction of the player
					soulHarvestHandler.soulFetchCooldown--;
					if (soulHarvestHandler.soulFetchCooldown <= 0) {
						soulHarvestHandler.soulFetchCooldown = 5;
						List<LivingEntity> entities = new ArrayList<>(player.getWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(7f), e -> !e.getUuid().equals(player.getUuid())));
						double biggestAngle = 0;
						LivingEntity chosenEntity = null;
						for (LivingEntity entity : entities) {
							if (!entity.isAlive() || MalumComponents.SOUL_COMPONENT.get(entity).isSoulless() || SpiritHelper.getEntitySpiritData(entity) == null) {
								continue;
							}
							Vec3d lookDirection = player.getRotationVector();
							Vec3d directionToEntity = entity.getPos().subtract(player.getPos()).normalize();
							double angle = lookDirection.dotProduct(directionToEntity);
							if (angle > biggestAngle && angle > 0.5f) {
								biggestAngle = angle;
								chosenEntity = entity;
							}
						}
						if (chosenEntity == null) {
							soulHarvestHandler.targetedSoulUUID = null;
							soulHarvestHandler.targetedSoulId = -1;
						} else if (!chosenEntity.getUuid().equals(soulHarvestHandler.targetedSoulUUID) || MalumComponents.SOUL_COMPONENT.get(chosenEntity).getSoulThiefUUID() == null) {
							soulHarvestHandler.targetedSoulUUID = chosenEntity.getUuid();
							soulHarvestHandler.targetedSoulId = chosenEntity.getId();
							MalumComponents.SOUL_COMPONENT.get(chosenEntity).setSoulThiefUUID(player.getUuid());
							if (chosenEntity.getWorld() instanceof ServerWorld) {
								MalumComponents.SOUL_COMPONENT.sync(chosenEntity);
							}
						}
					}
				}

				int desiredProgress = isUsingStave ? 160 : 10;
				if (soulHarvestHandler.targetedSoulUUID != null) {
					//Here we essentially "prime" the entity that we are targeting.
					//If the player isn't using their staff, we raise their target's soul harvest progress to ten, which the renderer understands as "put a target on this guy"
					//If the player is using their staff, we instead just start draining the soul
					Entity entity = player.getWorld().getEntityById(soulHarvestHandler.targetedSoulId);
					if (entity instanceof LivingEntity livingEntity) {
						var soulData = MalumComponents.SOUL_COMPONENT.get(livingEntity);
						if (soulData.getSoulSeparationProgress() < desiredProgress) {
							soulData.setSoulSeparationProgress(soulData.getSoulSeparationProgress() + 1);
						}
					}
				}
				//and here's where all the magic happens. When a player is using their staff, we check if the harvest progress has reached it's maximum.
				//Once it has, we will spawn a soul and mark the targeted entity as soulless.
				if (isUsingStave) {
					if (player.getWorld() instanceof ServerWorld) {
						if (soulHarvestHandler.targetedSoulUUID != null) {
							Entity entity = player.getWorld().getEntityById(soulHarvestHandler.targetedSoulId);
							if (entity instanceof LivingEntity livingEntity) {
								var soulData = MalumComponents.SOUL_COMPONENT.get(livingEntity);

								if (soulData.getSoulSeparationProgress() >= HARVEST_DURATION) {
									Vec3d position = entity.getPos().add(0, entity.getStandingEyeHeight() / 2f, 0);
									MalumEntitySpiritData data = SpiritHelper.getEntitySpiritData(livingEntity);
									PlayerLookup.tracking(entity).forEach(track -> SuccessfulSoulHarvestParticlePacket.send(track, data.primaryType.getColor(), data.primaryType.getEndColor(), position.x, position.y, position.z));

									if (livingEntity instanceof MobEntity mob) {
										SoulDataComponent.removeSentience(mob);
									}
									soulData.setSoulless(true);
									soulData.setSoulThiefUUID(player.getUuid());

									player.swingHand(player.getActiveHand(), true);
									player.stopUsingItem();
									MalumComponents.SOUL_COMPONENT.sync(livingEntity);
								}
							}
						}
					}
				}
			} else if (soulHarvestHandler.targetedSoulUUID != null) {
				soulHarvestHandler.targetedSoulUUID = null;
				soulHarvestHandler.targetedSoulId = -1;
			}
		}

	}

	public static void removeSentience(MobEntity mob) {
		mob.goalSelector.getGoals().removeIf(g -> g.getGoal() instanceof LookAtEntityGoal || g.getGoal() instanceof MeleeAttackGoal || g.getGoal() instanceof CreeperIgniteGoal || g.getGoal() instanceof EscapeDangerGoal || g.getGoal() instanceof LookAroundGoal || g.getGoal() instanceof FleeEntityGoal);
	}


	public static class ClientOnly {
		private static final Identifier SOUL_NOISE = Malum.id("textures/vfx/noise/soul_noise.png");
		private static final RenderLayer SOUL_NOISE_TYPE = LodestoneRenderLayerRegistry.RADIAL_NOISE.apply(SOUL_NOISE);
		private static final Identifier PREVIEW_NOISE =  Malum.id("textures/vfx/noise/harvest_noise.png");
		private static final RenderLayer PREVIEW_NOISE_TYPE = LodestoneRenderLayerRegistry.RADIAL_SCATTER_NOISE.apply(PREVIEW_NOISE);

		private static final Identifier TARGET_TEXTURE = Malum.id("textures/block/the_device.png");
		private static final RenderLayer TARGET_TYPE = LodestoneRenderLayerRegistry.ADDITIVE_TEXTURE.apply(TARGET_TEXTURE);

		@SuppressWarnings("all")
		public static void addRenderLayer(EntityRenderer<?> render) {
			if (render instanceof LivingEntityRenderer livingRenderer) {
				livingRenderer.addFeature(new SoulHarvestHandler.ClientOnly.HarvestRenderLayer<>(livingRenderer));
			}
		}

		public static class HarvestRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

			public HarvestRenderLayer(FeatureRendererContext<T, M> parent) {
				super(parent);
			}

			@Override
			public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
				MalumComponents.SOUL_COMPONENT.maybeGet(entity).ifPresent(c -> {
					if (c.getSoulThiefUUID() != null) {
						PlayerEntity player = entity.getWorld().getPlayerByUuid(c.getSoulThiefUUID());
						if (player != null && player.isAlive() && entity.isAlive()) {
							MalumEntitySpiritData data = SpiritHelper.getEntitySpiritData(entity);
							matrices.pop();
							renderSoulHarvestEffects(matrices, entity, player, data.primaryType.getColor(), c, tickDelta);
							matrices.push();
						}
					}
				});
			}
		}


		public static void renderSoulHarvestEffects(MatrixStack poseStack, LivingEntity target, PlayerEntity player, Color color, SoulDataComponent soulData, float partialTicks) {
			if (soulData.getSoulSeparationProgress() > 0f) {
				poseStack.push();
				Box boundingBox = target.getBoundingBox();
				Vec3d playerPosition = new Vec3d(player.prevX, player.prevY, player.prevZ).lerp(player.getPos(), partialTicks);
				Vec3d entityPosition = new Vec3d(target.prevX, target.prevY, target.prevZ).lerp(target.getPos(), partialTicks);
				Vec3d toPlayer = playerPosition.subtract(entityPosition).normalize().multiply(boundingBox.getXLength() * 0.5f, 0, boundingBox.getZLength() * 0.5f);

				VertexConsumer soulNoise = DELAYED_RENDER.getBuffer(TARGET_TYPE);

				poseStack.translate(toPlayer.x, toPlayer.y + target.getHeight() / 2f, toPlayer.z);
				poseStack.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
				poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

				//preview
				float intensity = Math.min(10, soulData.getSoulSeparationProgress()) / 10f;


				VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat()
					.setColor(color.brighter())
					.setAlpha(intensity * 0.6f)
					.renderQuad(soulNoise, poseStack, intensity*0.4f);
				poseStack.pop();
			}
		}
	}
}

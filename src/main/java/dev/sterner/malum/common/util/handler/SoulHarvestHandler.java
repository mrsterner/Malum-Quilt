package dev.sterner.malum.common.util.handler;

import com.sammy.lodestone.setup.LodestoneRenderLayerRegistry;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import dev.sterner.malum.Malum;
import dev.sterner.malum.api.event.EntitySpawnedEvent;
import dev.sterner.malum.api.event.LivingEntityEvent;
import dev.sterner.malum.common.component.MalumComponents;
import dev.sterner.malum.common.entity.boomerang.ScytheBoomerangEntity;
import dev.sterner.malum.common.entity.spirit.SoulEntity;
import dev.sterner.malum.common.item.spirit.SoulStaveItem;
import dev.sterner.malum.common.network.packet.s2c.entity.SuccessfulSoulHarvestParticlePacket;
import dev.sterner.malum.common.registry.MalumTagRegistry;
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
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.sammy.lodestone.handlers.RenderHandler.DELAYED_RENDER;
import static com.sammy.lodestone.helpers.RenderHelper.FULL_BRIGHT;

public class SoulHarvestHandler {
	public static void init(){
		EntitySpawnedEvent.EVENT.register(SoulHarvestHandler::markAsSpawnerSpawned);
		LivingEntityEvent.TICK_EVENT.register(SoulHarvestHandler::entityTick);
		LivingEntityEvent.TICK_EVENT.register(SoulHarvestHandler::playerTick);
		LivingEntityEvent.ON_TARGETING_EVENT.register(SoulHarvestHandler::preventTargeting);
		LivingEntityEvent.ADDED_EVENT.register(SoulHarvestHandler::updateAi);
	}

	public static void exposeSoul(DamageSource source, float amount, LivingEntity target) {
		if (amount == 0) {
			return;
		}
		if (source.getAttacker() instanceof LivingEntity attacker) {
			ItemStack stack = attacker.getMainHandStack();
			if (source.getSource() instanceof ScytheBoomerangEntity) {
				stack = ((ScytheBoomerangEntity) source.getSource()).scythe;
			}
			if (stack.isIn(MalumTagRegistry.SOUL_HUNTER_WEAPON)) {
				MalumComponents.SPIRIT_COMPONENT.get(target).exposedSoul = 200;
			}
		}
	}

	private static void markAsSpawnerSpawned(Entity entity, World world, float v, float v1, float v2, MobSpawnerLogic mobSpawnerLogic, SpawnReason spawnReason) {
		if (spawnReason != null) {
			if (entity instanceof LivingEntity livingEntity) {
				MalumComponents.SPIRIT_COMPONENT.maybeGet(livingEntity).ifPresent(ec -> {
					if (spawnReason.equals(SpawnReason.SPAWNER)) {
						ec.setSpawnerSpawned(true);
					}
				});
			}
		}
	}

	private static void updateAi(LivingEntity livingEntity, boolean b) {
		MalumComponents.SPIRIT_COMPONENT.maybeGet(livingEntity).ifPresent(ec -> {
			if (livingEntity instanceof MobEntity mob) {
				if (ec.isSoulless()) {
					removeSentience(mob);
				}
			}
		});
	}


	private static boolean preventTargeting(MobEntity mobEntity, @Nullable LivingEntity livingEntity) {
		MalumComponents.SPIRIT_COMPONENT.maybeGet(mobEntity).ifPresent(ec -> {
			if (ec.isSoulless()) {
				mobEntity.setTarget(null);
			}
		});
		return false;
	}


	public static void entityTick(LivingEntity livingEntity) {
		MalumComponents.SPIRIT_COMPONENT.maybeGet(livingEntity).ifPresent(ec -> {
			if (ec.exposedSoul > 0) {
				ec.exposedSoul--;
			}
			if (ec.getSoulThiefUuid() != null && ec.getSoulHarvestProgress() > 0) {
				PlayerEntity player = livingEntity.getWorld().getPlayerByUuid(ec.getSoulThiefUuid());
				if (player != null) {
					MalumComponents.PLAYER_COMPONENT.maybeGet(player).ifPresent(c -> {
						if (!player.isUsingItem() && ec.getSoulHarvestProgress() > 10) {
							ec.setSoulHarvestProgress(ec.getSoulHarvestProgress() - 2f);
						}
						if (ec.getSoulHarvestProgress() <= 10 && !ec.isSoulless()) {
							if (c.targetedSoulUUID == null || !livingEntity.getUuid().equals(c.targetedSoulUUID)) {
								ec.setSoulHarvestProgress(ec.getSoulHarvestProgress() - 0.5f);
								if (ec.getSoulHarvestProgress() == 0) {
									ec.setSoulThiefUuid(null);
								}
							}
						}
					});
				}
			}
		});
	}

	public static void playerTick(LivingEntity p) {
		if(p instanceof PlayerEntity player) {
			MalumComponents.PLAYER_COMPONENT.maybeGet(player).ifPresent(c -> {
				boolean isHoldingStave = (player.isHolding(s -> s.getItem() instanceof SoulStaveItem));
				if (isHoldingStave) {
					boolean isUsingStave = player.isUsingItem();
					int harvestVFXCap = isUsingStave ? 160 : 10;
					if (c.targetedSoulUUID != null) {
						Entity entity = player.getWorld().getEntityById(c.targetedSoulId);
						if (entity instanceof LivingEntity livingEntity) {
							MalumComponents.SPIRIT_COMPONENT.maybeGet(livingEntity).ifPresent(ec -> {
								if (ec.getSoulHarvestProgress() < harvestVFXCap) {
									ec.setSoulHarvestProgress(ec.getSoulHarvestProgress() + 1);
								}
							});
						}
					}
					if (isUsingStave) {
						//harvest soul
						if (player.getWorld() instanceof ServerWorld serverWorld) {
							if (c.targetedSoulUUID != null) {
								Entity entity = serverWorld.getEntityById(c.targetedSoulId);
								if (entity instanceof LivingEntity livingEntity) {
									MalumComponents.SPIRIT_COMPONENT.maybeGet(livingEntity).ifPresent(ec -> {
										if (ec.getHarvestProgress() >= 150) {
											Vec3d position = entity.getPos().add(0, entity.getHeight() / 2f, 0);
											MalumEntitySpiritData data = SpiritHelper.getEntitySpiritData(livingEntity);
											SoulEntity soulEntity = new SoulEntity(entity.getWorld(), data, player.getUuid(),
													position.x,
													position.y,
													position.z,
													MathHelper.nextFloat(Malum.RANDOM, -0.1f, 0.1f),
													0.05f + MathHelper.nextFloat(Malum.RANDOM, 0.05f, 0.15f),
													MathHelper.nextFloat(Malum.RANDOM, -0.1f, 0.1f));
											serverWorld.spawnEntity(soulEntity);
											PlayerLookup.tracking(entity).forEach(track -> SuccessfulSoulHarvestParticlePacket.send(track, data.primaryType.getColor(), data.primaryType.getEndColor(), position.x, position.y, position.z));
											if (livingEntity instanceof MobEntity mob) {
												removeSentience(mob);
											}
											ec.setSoulless(true);
											ec.setSoulThiefUuid(player.getUuid());
											player.swingHand(player.getActiveHand(), true);
											player.stopUsingItem();
											MalumComponents.SPIRIT_COMPONENT.sync(livingEntity);
										}
									});
								}
							}
						}
					} else {
						//fetch soul
						c.soulFetchCooldown--;
						if (c.soulFetchCooldown <= 0) {
							c.soulFetchCooldown = 5;
							List<LivingEntity> entities = new ArrayList<>(player.getWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(7f), e -> !e.getUuid().equals(player.getUuid())));
							double biggestAngle = 0;
							LivingEntity chosenEntity = null;
							for (LivingEntity entity : entities) {
								if (!entity.isAlive() || MalumComponents.SPIRIT_COMPONENT.get(entity).isSoulless() || SpiritHelper.getEntitySpiritData(entity) == null) {
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
							if (chosenEntity != null && (!chosenEntity.getUuid().equals(c.targetedSoulUUID) || MalumComponents.SPIRIT_COMPONENT.get(chosenEntity).getSoulThiefUuid() == null)) {
								c.targetedSoulUUID = chosenEntity.getUuid();
								c.targetedSoulId = chosenEntity.getId();
								MalumComponents.SPIRIT_COMPONENT.maybeGet(chosenEntity).ifPresent(ec -> ec.setSoulThiefUuid(player.getUuid()));
								if (chosenEntity.getWorld() instanceof ServerWorld) {
									MalumComponents.SPIRIT_COMPONENT.sync(chosenEntity);
								}
							}
							if (chosenEntity == null) {
								c.targetedSoulUUID = null;
								c.targetedSoulId = -1;
							}
						}
					}
				} else if (c.targetedSoulUUID != null) {
					c.targetedSoulUUID = null;
					c.targetedSoulId = -1;
				}
			});
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
				MalumComponents.SPIRIT_COMPONENT.maybeGet(entity).ifPresent(c -> {
					if (c.getSoulThiefUuid() != null) {
						PlayerEntity player = entity.getWorld().getPlayerByUuid(c.getSoulThiefUuid());
						if (player != null && player.isAlive() && entity.isAlive()) {
							MalumEntitySpiritData data = SpiritHelper.getEntitySpiritData(entity);
							matrices.pop();
							renderSoulHarvestEffects(matrices, entity, player, data.primaryType.getColor(), c.getSoulData(), tickDelta);
							matrices.push();
						}
					}
				});
			}
		}


		public static void renderSoulHarvestEffects(MatrixStack poseStack, LivingEntity target, PlayerEntity player, Color color, SoulDataHandler soulData, float partialTicks) {
			if (soulData.soulSeparationProgress > 0f) {
				poseStack.push();
				Box boundingBox = target.getBoundingBox();
				Vec3d playerPosition = new Vec3d(player.prevX, player.prevY, player.prevZ).lerp(player.getPos(), partialTicks);
				Vec3d entityPosition = new Vec3d(target.prevX, target.prevY, target.prevZ).lerp(target.getPos(), partialTicks);
				Vec3d toPlayer = playerPosition.subtract(entityPosition).normalize().multiply(boundingBox.getXsize() * 0.5f, 0, boundingBox.getZsize() * 0.5f);

				VertexConsumer soulNoise = DELAYED_RENDER.getBuffer(TARGET_TYPE);

				poseStack.translate(toPlayer.x, toPlayer.y + target.getHeight() / 2f, toPlayer.z);
				poseStack.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
				poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

				//preview
				float intensity = Math.min(10, soulData.soulSeparationProgress) / 10f;


				VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat()
					.setColor(color.brighter())
					.setAlpha(intensity * 0.6f)
					.renderQuad(soulNoise, poseStack, intensity*0.4f);
				poseStack.pop();
			}
		}
	}
}

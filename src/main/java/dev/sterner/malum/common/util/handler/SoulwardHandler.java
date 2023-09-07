package dev.sterner.malum.common.util.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sterner.lodestone.handlers.screenparticle.ScreenParticleHandler;
import dev.sterner.lodestone.setup.LodestoneScreenParticleRegistry;
import dev.sterner.lodestone.setup.LodestoneShaderRegistry;
import dev.sterner.lodestone.systems.particle.ScreenParticleBuilder;
import dev.sterner.lodestone.systems.particle.data.ColorParticleData;
import dev.sterner.lodestone.systems.particle.data.GenericParticleData;
import dev.sterner.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.lodestone.systems.rendering.VFXBuilders;
import dev.sterner.malum.Malum;
import dev.sterner.malum.api.event.SoulwardDamageAbsorbDamageEvent;
import dev.sterner.malum.common.component.MalumComponents;
import dev.sterner.malum.common.component.MalumPlayerComponent;
import dev.sterner.malum.common.registry.MalumAttributeRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import dev.sterner.malum.common.spirit.MalumSpiritAffinity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;

import java.util.Objects;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.ARCANE_SPIRIT;

public class SoulwardHandler extends MalumSpiritAffinity {
	public SoulwardHandler() {
		super(ARCANE_SPIRIT);
	}

	public static void recoverSoulWard(PlayerEntity player) {
		MalumPlayerComponent component = MalumComponents.PLAYER_COMPONENT.get(player);
		EntityAttributeInstance cap = player.getAttributeInstance(MalumAttributeRegistry.SOUL_WARD_CAP);
		if (cap != null) {
			if (component.soulWard < cap.getValue() && component.soulWardProgress <= 0) {
				component.soulWard++;
				if (player.getWorld().isClient && !player.isCreative()) {
					player.playSound(component.soulWard >= cap.getValue() ? MalumSoundRegistry.SOUL_WARD_CHARGE : MalumSoundRegistry.SOUL_WARD_GROW, 1, MathHelper.nextFloat(player.getRandom(), 0.6f, 1.4f));
				}
				component.soulWardProgress = getSoulWardCooldown(player);
			} else {
				component.soulWardProgress--;
			}
			if (component.soulWard > cap.getValue()) {
				component.soulWard = (float) cap.getValue();
			}
			MalumComponents.PLAYER_COMPONENT.sync(player);
		}
	}

	public static float shieldPlayer(LivingEntity entity, DamageSource source, float amount) {
		if (entity instanceof PlayerEntity player) {
			if (!player.getWorld().isClient) {
				MalumPlayerComponent component = MalumComponents.PLAYER_COMPONENT.get(player);
				component.soulWardProgress = getSoulWardCooldown(0) + getSoulWardCooldown(player);
				if (component.soulWard > 0) {
					float multiplier = source.isOf(DamageTypes.MAGIC) ? 0.1f : 0.7f;
					float result = amount * multiplier;
					float absorbed = amount - result;
					double strength = player.getAttributeValue(MalumAttributeRegistry.SOUL_WARD_STRENGTH);
					float soulwardLost = (float) (component.soulWard - (absorbed / strength));
					if (strength != 0) {
						component.soulWard = Math.max(0, soulwardLost);
					} else {
						soulwardLost = component.soulWard;
						component.soulWard = 0;
					}

					SoulwardDamageAbsorbDamageEvent.ON_ABSORB_DAMAGE_EVENT.invoker().react(player, source, soulwardLost, absorbed);
					player.getWorld().playSound(null, player.getBlockPos(), MalumSoundRegistry.SOUL_WARD_HIT, SoundCategory.PLAYERS, 1, MathHelper.nextFloat(player.getRandom(), 1.5f, 2f));
					MalumComponents.PLAYER_COMPONENT.sync(player);
					return result;
				}
			}
		}
		return amount;
	}

	public static int getSoulWardCooldown(PlayerEntity player) {
		return getSoulWardCooldown(player.getAttributeValue(MalumAttributeRegistry.SOUL_WARD_RECOVERY_SPEED));
	}

	public static int getSoulWardCooldown(double recoverySpeed) {
		int baseValue = 60;
		if (recoverySpeed == 0) {
			return baseValue;
		}
		float n = 0.6f;
		double exponent = 1 + (Math.pow(recoverySpeed * 0.25f + 1, 1 - n) - 1) / (1 - n);
		return (int) (baseValue * (1 / exponent));
	}

	public static class Client {
		public static Identifier getSoulWardTexture() {
			return Malum.id("textures/gui/soul_ward/default.png");
		}

		public static void renderSoulWard(DrawContext context, Window window) {
			MatrixStack matrixStack = context.getMatrices();
			final MinecraftClient client = MinecraftClient.getInstance();
			ClientPlayerEntity player = client.player;
			if (player != null && client.world != null && !player.isCreative() && !player.isSpectator()) {
				float soulWard = MalumComponents.PLAYER_COMPONENT.get(player).soulWard;
				if (soulWard > 0) {
					float absorb = MathHelper.ceil(player.getAbsorptionAmount());
					float maxHealth = (float) Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).getValue();
					float armor = (float) Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).getValue();

					int left = window.getScaledWidth() / 2 - 91;
					int top = window.getScaledHeight() - 59;

					if (armor == 0) {
						top += 4;
					}
					int healthRows = MathHelper.ceil((maxHealth + absorb) / 2.0F / 10.0F);
					int rowHeight = Math.max(10 - (healthRows - 2), 3);

					matrixStack.push();
					RenderSystem.depthMask(false);
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					RenderSystem.setShaderTexture(0, getSoulWardTexture());
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					ShaderProgram shader = LodestoneShaderRegistry.DISTORTED_TEXTURE.getInstance().get();
					shader.getUniformOrDefault("YFrequency").set(15f);
					shader.getUniformOrDefault("XFrequency").set(15f);
					shader.getUniformOrDefault("Speed").set(550f);
					shader.getUniformOrDefault("Intensity").set(150f);
					VFXBuilders.ScreenVFXBuilder builder = VFXBuilders.createScreen()
							.setPosColorTexLightmapDefaultFormat()
							.setShader(() -> shader);
					for (int i = 0; i < Math.ceil(soulWard / 3f); i++) {
						int row = (int) (Math.ceil(i) / 10f);
						int x = left + i % 10 * 8;
						int y = top - row * 4 + rowHeight * 2 - 15;
						int progress = Math.min(3, (int) soulWard - i * 3);
						int xTextureOffset = 1 + (3 - progress) * 15;

						shader.getUniformOrDefault("UVCoordinates").set(new Vector4f(xTextureOffset / 45f, (xTextureOffset + 12) / 45f, 1 / 45f, 13 / 45f));
						shader.getUniformOrDefault("TimeOffset").set(i * 150f);

						builder.setPositionWithWidth(x - 2, y - 2, 13, 13)
								.setUVWithWidth(xTextureOffset, 0, 13, 13, 45)
								.draw(matrixStack);
						if (ScreenParticleHandler.canSpawnParticles) {
							final float spin = client.world.random.nextFloat() * 6.28f;
							ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.WISP, ScreenParticleHandler.EARLY_PARTICLES)
									.setLifetime(20)
									.setColorData(ColorParticleData.create(MalumSpiritTypeRegistry.ARCANE_SPIRIT.getColor().brighter(), MalumSpiritTypeRegistry.ARCANE_SPIRIT.getEndColor()).build())
									.setScaleData(GenericParticleData.create(0.2f * progress, 0f).build())
									.setTransparencyData(GenericParticleData.create(0.05f, 0).setCoefficient(0.75f).build())
									.setSpinData(SpinParticleData.create(spin).build())
									.setRandomOffset(2)
									.setRandomMotion(0.5f, 0.5f)
									.addMotion(0, 0.2f)
									.repeat(x + 5, y + 5, 1);
						}
					}
					RenderSystem.depthMask(true);
					RenderSystem.disableBlend();
					matrixStack.pop();
				}
			}
		}
	}
}

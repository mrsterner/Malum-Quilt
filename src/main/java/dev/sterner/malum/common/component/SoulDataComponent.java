package dev.sterner.malum.common.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.sterner.malum.common.entity.boomerang.ScytheBoomerangEntity;
import dev.sterner.malum.common.registry.MalumTagRegistry;
import dev.sterner.malum.common.util.handler.SoulHarvestHandler;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.spawner.Spawner;

import java.util.UUID;

public class SoulDataComponent implements AutoSyncedComponent {

	private LivingEntity livingEntity;

	private float exposedSoulDuration;
	private boolean soulless;
	private boolean spawnerSpawned;

	private float soulSeparationProgress;
	private UUID soulThiefUUID;

	public SoulDataComponent(LivingEntity livingEntity) {
		this.livingEntity = livingEntity;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		exposedSoulDuration = tag.getFloat("exposedSoulDuration");
		soulless = tag.getBoolean("soulless");
		spawnerSpawned = tag.getBoolean("spawnerSpawned");

		soulSeparationProgress = tag.getFloat("soulSeparationProgress");
		if (tag.contains("soulThiefUUID")) {
			soulThiefUUID = tag.getUuid("soulThiefUUID");
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		if (exposedSoulDuration != 0) {
			tag.putFloat("exposedSoulDuration", exposedSoulDuration);
		}
		tag.putBoolean("soulless", soulless);
		tag.putBoolean("spawnerSpawned", spawnerSpawned);

		if (soulSeparationProgress != 0) {
			tag.putFloat("soulSeparationProgress", soulSeparationProgress);
		}
		if (soulThiefUUID != null) {
			tag.putUuid("soulThiefUUID", soulThiefUUID);
		}
	}



	public static void removeSentience(MobEntity mob) {
		mob.goalSelector.getGoals().removeIf(g ->
			g.getGoal() instanceof LookAtEntityGoal
				|| g.getGoal() instanceof MeleeAttackGoal
				|| g.getGoal() instanceof CreeperIgniteGoal
				|| g.getGoal() instanceof EscapeDangerGoal
				|| g.getGoal() instanceof LookAroundGoal
				|| g.getGoal() instanceof FleeEntityGoal<?>
		);
	}

	public static ItemStack getSoulHunterWeapon(DamageSource source, LivingEntity attacker) {
		ItemStack stack = attacker.getMainHandStack();

		if (source.getSource() instanceof ScytheBoomerangEntity scytheBoomerang) {
			stack = scytheBoomerang.getStack();
		}
		return stack;
	}

	public static boolean setTaget(MobEntity mobEntity, LivingEntity livingEntity) {
        var v = MalumComponents.SOUL_COMPONENT.maybeGet(mobEntity);
        return v.map(soulDataComponent -> soulDataComponent.soulless).orElse(false);
    }

	public static void tick(LivingEntity entity) {
		//here we tick down all the data and reset it overtime.
		var v = MalumComponents.SOUL_COMPONENT.maybeGet(entity);
		if (v.isPresent()) {
            SoulDataComponent soulData = v.get();
			if (soulData.getExposedSoulDuration() > 0) {
				soulData.setExposedSoulDuration(soulData.getExposedSoulDuration() - 1);
			}

			if (soulData.getSoulThiefUUID() != null && soulData.getSoulSeparationProgress() > 0) {
				PlayerEntity soulThief = entity.getWorld().getPlayerByUuid(soulData.getSoulThiefUUID());
				if (soulThief != null) {
					var e = MalumComponents.PLAYER_COMPONENT.maybeGet(soulThief);
					if (e.isPresent()) {
						SoulHarvestHandler soulHarvestHandler = e.get().soulHarvestHandler;
						//If an entity is being targeted by a soul staff, and the isn't using a staff when the entity is past the "targeted" point, we rapidly remove separation progress.
						if (!soulThief.isUsingItem() && soulData.getSoulSeparationProgress() > SoulHarvestHandler.PRIMING_END) {
							soulData.setSoulSeparationProgress(soulData.getSoulSeparationProgress() - 2f);
						}
						//If the entity isn't soulless, and is in the "targeted" point, we slowly remove separation progress and forget the soul thief.
						if (soulData.getSoulSeparationProgress() <= SoulHarvestHandler.PRIMING_END && !soulData.isSoulless()) {
							if (soulHarvestHandler.targetedSoulUUID == null || !entity.getUuid().equals(soulHarvestHandler.targetedSoulUUID)) {
								soulData.setSoulSeparationProgress(soulData.getSoulSeparationProgress() - 0.5f);
								if (soulData.getSoulSeparationProgress() == 0) {
									soulData.setSoulThiefUUID(null);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void hurt(LivingEntityDamageEvents.HurtEvent hurtEvent) {
		//Here we expose an entity's soul if it is struck by a soul hunter weapon, or other means of shattering the soul
		if (hurtEvent.isCanceled() || hurtEvent.damageAmount <= 0) {
			return;
		}
		LivingEntity target = hurtEvent.damaged;
		DamageSource source = hurtEvent.damageSource;
		var v = MalumComponents.SOUL_COMPONENT.maybeGet(target);
		if (v.isPresent()) {
            SoulDataComponent soulData = v.get();
			if (source.getAttacker() instanceof LivingEntity attacker) {
				ItemStack stack = getSoulHunterWeapon(source, attacker);
				if (stack.isIn(MalumTagRegistry.SOUL_HUNTER_WEAPON)) {
					soulData.setExposedSoulDuration(200);
				}
			}
			if (source.getSource() != null && source.getSource().getCommandTags().contains("malum:soul_arrow")) {
				soulData.setExposedSoulDuration(200);
			}
		}
	}

	public static void added(LivingEntity livingEntity, boolean b) {
		MalumComponents.SOUL_COMPONENT.maybeGet(livingEntity).ifPresent(ec -> {
			if (livingEntity instanceof MobEntity mob && ec.isSoulless()) {
				removeSentience(mob);
			}
		});
    }

	public static TriState spawn(MobEntity mobEntity, double v, double v1, double v2, WorldAccess worldAccess, Spawner spawner, SpawnReason spawnReason) {
		if (spawnReason != null) {
			MalumComponents.SOUL_COMPONENT.maybeGet(mobEntity).ifPresent(c -> {
				if (spawnReason.equals(SpawnReason.SPAWNER)) {
					c.setSpawnerSpawned(true);
				}
			});
		}
		return TriState.DEFAULT;
	}

	public LivingEntity getLivingEntity() {
		return livingEntity;
	}

	public void setLivingEntity(LivingEntity livingEntity) {
		this.livingEntity = livingEntity;
		MalumComponents.SOUL_COMPONENT.sync(livingEntity);
	}

	public float getExposedSoulDuration() {
		return exposedSoulDuration;
	}

	public void setExposedSoulDuration(float exposedSoulDuration) {
		this.exposedSoulDuration = exposedSoulDuration;
		MalumComponents.SOUL_COMPONENT.sync(livingEntity);
	}

	public boolean isSoulless() {
		return soulless;
	}

	public void setSoulless(boolean soulless) {
		this.soulless = soulless;
		MalumComponents.SOUL_COMPONENT.sync(livingEntity);
	}

	public boolean isSpawnerSpawned() {
		return spawnerSpawned;
	}

	public void setSpawnerSpawned(boolean spawnerSpawned) {
		this.spawnerSpawned = spawnerSpawned;
		MalumComponents.SOUL_COMPONENT.sync(livingEntity);
	}

	public float getSoulSeparationProgress() {
		return soulSeparationProgress;
	}

	public void setSoulSeparationProgress(float soulSeparationProgress) {
		this.soulSeparationProgress = soulSeparationProgress;
		MalumComponents.SOUL_COMPONENT.sync(livingEntity);
	}

	public UUID getSoulThiefUUID() {
		return soulThiefUUID;
	}

	public void setSoulThiefUUID(UUID soulThiefUUID) {
		this.soulThiefUUID = soulThiefUUID;
		MalumComponents.SOUL_COMPONENT.sync(livingEntity);
	}
}

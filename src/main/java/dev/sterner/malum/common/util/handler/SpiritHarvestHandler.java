package dev.sterner.malum.common.util.handler;

import dev.sterner.lodestone.helpers.ItemHelper;
import dev.emi.trinkets.api.TrinketsApi;
import dev.sterner.malum.MalumConfig;
import dev.sterner.malum.api.interfaces.item.SpiritCollectActivity;
import dev.sterner.malum.common.component.MalumComponents;
import dev.sterner.malum.common.component.SpiritLivingEntityComponent;
import dev.sterner.malum.common.entity.boomerang.ScytheBoomerangEntity;
import dev.sterner.malum.common.item.spirit.SpiritPouchItem;
import dev.sterner.malum.common.registry.MalumAttributeRegistry;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SpiritHarvestHandler {


	public static void shatterSoul(DamageSource source, LivingEntity target) {
		LivingEntity attacker = null;

		if (source.getAttacker() instanceof LivingEntity directAttacker) {
			attacker = directAttacker;
		}
		if (attacker == null) {
			attacker = target.getAttacker();
		}
		if (source.getSource() != null && source.isOf(MalumDamageSourceRegistry.VOODOO)) {
			SpiritHelper.createSpiritEntities(SpiritHelper.getSpiritItemStacks(target), target, null);
			return;
		}
		if (attacker != null) {
			ItemStack stack = attacker.getMainHandStack();
			if (source.getSource() instanceof ScytheBoomerangEntity scytheBoomerang) {
				stack = scytheBoomerang.scythe;
			}
			if (!(target instanceof PlayerEntity)) {
				var component = MalumComponents.SOUL_COMPONENT.get(target);
				if(component.getExposedSoulDuration() > 0 && !component.isSoulless() && (!MalumConfig.SOULLESS_SPAWNERS || (MalumConfig.SOULLESS_SPAWNERS && !component.isSpawnerSpawned()))) {
					SpiritHelper.createSpiritsFromWeapon(target, attacker, stack);
					component.setSoulless(true);
				}
			}
		}
	}

	public static void pickupSpirit(ItemStack stack, LivingEntity collector) {
		if (collector instanceof PlayerEntity playerEntity) {
			TrinketsApi.getTrinketComponent(playerEntity).orElseThrow().forEach((slot, trinket) -> {
				EntityAttributeInstance instance = playerEntity.getAttributeInstance(MalumAttributeRegistry.ARCANE_RESONANCE);
				if (trinket.getItem() instanceof SpiritCollectActivity spiritCollectActivity) {
					spiritCollectActivity.collect(stack, playerEntity, slot, trinket, instance != null ? instance.getValue() : 0);
				}
			});
			for (DefaultedList<ItemStack> playerInventory : playerEntity.getInventory().combinedInventory) {
				for (ItemStack item : playerInventory) {
					if (item.getItem() instanceof SpiritPouchItem) {
						DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
						NbtCompound nbt = item.getOrCreateNbt();
						if (nbt != null) {
							Inventories.readNbt(nbt, stacks);
						}
						SimpleInventory inventory = new SimpleInventory(stacks.delegate.toArray(ItemStack[]::new));

						ItemStack newStack = inventory.addStack(stack);
						if (!newStack.isEmpty()) {
							ItemHelper.giveItemToEntity(newStack, collector);
						}

						Inventories.writeNbt(nbt, inventory.stacks);

						World world = playerEntity.getWorld();
						world.playSound(null, playerEntity.getX(), playerEntity.getY() + 0.5, playerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
						return;
					}
				}
			}
		}
		ItemHelper.giveItemToEntity(stack, collector);
	}
}

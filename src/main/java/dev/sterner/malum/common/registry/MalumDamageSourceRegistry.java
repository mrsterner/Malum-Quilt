package dev.sterner.malum.common.registry;

import dev.sterner.malum.Malum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface MalumDamageSourceRegistry {
	RegistryKey<DamageType> VOODOO = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Malum.id("voodoo"));
	RegistryKey<DamageType> MAGEBANE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Malum.id("magebane"));
	RegistryKey<DamageType> SCYTHE_SWEEP = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Malum.id("scythe_sweep"));
	RegistryKey<DamageType> GUARANTEED_SOUL_SHATTER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Malum.id("soul_strike"));

	static DamageSource create(World world, RegistryKey<DamageType> key, @Nullable Entity source, @Nullable Entity attacker) {
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), source, attacker);
	}

	static DamageSource create(World world, RegistryKey<DamageType> key, @Nullable Entity attacker) {
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), attacker);
	}

	static DamageSource create(World world, RegistryKey<DamageType> key) {
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
	}

	static void init(){

	}
}

package dev.sterner.malum.common.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import dev.sterner.malum.Malum;
import net.minecraft.entity.LivingEntity;

public class MalumComponents implements EntityComponentInitializer, WorldComponentInitializer {
	public static final ComponentKey<MalumPlayerComponent> PLAYER_COMPONENT = ComponentRegistry.getOrCreate(Malum.id("player"), MalumPlayerComponent.class);
	public static final ComponentKey<SpiritLivingEntityComponent> SPIRIT_COMPONENT = ComponentRegistry.getOrCreate(Malum.id("spirit"), SpiritLivingEntityComponent.class);
	public static final ComponentKey<TouchOfDarknessComponent> TOUCH_OF_DARKNESS_COMPONENT = ComponentRegistry.getOrCreate(Malum.id("touch_of_darkness"), TouchOfDarknessComponent.class);

	public static final ComponentKey<SoulDataComponent> SOUL_COMPONENT = ComponentRegistry.getOrCreate(Malum.id("soul"), SoulDataComponent.class);


	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(LivingEntity.class, SPIRIT_COMPONENT).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(SpiritLivingEntityComponent::new);
		registry.beginRegistration(LivingEntity.class, PLAYER_COMPONENT).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(MalumPlayerComponent::new);
		registry.beginRegistration(LivingEntity.class, TOUCH_OF_DARKNESS_COMPONENT).respawnStrategy(RespawnCopyStrategy.LOSSLESS_ONLY).end(TouchOfDarknessComponent::new);
		registry.beginRegistration(LivingEntity.class, SOUL_COMPONENT).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(SoulDataComponent::new);

	}

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {

	}
}

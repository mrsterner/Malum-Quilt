package dev.sterner.malum.common.registry;

import dev.sterner.lodestone.systems.worldevent.WorldEventType;
import dev.sterner.malum.common.event.world.ActiveBlightEvent;
import dev.sterner.malum.common.event.world.TotemCreatedBlightEvent;

import static dev.sterner.lodestone.setup.worldevent.LodestoneWorldEventTypeRegistry.registerEventType;

public class WorldEventTypes {
	public static WorldEventType ACTIVE_BLIGHT = registerEventType(new WorldEventType("active_blight", ActiveBlightEvent::new));
	public static WorldEventType TOTEM_CREATED_BLIGHT = registerEventType(new WorldEventType("totem_blight", TotemCreatedBlightEvent::new));

}

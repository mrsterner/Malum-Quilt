package dev.sterner.malum.common.registry;

import dev.sterner.malum.common.screen.SpiritPouchScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

import static dev.sterner.malum.Malum.MODID;

public interface MalumScreenHandlerRegistry {
    Map<Identifier, ScreenHandlerType<? extends ScreenHandler>> SCREEN_HANDLERS = new LinkedHashMap<>();

    ScreenHandlerType<SpiritPouchScreenHandler> SPIRIT_POUCH_SCREEN_HANDLER = register("spirit_pouch", SpiritPouchScreenHandler::new);

	static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
		ScreenHandlerType<T> screenHandlerType = new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES);
		SCREEN_HANDLERS.put(new Identifier(MODID, id), screenHandlerType);
		return screenHandlerType;
	}

	static void init() {
		SCREEN_HANDLERS.forEach((id, handler) -> Registry.register(Registries.SCREEN_HANDLER, id, handler));
	}
}

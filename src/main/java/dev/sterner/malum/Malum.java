package dev.sterner.malum;

import dev.sterner.malum.common.enchantment.ReboundEnchantment;
import dev.sterner.malum.common.event.MalumEvents;
import dev.sterner.malum.common.event.MalumItemGroupEvents;
import dev.sterner.malum.common.event.MalumTrinketEvents;
import dev.sterner.malum.common.reaping.ReapingDataReloadListener;
import dev.sterner.malum.common.registry.*;
import dev.sterner.malum.common.spirit.SpiritDataReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**TODO
 * Add all pridewears
 * Add config
 * Add boat and sign
 */
public class Malum implements ModInitializer {
	public static final Random RANDOM = Random.create();
	public static final Logger LOGGER = LoggerFactory.getLogger("Malum");
	public static final String MODID = "malum";

	@Override
	public void onInitialize() {
		MalumObjects.init();
		MalumSpiritTypeRegistry.init();
		MalumDamageSourceRegistry.init();

		MalumAttributeRegistry.init();
		MalumParticleRegistry.init();
		MalumEnchantmentRegistry.init();
		MalumSoundRegistry.init();


		MalumEntityRegistry.init();
		MalumBlockEntityRegistry.init();


		MalumStatusEffectRegistry.init();
		MalumTagRegistry.init();
		MalumRecipeTypeRegistry.init();
		MalumRecipeSerializerRegistry.init();
		MalumScreenHandlerRegistry.init();

		MalumRiteRegistry.init();
		MalumTrinketEvents.init();
		MalumItemGroupEvents.init();
		MalumEvents.init();

		MalumStructures.init();
		MalumFeatureRegistry.init();

		UseItemCallback.EVENT.register(ReboundEnchantment::onRightClickItem);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SpiritDataReloadListenerFabricImpl());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ReapingDataReloadListenerFabricImpl());

		DynamicRegistrySetupCallback.EVENT.register((registryView) -> {
			registryView.getOptional(RegistryKeys.CONFIGURED_FEATURE).ifPresent(configuredFeatures -> {
				MalumFeatureRegistry.init(registryView, configuredFeatures);
			});
		});
	}


	public static class SpiritDataReloadListenerFabricImpl extends SpiritDataReloadListener implements IdentifiableResourceReloadListener {
		@Override
		public Identifier getFabricId() {
			return new Identifier(MODID, "spirit_data");
		}
	}

	public static class ReapingDataReloadListenerFabricImpl extends ReapingDataReloadListener implements IdentifiableResourceReloadListener {
		@Override
		public Identifier getFabricId() {
			return new Identifier(MODID, "reaping_data");
		}
	}

	public static Identifier id(String name){
		return new Identifier(MODID, name);
	}
}

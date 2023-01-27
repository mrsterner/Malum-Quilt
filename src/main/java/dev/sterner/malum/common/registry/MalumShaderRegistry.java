package dev.sterner.malum.common.registry;

import com.mojang.blaze3d.vertex.VertexFormats;
import com.mojang.datafixers.util.Pair;
import com.sammy.lodestone.systems.rendering.ExtendedShader;
import com.sammy.lodestone.systems.rendering.ShaderHolder;
import dev.sterner.malum.Malum;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.resource.ResourceFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MalumShaderRegistry {
	public static List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shaderList;

	public static ShaderHolder TOUCH_OF_DARKNESS = new ShaderHolder("Speed", "Zoom", "Distortion", "Intensity", "Wibble");


	public static void init(ResourceFactory manager) throws IOException {
		shaderList = new ArrayList<>();
		registerShader(ExtendedShader.createShaderInstance(TOUCH_OF_DARKNESS, manager, Malum.id("touch_of_darkness"), VertexFormats.POSITION_COLOR_TEXTURE));
	}
	public static void registerShader(ExtendedShader extendedShaderInstance) {
		registerShader(extendedShaderInstance, (shader) -> ((ExtendedShader) shader).getHolder().setInstance((ExtendedShader) shader));
	}
	public static void registerShader(ShaderProgram shader, Consumer<ShaderProgram> onLoaded) {
		shaderList.add(Pair.of(shader, onLoaded));
	}
}

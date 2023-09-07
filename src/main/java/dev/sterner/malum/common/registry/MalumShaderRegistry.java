package dev.sterner.malum.common.registry;

import com.mojang.datafixers.util.Pair;
import dev.sterner.lodestone.systems.rendering.shader.ExtendedShader;
import dev.sterner.lodestone.systems.rendering.shader.ShaderHolder;
import dev.sterner.malum.Malum;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
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
		registerShader(ExtendedShader.createShaderProgram(TOUCH_OF_DARKNESS, manager, Malum.id("touch_of_darkness"), VertexFormats.POSITION_COLOR_TEXTURE));
	}
	public static void registerShader(ExtendedShader extendedShaderInstance) {
		registerShader(extendedShaderInstance, (shader) -> ((ExtendedShader) shader).getHolder().setInstance((ExtendedShader) shader));
	}
	public static void registerShader(ShaderProgram shader, Consumer<ShaderProgram> onLoaded) {
		shaderList.add(Pair.of(shader, onLoaded));
	}
}

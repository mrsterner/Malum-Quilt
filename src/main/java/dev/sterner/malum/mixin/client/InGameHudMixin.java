package dev.sterner.malum.mixin.client;

import dev.sterner.malum.common.component.TouchOfDarknessComponent;
import dev.sterner.malum.common.util.handler.SoulwardHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
final class InGameHudMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
	private void malum$renderArmorOverlay(MatrixStack matrices, CallbackInfo info) {
		SoulwardHandler.Client.renderSoulWard(matrices, this.client.getWindow());
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getSleepTimer()I"))
	private void malum$renderDarknessOverlay(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		TouchOfDarknessComponent.ClientOnly.renderDarknessVignette(matrices);
	}
}

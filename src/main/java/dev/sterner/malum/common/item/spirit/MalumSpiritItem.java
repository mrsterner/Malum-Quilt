package dev.sterner.malum.common.item.spirit;

import dev.sterner.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import dev.sterner.lodestone.systems.particle.screen.LodestoneScreenParticleTextureSheet;
import dev.sterner.lodestone.systems.particle.screen.ScreenParticleHolder;
import dev.sterner.lodestone.systems.particle.screen.base.ScreenParticle;
import dev.sterner.malum.api.interfaces.item.FloatingGlowItem;
import dev.sterner.malum.client.ParticleEffects;
import dev.sterner.malum.client.ScreenParticleEffects;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class MalumSpiritItem extends Item implements FloatingGlowItem, ParticleEmitterHandler.ItemParticleSupplier {
    public MalumSpiritType type;

    public MalumSpiritItem(Settings properties, MalumSpiritType type) {
        super(properties);
        this.type = type;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, java.util.List<Text> tooltip, TooltipContext context) {
        tooltip.add(type.getFlavourComponent(stack));
    }

    @Override
    public Color getColor() {
        return type.getColor();
    }

    @Override
    public Color getEndColor() {
        return type.getEndColor();
    }

    @Environment(EnvType.CLIENT)
	@Override
	public void spawnLateParticles(ScreenParticleHolder target, World level, float partialTick, ItemStack stack, float x, float y) {
		ScreenParticleEffects.spawnSpiritShardScreenParticles(target, type.getColor(), type.getEndColor(), stack, x, y);
	}
}

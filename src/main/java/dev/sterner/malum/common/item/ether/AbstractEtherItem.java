package dev.sterner.malum.common.item.ether;

import dev.sterner.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public abstract class AbstractEtherItem extends BlockItem implements DyeableItem, ParticleEmitterHandler.ItemParticleSupplier {
    public static final String FIRST_COLOR = "firstColor";
    public static final String SECOND_COLOR = "secondColor";
    public static final int DEFAULT_FIRST_COLOR = 15712278;
    public static final int DEFAULT_SECOND_COLOR = 4607909;

    public final boolean iridescent;

    public AbstractEtherItem(Block blockIn, Settings builder, boolean iridescent) {
        super(blockIn, builder);
        this.iridescent = iridescent;
    }

    public String colorLookup() {
        return iridescent ? SECOND_COLOR : FIRST_COLOR;
    }

    public int getSecondColor(ItemStack stack) {
        if (!iridescent) {
            return getFirstColor(stack);
        }
        NbtCompound tag = stack.getSubNbt("display");

        return tag != null && tag.contains(SECOND_COLOR, 99) ? tag.getInt(SECOND_COLOR) : DEFAULT_SECOND_COLOR;
    }

    public void setSecondColor(ItemStack stack, int color) {
        stack.getOrCreateSubNbt("display").putInt(SECOND_COLOR, color);
    }

    public int getFirstColor(ItemStack stack) {
        NbtCompound tag = stack.getSubNbt("display");
        return tag != null && tag.contains(FIRST_COLOR, 99) ? tag.getInt(FIRST_COLOR) : DEFAULT_FIRST_COLOR;
    }

    public void setFirstColor(ItemStack stack, int color) {
        stack.getOrCreateSubNbt("display").putInt(FIRST_COLOR, color);
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound tag = stack.getSubNbt("display");
        return tag != null && tag.contains(colorLookup(), 99) ? tag.getInt(colorLookup()) : DEFAULT_FIRST_COLOR;
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        NbtCompound tag = stack.getSubNbt("display");
        return tag != null && tag.contains(colorLookup(), 99);
    }

    @Override
    public void removeColor(ItemStack stack) {
        NbtCompound tag = stack.getSubNbt("display");
        if (tag != null && tag.contains(colorLookup())) {
            tag.remove(colorLookup());
        }
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        stack.getOrCreateSubNbt("display").putInt(colorLookup(), color);
    }
}

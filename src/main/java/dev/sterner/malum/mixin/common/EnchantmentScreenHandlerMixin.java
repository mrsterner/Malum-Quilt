package dev.sterner.malum.mixin.common;

import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static dev.sterner.malum.common.registry.MalumObjects.BRILLIANT_OBELISK;


@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {

    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyVariable(method = "method_17411", at = @At(value = "FIELD", target = "Lnet/minecraft/block/EnchantingTableBlock;POWER_PROVIDER_OFFSETS:Ljava/util/List;"), index = 4)
    private int malum$brilliantObeliskEnchantmentPower(int ix, ItemStack stack, World world, BlockPos pos) {
        return ix + 4 * (int) EnchantingTableBlock.POWER_PROVIDER_OFFSETS.stream().filter(blockPos -> isObelisk(world, pos, blockPos)).count();
    }

    @Unique
    private static boolean isObelisk(World world, BlockPos tablePos, BlockPos possiblePos) {
       return world.getBlockState(tablePos.add(possiblePos)).isOf(BRILLIANT_OBELISK)
           && world.isAir(tablePos.add(possiblePos.getX() / 2, possiblePos.getY(), possiblePos.getZ() / 2))
           && world.getBlockState(tablePos.add(possiblePos)).isOf(BRILLIANT_OBELISK);
    }


}

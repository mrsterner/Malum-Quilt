package dev.sterner.malum.common.item;

import dev.sterner.malum.client.screen.codex.ProgressionBookScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EncyclopediaArcanaItem extends Item {
    public EncyclopediaArcanaItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            ProgressionBookScreen.openScreen(true);
            user.swingHand(hand);
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }
}

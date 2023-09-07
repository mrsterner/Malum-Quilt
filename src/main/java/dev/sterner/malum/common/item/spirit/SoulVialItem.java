package dev.sterner.malum.common.item.spirit;

import dev.sterner.lodestone.helpers.ItemHelper;
import dev.sterner.malum.api.interfaces.item.SoulContainerItem;
import dev.sterner.malum.common.entity.spirit.SoulEntity;
import dev.sterner.malum.common.spirit.MalumEntitySpiritData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulVialItem extends BlockItem implements SoulContainerItem {
    public SoulVialItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public String getTranslationKey(ItemStack itemStack) {
        if (itemStack.hasNbt() && itemStack.getNbt().contains(MalumEntitySpiritData.SOUL_DATA)) {
            return "item.malum.filled_soul_vial";
        }
        return super.getTranslationKey();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()) {
            NbtCompound tag = stack.getNbt();
            if (tag.contains(MalumEntitySpiritData.SOUL_DATA)) {
                MalumEntitySpiritData data = MalumEntitySpiritData.load(tag);
                tooltip.add(Text.translatable("malum.spirit.description.stored_soul").formatted(Formatting.GRAY));
                tooltip.addAll(data.createTooltip());
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld) {
            return fetchSoul(user, hand);
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() != null) {
            ActionResult result = fetchSoul(context.getPlayer(), context.getHand()).getResult();
            if (result.equals(ActionResult.SUCCESS)) {
                return result;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        ItemStack otherStack = context.getPlayer().getStackInHand(context.getHand().equals(Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND);
        if (otherStack.getItem() instanceof SoulStaveItem) {
            return false;
        }
        return super.canPlace(context, state);
    }


	@Override
    public TypedActionResult<ItemStack> interactWithSoul(PlayerEntity player, Hand hand, SoulEntity soul) {
        ItemStack stack = player.getStackInHand(hand);
        if (!soul.spiritData.equals(MalumEntitySpiritData.EMPTY) && (!stack.hasNbt() || (stack.hasNbt() && !stack.getOrCreateNbt().contains(MalumEntitySpiritData.SOUL_DATA)))) {
            ItemStack split = stack.split(1);
            soul.spiritData.saveTo(split.getOrCreateNbt());
            soul.discard();
            ItemHelper.giveItemToEntity(split, player);
            player.swingHand(hand, true);
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.fail(player.getStackInHand(hand));
    }
}

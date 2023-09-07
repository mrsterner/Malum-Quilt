package dev.sterner.malum.common.item.spirit;

import dev.sterner.lodestone.systems.container.ItemInventory;
import dev.sterner.malum.common.screen.SpiritPouchScreenHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.stream.Stream;

import static dev.sterner.malum.common.registry.MalumScreenHandlerRegistry.SPIRIT_POUCH_SCREEN_HANDLER;


public class SpiritPouchItem extends Item {
    public SpiritPouchItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        Iterator<ItemStack> iter = new Iterator<>() {
            private int i = 0;
            private final ItemInventory inventory = getInventory(entity.getStack());

            @Override
            public boolean hasNext() {
                return i < inventory.size();
            }

            @Override
            public ItemStack next() {
                return inventory.getStack(i++);
            }
        };

        ItemUsage.spawnItemContents(entity, Stream.iterate(iter.next(), t -> iter.hasNext(), t -> iter.next()));
    }

    @Override
    public boolean onStackClicked(ItemStack thisStack, Slot otherSlot, ClickType clickType, PlayerEntity player) {
        ItemStack stack = otherSlot.getStack();

        if (clickType != ClickType.RIGHT || !(stack.getItem() instanceof MalumSpiritItem)) {
            return false;
        } else {
            ItemInventory inventory = getInventory(thisStack);

            if (!stack.isEmpty() && stack.getItem().canBeNested()) {
                ItemStack toInsert = otherSlot.takeStackRange(stack.getCount(), Integer.MAX_VALUE, player);
                ItemStack remainder = inventory.addStack(toInsert);
                otherSlot.setStack(remainder);
                if (remainder.getCount() != toInsert.getCount())
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
            }

            return true;
        }
    }

    @Override
    public boolean onClicked(ItemStack thisStack, ItemStack otherStack, Slot thisSlot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !thisSlot.canTakePartial(player) || !(otherStack.getItem() instanceof MalumSpiritItem)) {
            return false;
        } else {
            ItemInventory inventory = getInventory(thisStack);

            if (!otherStack.isEmpty() && otherStack.getItem().canBeNested()) {
                ItemStack remainder = inventory.addStack(otherStack.copy());
                if (otherStack.getCount() != remainder.getCount())
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);

                otherStack.decrement(otherStack.getCount() - remainder.getCount());
            }

            return true;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) {
            player.swingHand(hand);
            return TypedActionResult.success(player.getStackInHand(hand));
        } else {
            player.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return Text.empty();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
                    NbtCompound nbt = player.getStackInHand(hand).getNbt();
                    ItemStack stack = player.getStackInHand(hand);
                    if (nbt != null) {
                        Inventories.readNbt(nbt, stacks);
                    }
                    return new SpiritPouchScreenHandler(SPIRIT_POUCH_SCREEN_HANDLER, syncId, playerInventory, new SimpleInventory(stacks.toArray(ItemStack[]::new)), stack, playerInventory.getSlotWithStack(stack));
                }
            });
            return super.use(world, player, hand);
        }
    }

    public static ItemInventory getInventory(ItemStack stack) {
        return new ItemInventory(stack, 27) {
            @Override
            public boolean isValid(int pIndex, ItemStack pStack) {
                return pStack.getItem() instanceof MalumSpiritItem;
            }
        };
    }
}

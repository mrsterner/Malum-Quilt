package dev.sterner.malum.common.blockentity.storage;

import com.sammy.lodestone.forge.ItemHandler;
import com.sammy.lodestone.forge.LazyOptional;
import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.setup.LodestoneParticles;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntity;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PlayerLookup;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;


public class SpiritJarBlockEntity extends LodestoneBlockEntity {
    public MalumSpiritType type;
    public int count;

    // Storage Drawers moment
    private long lastClickTime;
    private UUID lastClickUUID;

    public SpiritJarBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.SPIRIT_JAR, pos, state);
    }

    public SpiritJarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public final LazyOptional<ItemHandler> inventory = LazyOptional.of(() -> new ItemHandler() {
        @Override
        public int size() {
            return 2;
        }

        @Override
        public @NotNull ItemStack getStack(int slot) {
            if (slot == 0 && type != null) {
                return new ItemStack(type.getSplinterItem(), count); // Yes, this can create a stack bigger than 64. It's fine.
            } else
                return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItemStack(int slot, ItemStack itemStack, boolean simulate) {
            if (slot == 1 && itemStack.getItem() instanceof MalumSpiritItem spiritItem && (type == null || spiritItem.type == type)) {
                if (!simulate) {
                    if (type == null)
                        type = spiritItem.type;
                    count += itemStack.getCount();
                    if (!world.isClient) {
                        BlockHelper.updateAndNotifyState(world, pos);
                    }
                }
                return ItemStack.EMPTY;
            }
            return itemStack;
        }

        @Override
        public ItemStack extractItemStack(int slot, int amount, boolean simulate) {
            if (slot != 0 || count <= 0)
                return ItemStack.EMPTY;
            MalumSpiritType extractedType = type;
            if (extractedType == null)
                return ItemStack.EMPTY;

            int amountToExtract = Math.min(count, amount);
            if (!simulate) {
                count -= amountToExtract;
                if (count == 0) {
                    type = null;
                }
                if (!world.isClient()) {
                    BlockHelper.updateAndNotifyState(world, pos);
                }
            }

            return new ItemStack(extractedType.getSplinterItem(), amountToExtract);
        }

        @Override
        public int getMaxCountForSlot(int slot) {
            if (slot == 0)
                return Math.min(64, count);
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof MalumSpiritItem spiritItem && spiritItem.type == type;
        }
    });

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    public void clientTick(World world1, BlockPos pos, BlockState state1) {
    }



    /*
    @Override
    public boolean canPush(ItemStack stack) {
        return !stack.isEmpty() && stack.getCount() <= getRemainingSpace()
            && (getItem() == Items.AIR || stack.getItem().equals(getItem()))
            && stack.getItem() instanceof MalumSpiritItem;
    }
    public Item getItem() {
        return items.size() > 0 ? items.get(0).getItem() : Items.AIR;
    }
    private int getRemainingSpace(){
        return 2147483647 - this.getSize();
    }
    public int getSize() {
        int size = 0;
        if (items.size() > 0) for(ItemStack stack : this.items) {
            size += stack.getCount();
        }
        return size;
    }
    @Override
    public void pushStack(ItemStack stack) {
        for (ItemStack s : items) {
            if (s != null && ItemStack.canCombine(s, stack) && s.getCount() < s.getMaxCount()) {
                int diff = s.getMaxCount() - s.getCount();
                int available = stack.getCount();
                int toAdd = Math.min(diff, available);
                s.increment(toAdd);
                stack.decrement(toAdd);
                this.markDirty();
            }
        }
        if(!stack.isEmpty()) {
            items.add(0, stack);
            this.markDirty();
        }
    }

    @Override
    public ItemStack takeStack() {
        if (items.size() > 1 && ItemStack.canCombine(getStack(0), getStack(1))) { //if the top two stacks are combinable
            return takeStack(getItem().getMaxCount()); //grab a full stack
        } else if (items.size() < 1) {
            return ItemStack.EMPTY;
        } else {
            return takeStack(items.get(0).getCount()); //otherwise grab the top stack
        }
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (items.size() < 1) {
            return ItemStack.EMPTY;
        }
        ItemStack top = getStack(0);
        ItemStack extracted = top.split(amount); //grab items from the top stack
        if(top.isEmpty()) {
            items.remove(0);
        }
        if(extracted.getCount() < amount && ItemStack.canCombine(extracted, getStack(0))) { //if there's room for more...
            int diff = amount - extracted.getCount();
            items.get(0).decrement(diff); //take the difference from the next stack
            if(items.get(0).getCount() == 0) { //and delete that stack if it's emptied, though that should never happen
                items.remove(0);
            }
            extracted.increment(diff); //then add those items to the new stack
        }

        this.markDirty();
        return extracted;
    }

    public void clientTick(World world, BlockPos pos, BlockState state) {
        if (this.getItem() instanceof MalumSpiritItem item) {
            Vec3d vec = DataHelper.fromBlockPos(pos).add(new Vec3d(0.5f, 0.5f, 0.5f));
            double x = vec.x;
            double y = vec.y + Math.sin(world.getTime() / 20f) * 0.1f;
            double z = vec.z;
            SpiritHelper.spawnSpiritParticles(world, x, y, z, item.type.color, item.type.endColor);
        }
    }

    @Override
    public World getWorld() {
        return super.getWorld();
    }

    @Override
    public BlockPos getPos() {
        return super.getPos();
    }

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.clear();
        NbtList list = nbt.getList("spirits", NbtElement.COMPOUND_TYPE);
        for(NbtElement e : list) {
            ItemStack stack = ItemStack.fromNbt((NbtCompound) e);
            this.pushStack(stack);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for(ItemStack stack : items){
            NbtCompound c = new NbtCompound();
            stack.writeNbt(c);
            list.add(0, c);
        }
        nbt.put("spirits", list);
    }

    @Override
    public NbtCompound toSyncedNbt() {
        NbtCompound tag = super.toSyncedNbt();
        writeNbt(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.of(this);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getStackInHand(hand);
        MalumSpiritType type = this.getItem() instanceof MalumSpiritItem sp ? sp.type : null;
        Item heldItem = heldStack.getItem();
        if (heldItem instanceof SpiritPouchItem) {
            if (type != null) {
                DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
                NbtCompound nbt = heldStack.getOrCreateNbt();
                if (nbt != null) {
                    Inventories.readNbt(nbt, stacks);
                }
                SimpleInventory inventory = new SimpleInventory(stacks.delegate.toArray(ItemStack[]::new));
                boolean successful = false;
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.getStack(i);
                    if (stack.getItem() instanceof MalumSpiritItem malumSpiritItem) {
                        MalumSpiritType spType = malumSpiritItem.type;
                        if(spType.id.equals(type.id)) {
                            if(this.tryPush(stack)) {
                                type = spType;
                                inventory.setStack(i, ItemStack.EMPTY);
                            }
                            successful = true;
                        }
                    }
                }
                Inventories.writeNbt(nbt, inventory.stacks);
                if (successful) {
                    if (player.world.isClient) {
                        spawnUseParticles(world, pos, type);
                    }
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS;
                }
            }
        }
        else if (heldItem instanceof MalumSpiritItem spiritSplinterItem) {
            if (type == null || type.equals(spiritSplinterItem.type)) {
                type = spiritSplinterItem.type;
                if (this.tryPush(heldStack.copy())) {
                    heldStack.decrement(heldStack.getCount());
                }
                if (!player.world.isClient) {
                    BlockHelper.updateAndNotifyState(world, pos);
                } else {
                    spawnUseParticles(world, pos, type);
                }
                return ActionResult.SUCCESS;
            }
        } else if(type != null) {
            ItemStack grabbed = this.takeStack();
            System.out.println(player.isSneaking());
            if (!player.world.isClient) {
                if(grabbed != null) {
                    if(heldStack.isEmpty()){
                        player.setStackInHand(hand, grabbed);
                    } else{
                        player.getInventory().offerOrDrop(grabbed);
                    }
                    return ActionResult.SUCCESS;
                }
            } else {
                spawnUseParticles(world, pos, type);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    private void comparatorUpdate() {
        if(this.world != null && !this.world.isClient) {
            this.world.updateComparators(pos, this.getCachedState().getBlock());
        }
    }
    @Override
    public void markDirty() {
        if(world == null || world.isClient) {return;}
        comparatorUpdate();
        PlayerLookup.tracking(this).forEach(player -> player.networkHandler.sendPacket(toUpdatePacket()));
        super.markDirty();
    }
    public void spawnUseParticles(World world, BlockPos pos, MalumSpiritType type) {
        Color color = type.color;
        ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
                .setAlpha(0.15f, 0f)
                .setLifetime(20)
                .setScale(0.3f, 0)
                .setSpin(0.2f)
                .randomMotion(0.02f)
                .randomOffset(0.1f, 0.1f)
                .setColor(color, color.darker())
                .enableNoClip()
                .repeat(world, pos.getX()+0.5f, pos.getY()+0.5f + Math.sin(world.getTime() / 20f) * 0.2f, pos.getZ()+0.5f, 10);
    }

    @Override
    public void clear() {
        items.clear();
    }

     */
}

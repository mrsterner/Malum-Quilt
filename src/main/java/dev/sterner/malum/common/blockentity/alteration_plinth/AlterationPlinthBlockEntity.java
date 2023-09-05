package dev.sterner.malum.common.blockentity.alteration_plinth;

import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import dev.sterner.malum.common.blockentity.storage.ItemPedestalBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.block.blight.BlightTransformItemParticlePacket;
import dev.sterner.malum.common.recipe.AugmentingRecipe;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static dev.sterner.malum.common.registry.MalumSpiritTypeRegistry.ARCANE_SPIRIT;


public class AlterationPlinthBlockEntity extends ItemPedestalBlockEntity {
    public AlterationPlinthBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        inventory = new LodestoneBlockEntityInventory(1, 1) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(world, pos);
            }
        };
    }

    @Override
    public Vec3d itemOffset() {
        return new Vec3d(0.5f, 1.5f, 0.5f);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand) {
        ItemStack target = inventory.getStack(0);
        if (!target.isEmpty()) {
            ItemStack applied = player.getStackInHand(hand);
            AugmentingRecipe recipe = AugmentingRecipe.getRecipe(world, target, applied);
            if (recipe != null) {
                if (!world.isClient()) {
                    var random = world.random;
                    NbtCompound tag = target.getOrCreateNbt();
                    NbtCompound modifiedTag = tag.copy().copyFrom(recipe.tagAugment);
                    if (tag.equals(modifiedTag)) {
                        return ActionResult.FAIL;
                    }
                    target.setNbt(modifiedTag);
                    Vec3d pos = getItemPos();
                    ItemEntity pEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), target);
                    pEntity.setVelocity(MathHelper.nextFloat(random, -0.1F, 0.1F), MathHelper.nextFloat(random, 0.25f, 0.5f), MathHelper.nextFloat(random, -0.1F, 0.1F));
                    if(world instanceof ServerWorld serverWorld){
                        PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(getPos()).getPos()).forEach(track -> BlightTransformItemParticlePacket.send(track, List.of(ARCANE_SPIRIT.identifier), pos.x, pos.y, pos.z));
                    }
					world.playSound(null, new BlockPos((int)pos.x ,(int)pos.y ,(int)pos.z), MalumSoundRegistry.ALTERATION_PLINTH_ALTERS, SoundCategory.BLOCKS, 1, 0.9f + world.random.nextFloat() * 0.25f);

                    world.spawnEntity(pEntity);
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                    if (!player.isCreative()) {
                        applied.decrement(1);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(player, hand);
    }
}

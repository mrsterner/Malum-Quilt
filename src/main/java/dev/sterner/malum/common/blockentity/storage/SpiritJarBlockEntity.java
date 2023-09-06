package dev.sterner.malum.common.blockentity.storage;

import com.sammy.lodestone.forge.ItemHandler;
import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntity;
import com.sammy.lodestone.systems.container.ItemInventory;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.malum.client.ParticleEffects;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.item.spirit.SpiritPouchItem;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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
        public int getSlotLimit(int slot) {
            if (slot == 0)
                return Math.min(64, count);
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof MalumSpiritItem spiritItem && spiritItem.type == type;
        }
    });



	@Override
	public ActionResult onUse(PlayerEntity player, Hand hand) {
		if (getWorld() == null)
			return ActionResult.PASS;

		int count;
		if (getWorld().getTime() - lastClickTime < 10 && player.getUuid().equals(lastClickUUID))
			count = insertAllSpirits(player);
		else
			count = insertHeldItem(player);

		lastClickTime = getWorld().getTime();
		lastClickUUID = player.getUuid();

		if (count != 0) {
			if (player.getWorld().isClient) {
				spawnUseParticles(world, pos, type);
			} else {
				BlockHelper.updateAndNotifyState(world, pos);
			}
		}

		return ActionResult.success(player.getWorld().isClient);
	}

	public int insertHeldItem(PlayerEntity player) {
		int count = 0;
		ItemStack playerStack = player.getInventory().getMainHandStack();
		if (!playerStack.isEmpty())
			count = insertFromStack(playerStack);

		return count;
	}

	public int insertAllSpirits(PlayerEntity player) {
		if (type == null)
			return 0;

		int count = 0;
		for (int i = 0, n = player.getInventory().size(); i < n; i++) {
			ItemStack subStack = player.getInventory().getStack(i);
			if (!subStack.isEmpty()) {
				int subCount = insertFromStack(subStack);
				if (subCount > 0 && subStack.getCount() == 0)
					player.getInventory().setStack(i, ItemStack.EMPTY);

				count += subCount;
			}
		}

		return count;
	}

	public int insertFromStack(ItemStack stack) {
		int inserted = 0;
		if (stack.getItem() instanceof SpiritPouchItem) {
			if (type != null) {
				ItemInventory inventory = SpiritPouchItem.getInventory(stack);
				for (int i = 0; i < inventory.size(); i++) {
					ItemStack spiritStack = inventory.getStack(i);
					if (spiritStack.getItem() instanceof MalumSpiritItem spiritItem) {
						MalumSpiritType type = spiritItem.type;
						if (type.identifier.equals(this.type.identifier)) {
							inventory.setStack(i, ItemStack.EMPTY);
							inserted += spiritStack.getCount();
							count += spiritStack.getCount();
						}
					}
				}
			}
		} else if (stack.getItem() instanceof MalumSpiritItem spiritSplinterItem) {
			if (type == null || type.equals(spiritSplinterItem.type)) {
				type = spiritSplinterItem.type;
				inserted += stack.getCount();
				count += stack.getCount();
				stack.decrement(stack.getCount());
			}
		}
		return inserted;

	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onPlace(LivingEntity placer, ItemStack stack) {
		if (stack.hasNbt()) {
			readNbt(stack.getNbt());
		}
		markDirty();
	}

	@Override
	protected void writeNbt(NbtCompound compound) {
		if (type != null) {
			compound.putString("spirit", type.identifier);
		}
		compound.putInt("count", count);
	}

	@Override
	public void readNbt(@NotNull NbtCompound compound) {
		if (compound.contains("spirit")) {
			type = SpiritHelper.getSpiritType(compound.getString("spirit"));
		} else {
			type = null;
		}
		count = compound.getInt("count");
		super.readNbt(compound);
	}

	@Override
	public void tick() {
		if (world.isClient) {
			if (type != null) {
				double x = getPos().getX() + 0.5f;
				double y = getPos().getY() + 0.5f + Math.sin(world.getTime() / 20f) * 0.2f;
				double z = getPos().getZ() + 0.5f;
				ParticleEffects.spawnSpiritGlimmerParticles(world, x, y, z, type.getColor(), type.getEndColor());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void spawnUseParticles(World world, BlockPos pos, MalumSpiritType type) {
		Color color = type.getColor();
		WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
				.setTransparencyData(GenericParticleData.create(0.15f, 0f).build())
				.setScaleData(GenericParticleData.create(0.3f, 0).build())
				.setSpinData(SpinParticleData.create(0.2f).build())
				.setColorData(ColorParticleData.create(color, color.darker()).build())
				.setLifetime(20)
				.setRandomMotion(0.02f)
				.setRandomOffset(0.1f, 0.1f)
				.enableNoClip()
				.repeat(world, pos.getX() + 0.5f, pos.getY() + 0.5f + Math.sin(world.getTime() / 20f) * 0.2f, pos.getZ() + 0.5f, 10);
	}
}

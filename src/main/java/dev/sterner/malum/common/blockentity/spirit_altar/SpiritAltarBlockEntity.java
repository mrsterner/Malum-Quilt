package dev.sterner.malum.common.blockentity.spirit_altar;

import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.helpers.DataHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntity;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.SimpleParticleEffect;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import com.sammy.lodestone.systems.recipe.IngredientWithCount;
import dev.sterner.malum.client.ParticleEffects;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.network.packet.s2c.block.functional.AltarConsumeParticlePacket;
import dev.sterner.malum.common.network.packet.s2c.block.functional.AltarCraftParticlePacket;
import dev.sterner.malum.common.recipe.SpiritInfusionRecipe;
import dev.sterner.malum.common.recipe.SpiritWithCount;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class SpiritAltarBlockEntity extends LodestoneBlockEntity {
    private static final int HORIZONTAL_RANGE = 4;
    private static final int VERTICAL_RANGE = 3;
	public boolean needsSync;
	public float speed;
    public int progress;
    public float spinUp;

    public List<BlockPos> acceleratorPositions = new ArrayList<>();
    public List<IAltarAccelerator> accelerators = new ArrayList<>();
    public float spiritAmount;
    public float spiritSpin;
    public boolean isCrafting;

    public LodestoneBlockEntityInventory inventory;
    public LodestoneBlockEntityInventory extrasInventory;
    public LodestoneBlockEntityInventory spiritInventory;
    public List<SpiritInfusionRecipe> possibleRecipes = new ArrayList<>();
    public SpiritInfusionRecipe recipe;

    public SpiritAltarBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.SPIRIT_ALTAR, pos, state);
    }

    public SpiritAltarBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
        inventory = new LodestoneBlockEntityInventory(1, 64, t -> !(t.getItem() instanceof MalumSpiritItem)) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                needsSync = true;
                BlockHelper.updateAndNotifyState(world, pos);
            }
        };
        extrasInventory = new LodestoneBlockEntityInventory(8, 1) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(world, pos);
            }
        };
        spiritInventory = new LodestoneBlockEntityInventory(MalumSpiritTypeRegistry.SPIRITS.size(), 64) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                needsSync = true;
                spiritAmount = Math.max(1, MathHelper.lerp(0.15f, spiritAmount, nonEmptyItemAmount + 1));
                BlockHelper.updateAndNotifyState(world, pos);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (!(stack.getItem() instanceof MalumSpiritItem spiritItem))
                    return false;

                for (int i = 0; i < size(); i++) {
                    if (i != slot) {
                        ItemStack stackInSlot = getStack(i);
                        if (!stackInSlot.isEmpty() && stackInSlot.getItem() == spiritItem)
                            return false;
                    }
                }
                return true;
            }
        };
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        if (progress != 0) {
            compound.putInt("progress", progress);
        }
        if (spinUp != 0) {
            compound.putFloat("spinUp", spinUp);
        }
        if (speed != 0) {
            compound.putFloat("speed", speed);
        }
        if (spiritAmount != 0) {
            compound.putFloat("spiritAmount", spiritAmount);
        }
        if (!acceleratorPositions.isEmpty()) {
            compound.putInt("acceleratorAmount", acceleratorPositions.size());
            for (int i = 0; i < acceleratorPositions.size(); i++) {
                BlockHelper.saveBlockPos(compound, acceleratorPositions.get(i), "" + i);
            }
        }

        inventory.save(compound);
        spiritInventory.save(compound, "spiritInventory");
        extrasInventory.save(compound, "extrasInventory");
    }

    @Override
    public void readNbt(NbtCompound compound) {
		needsSync = true;
        progress = compound.getInt("progress");
        spinUp = compound.getFloat("spinUp");
        speed = compound.getFloat("speed");
        spiritAmount = compound.getFloat("spiritAmount");

        acceleratorPositions.clear();
        accelerators.clear();
        int amount = compound.getInt("acceleratorAmount");
        for (int i = 0; i < amount; i++) {
            BlockPos pos = BlockHelper.loadBlockPos(compound, "" + i);
            if (world != null && world.getBlockEntity(pos) instanceof IAltarAccelerator accelerator) {
                acceleratorPositions.add(pos);
                accelerators.add(accelerator);
            }
        }
        inventory.load(compound);
        spiritInventory.load(compound, "spiritInventory");
        extrasInventory.load(compound, "extrasInventory");
        super.readNbt(compound);
    }


    @Override
    public void onBreak(@Nullable PlayerEntity player) {
        inventory.dumpItems(world, pos);
        spiritInventory.dumpItems(world, pos);
        extrasInventory.dumpItems(world, pos);
    }

    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand){
        if (hand.equals(Hand.MAIN_HAND)) {
            ItemStack heldStack = player.getMainHandStack();
            recalibrateAccelerators();

            if (!(heldStack.getItem() instanceof MalumSpiritItem)) {
                ItemStack stack = inventory.interact(world, player, hand);
				this.notifyListeners();
                if (!stack.isEmpty()) {
                    return ActionResult.SUCCESS;
                }
            }
            spiritInventory.interact(world, player, hand);
			this.notifyListeners();
            if (heldStack.isEmpty()) {
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        }
        return super.onUse(player, hand);
    }

	private void notifyListeners() {
		markDirty();

		if (getWorld() != null && !getWorld().isClient) {
			getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
		}
	}

    @Override
    public void init() {
        ItemStack stack = inventory.getStack(0);
        possibleRecipes = new ArrayList<>(DataHelper.getAll(SpiritInfusionRecipe.getRecipes(world), r -> r.doesInputMatch(stack) && r.doSpiritsMatch(spiritInventory.nonEmptyItemStacks)));
        recipe = SpiritInfusionRecipe.getRecipe(world, stack, spiritInventory.nonEmptyItemStacks);
        if (world.isClient && !possibleRecipes.isEmpty() && !isCrafting) {
            AltarSoundInstance.playSound(this);
        }
    }

	@Override
    public void tick() {
		if (needsSync) {
			init();
			needsSync = false;
		}

        super.tick();
        spiritAmount = Math.max(1, MathHelper.lerp(0.1f, spiritAmount, spiritInventory.nonEmptyItemAmount));
        if (!possibleRecipes.isEmpty()) {

            if (spinUp < 30) {
                spinUp++;
            }
            isCrafting = true;
            progress++;
            if (!world.isClient) {
                if (world.getTime() % 20L == 0) {
                    boolean canAccelerate = accelerators.stream().allMatch(IAltarAccelerator::canAccelerate);
                    if (!canAccelerate) {
                        recalibrateAccelerators();
                    }
                }
                int progressCap = (int) (300 * (1 - Math.log(1+speed)/4f));
                if (progress >= progressCap) {
                    boolean success = consume();
                    if (success) {
                        craft();
                    }
                }
            }
        } else {
            isCrafting = false;
            progress = 0;
            if (spinUp > 0) {
                float spinUp = 0.1f + Easing.QUAD_OUT.ease(Math.min(1, this.spinUp/10f), 0, 1, 1)*1.4f;
                this.spinUp -= spinUp;
            }
        }
        if (world.isClient) {
            spiritSpin += 1 + spinUp / 15f + speed / 8f;
            passiveParticles();
        }
    }

    public static Vec3d getItemPos(SpiritAltarBlockEntity blockEntity) {
        return BlockHelper.fromBlockPos(blockEntity.getPos()).add(blockEntity.itemOffset());
    }

    public Vec3d itemOffset() {
        return new Vec3d(0.5f, 1.25f, 0.5f);
    }

    public Vec3d getSpiritOffset(int slot, float partialTicks) {
        float distance = 1 - getSpinUp(Easing.SINE_OUT) * 0.25f + (float) Math.sin((spiritSpin + partialTicks) / 20f) * 0.025f;
        float height = 0.75f + getSpinUp(Easing.QUARTIC_OUT) * getSpinUp(Easing.BACK_OUT) * 0.5f;
        return DataHelper.rotatingRadialOffset(new Vec3d(0.5f, height, 0.5f), distance, slot, spiritAmount, (long) (spiritSpin + partialTicks), 360);
    }

    public float getSpinUp(Easing easing) {
        if (spinUp > 30) {
            return 1;
        }
        return easing.ease(spinUp / 30f, 0, 1, 1);
    }
    public boolean consume() {
        Vec3d itemPos = getItemPos(this);
        if (recipe.extraItems.isEmpty()) {
            return true;
        }
        extrasInventory.updateData();
        int extras = extrasInventory.nonEmptyItemAmount;
        if (extras < recipe.extraItems.size()) {
            progress *= 0.8f;
            Collection<IAltarProvider> altarProviders = BlockHelper.getBlockEntities(IAltarProvider.class, world, pos, HORIZONTAL_RANGE, VERTICAL_RANGE, HORIZONTAL_RANGE);
            for (IAltarProvider provider : altarProviders) {
                LodestoneBlockEntityInventory inventoryForAltar = provider.getInventoryForAltar();
                ItemStack providedStack = inventoryForAltar.getStack(0);
                IngredientWithCount requestedItem = recipe.extraItems.get(extras);
                boolean matches = requestedItem.matches(providedStack);
                if (!matches) {
                    for (SpiritInfusionRecipe recipe : possibleRecipes) {
                        if (extras < recipe.extraItems.size() && recipe.extraItems.get(extras).matches(providedStack)) {
                            this.recipe = recipe;
                            break;
                        }
                    }
                }
                requestedItem = recipe.extraItems.get(extras);
                matches = requestedItem.matches(providedStack);
                if (matches) {
                    world.playSound(null, provider.getBlockPosForAltar(), MalumSoundRegistry.ALTAR_CONSUME, SoundCategory.BLOCKS, 1, 0.9f + world.random.nextFloat() * 0.2f);
                    Vec3d providedItemPos = provider.getItemPosForAltar();
                    if(world instanceof ServerWorld serverWorld){
                        PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(provider.getBlockPosForAltar()).getPos()).forEach(track -> AltarConsumeParticlePacket.send(track, recipe.spirits.stream().map(s -> s.type.identifier).collect(Collectors.toList()), providedItemPos.x, providedItemPos.y, providedItemPos.z, itemPos.x, itemPos.y, itemPos.z));
                    }
                    extrasInventory.insertItem(world, providedStack.split(requestedItem.count));
                    inventoryForAltar.updateData();
                    BlockHelper.updateAndNotifyState(world, provider.getBlockPosForAltar());
                    break;
                }
            }
            return false;
        }
        return true;
    }

    public void craft() {
        ItemStack stack = inventory.getStack(0);
        ItemStack outputStack = recipe.output.copy();
        Vec3d itemPos = getItemPos(this);
        if (inventory.getStack(0).hasNbt()) {
            outputStack.setNbt(stack.getNbt());
        }
        stack.decrement(recipe.input.count);
        inventory.updateData();
        for (SpiritWithCount spirit : recipe.spirits) {
            for (int i = 0; i < spiritInventory.slotCount; i++) {
                ItemStack spiritStack = spiritInventory.getStack(i);
                if (spirit.matches(spiritStack)) {
                    spiritStack.decrement(spirit.count);
                    break;
                }
            }
        }
        spiritInventory.updateData();
        if(world instanceof ServerWorld serverWorld){
            PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> AltarCraftParticlePacket.send(track, recipe.spirits.stream().map(s -> s.type.identifier).collect(Collectors.toList()), itemPos));
        }
        progress *= 0.5f;
        extrasInventory.clear();
        world.playSound(null, pos, MalumSoundRegistry.ALTAR_CRAFT, SoundCategory.BLOCKS, 1, 0.9f + world.random.nextFloat() * 0.2f);
        world.spawnEntity(new ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, outputStack));
        init();
        recalibrateAccelerators();
        BlockHelper.updateAndNotifyState(world, pos);
    }

    public void recalibrateAccelerators() {
        speed = 0f;
        accelerators.clear();
        acceleratorPositions.clear();
        Collection<IAltarAccelerator> nearbyAccelerators = BlockHelper.getBlockEntities(IAltarAccelerator.class, world, pos, HORIZONTAL_RANGE, VERTICAL_RANGE, HORIZONTAL_RANGE);
        Map<IAltarAccelerator.AltarAcceleratorType, Integer> entries = new HashMap<>();
        for (IAltarAccelerator accelerator : nearbyAccelerators) {
            if (accelerator.canAccelerate()) {
                int max = accelerator.getAcceleratorType().maximumEntries();
                int amount = entries.computeIfAbsent(accelerator.getAcceleratorType(), (a) -> 0);
                if (amount < max) {
                    accelerators.add(accelerator);
                    acceleratorPositions.add(((BlockEntity) accelerator).getPos());
                    speed += accelerator.getAcceleration();
                    entries.replace(accelerator.getAcceleratorType(), amount + 1);
                }
            }
        }
    }

    public void passiveParticles() {
        Vec3d itemPos = getItemPos(this);
        float particleVelocityMultiplier = -0.015f;
        int particleAge = 40;
        float scaleMultiplier = 1f;
        if (recipe != null) {
            if (recipe.spirits.size() > 1) {
                int amount = (recipe.spirits.size() - 2);
                particleVelocityMultiplier -= amount * 0.005f;
                particleAge -= amount * 5;
                scaleMultiplier += amount * 0.02f;
            }
        }
        int spiritsRendered = 0;
        for (int i = 0; i < spiritInventory.slotCount; i++) {
            ItemStack item = spiritInventory.getStack(i);
            for (IAltarAccelerator accelerator : accelerators) {
                if (accelerator != null) {
                    accelerator.addParticles(pos, itemPos);
                }
            }
            if (item.getItem() instanceof MalumSpiritItem spiritSplinterItem) {
                Vec3d offset = getSpiritOffset(spiritsRendered++, 0);
                Color color = spiritSplinterItem.type.getColor();
                Color endColor = spiritSplinterItem.type.getEndColor();
                double x = getPos().getX() + offset.getX();
                double y = getPos().getY() + offset.getY();
                double z = getPos().getZ() + offset.getZ();
				ParticleEffects.spawnSpiritGlimmerParticles(world, x, y, z, color, endColor);
                if (recipe != null) {
                    Vec3d velocity = new Vec3d(x, y, z).subtract(itemPos).normalize().multiply(particleVelocityMultiplier);
                    float alpha = 0.07f / spiritInventory.nonEmptyItemAmount;
                    for (IAltarAccelerator accelerator : accelerators) {
                        if (accelerator != null) {
                            accelerator.addParticles(color, endColor, alpha, pos, itemPos);
                        }
                    }
					WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
							.setTransparencyData(GenericParticleData.create(0.15f, 0.25f, 0f).build())
							.setColorData(ColorParticleData.create(color, endColor).setEasing(Easing.BOUNCE_IN_OUT).setCoefficient(0.8f).build())
							.setScaleData(GenericParticleData.create(0.225f*scaleMultiplier, 0).build())
							.setSpinData(SpinParticleData.create(0.1f + world.random.nextFloat() * 0.1f).build())
							.setLifetime(particleAge)
							.setRandomOffset(0.02f)
							.setRandomMotion(0.01f, 0.01f)
							.setRandomMotion(0.0025f, 0.0025f)
							.addMotion(velocity.x, velocity.y, velocity.z)
							.enableNoClip()
							.repeat(world, x, y, z, 1);

					WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
							.setTransparencyData(GenericParticleData.create(0.05f, 0.15f, 0f).build())
							.setScaleData(GenericParticleData.create(0.1f*scaleMultiplier, 0).build())
							.setLifetime(particleAge)
							.setRandomOffset(0.02f)
							.setRandomMotion(0.01f, 0.01f)
							.setColorData(ColorParticleData.create(endColor, color.darker()).setEasing(Easing.BOUNCE_IN_OUT).setCoefficient(0.8f).build())
							.setSpinData(SpinParticleData.create(0.1f + world.random.nextFloat() * 0.1f).build())
							.setRandomMotion(0.0025f, 0.0025f)
							.addMotion(velocity.x, velocity.y, velocity.z)
							.enableNoClip()
							.repeat(world, x, y, z, 1);

					WorldParticleBuilder.create(LodestoneParticleRegistry.TWINKLE_PARTICLE)
							.setTransparencyData(GenericParticleData.create(alpha*0.5f, alpha*3.5f, 0f).build())
							.setScaleData(GenericParticleData.create(0.2f, 0.4f*scaleMultiplier, 0).setEasing(Easing.QUINTIC_IN, Easing.CUBIC_IN_OUT).build())
							.setLifetime(particleAge)
							.setRandomOffset(0.1)
							.setRandomMotion(0.02f)
							.setColorData(ColorParticleData.create(color, endColor).setEasing(Easing.BOUNCE_IN_OUT).setCoefficient(0.5f).build())
							.setRandomMotion(0.0025f, 0.0025f)
							.enableNoClip()
							.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
							.repeat(world, itemPos.x, itemPos.y, itemPos.z, 1);
                }
            }
        }
    }
}

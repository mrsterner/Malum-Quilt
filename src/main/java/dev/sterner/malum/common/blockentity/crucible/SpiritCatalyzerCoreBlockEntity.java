package dev.sterner.malum.common.blockentity.crucible;

import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import com.sammy.lodestone.systems.multiblock.HorizontalDirectionStructure;
import com.sammy.lodestone.systems.multiblock.MultiBlockCoreEntity;
import com.sammy.lodestone.systems.multiblock.MultiBlockStructure;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.malum.common.item.spirit.MalumSpiritItem;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Supplier;

public class SpiritCatalyzerCoreBlockEntity extends MultiBlockCoreEntity implements ICrucibleAccelerator {
    public static final Supplier<HorizontalDirectionStructure> STRUCTURE = () -> HorizontalDirectionStructure.of(new MultiBlockStructure.StructurePiece(0, 1, 0, MalumObjects.SPIRIT_CATALYZER_COMPONENT.getDefaultState()));

    public static final ICrucibleAccelerator.CrucibleAcceleratorType CATALYZER = new ICrucibleAccelerator.ArrayCrucibleAcceleratorType("catalyzer",
        new float[]{0.2f, 0.25f, 0.3f, 0.4f, 0.45f, 0.5f, 0.6f, 0.8f},
        new int[]{1, 1, 1, 2, 2, 3, 3, 5},
        new float[]{0.25f, 0.5f, 0.75f, 1f, 1.5f, 2.25f, 3.5f, 8f});

    public LodestoneBlockEntityInventory inventory;
    public int burnTicks;
    IAccelerationTarget target;

    public SpiritCatalyzerCoreBlockEntity(BlockEntityType<?> type, MultiBlockStructure structure, BlockPos pos, BlockState state) {
        super(type, structure, pos, state);
        inventory = new LodestoneBlockEntityInventory(1, 64, t -> !(t.getItem() instanceof MalumSpiritItem)) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(world, pos);
            }
        };
    }

    public ItemStack getHeldItem() {
        return inventory.getStack(0);
    }

    public SpiritCatalyzerCoreBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.SPIRIT_CATALYZER, STRUCTURE.get(), pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        inventory.save(compound);
        if (burnTicks != 0) {
            compound.putInt("burnTicks", burnTicks);
        }
        super.writeNbt(compound);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        inventory.load(compound);
        burnTicks = compound.getInt("burnTicks");
        super.readNbt(compound);
    }

    @Override
    public CrucibleAcceleratorType getAcceleratorType() {
        return CATALYZER;
    }

    @Override
    public IAccelerationTarget getTarget() {
        return target;
    }

    @Override
    public void setTarget(IAccelerationTarget target) {
        this.target = target;
    }

    @Override
    public boolean canStartAccelerating() {
        boolean ticks = burnTicks > 0;
        return ticks;
    }

    @Override
    public boolean canAccelerate() {
        updateBurnTicks();
        return burnTicks > 0;
    }

    @Override
    public void tick() {
        if (target != null && !target.canBeAccelerated()) {
            setTarget(null);
        } else if (target != null) {
            if (burnTicks > 0) {
                burnTicks--;
            }
        }
    }

    public void updateBurnTicks() {
        if (burnTicks == 0) {
            ItemStack stack = inventory.getStack(0);
            if (!stack.isEmpty()) {
                burnTicks = AbstractFurnaceBlockEntity.createFuelTimeMap().get(stack.getItem()) / 2;
                stack.decrement(1);
                BlockHelper.updateAndNotifyState(world, pos);
            }
        }
    }

    @Override
    public void addParticles(Color color, Color endColor, float alpha, BlockPos targetPos, Vec3d targetItemPos) {
        if (burnTicks > 0) {
            Vec3d startPos = getItemPos(this);
            float random = world.random.nextFloat() * 0.04f;
            Vec3d velocity = startPos.subtract(targetItemPos.add(random, random, random)).normalize().multiply(-0.08f);

			WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
					.setTransparencyData(GenericParticleData.create(alpha * 5f, 0f).setCoefficient(0.5f).build())
					.setScaleData(GenericParticleData.create(0.15f + world.random.nextFloat() * 0.15f, 0).build())
					.setSpinData(SpinParticleData.create(0.1f + world.random.nextFloat() * 0.05f).setSpinOffset((0.075f * world.getTime() % 6.28f)).build())
					.setColorData(ColorParticleData.create(color.brighter(), endColor).setCoefficient(0.75f).build())
					.setLifetime((int) (10 + world.random.nextInt(8) + Math.sin((0.2 * world.getTime()) % 6.28f)))
					.setRandomOffset(0.05)
					.setMotion(velocity.x, velocity.y, velocity.z)
					.enableNoClip()
					.repeat(world, startPos.x, startPos.y, startPos.z, 1);

			WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
					.setTransparencyData(GenericParticleData.create(alpha * 3, 0f).build())
					.setScaleData(GenericParticleData.create(0.2f + world.random.nextFloat() * 0.15f, 0).build())
					.setColorData(ColorParticleData.create(color, endColor).build())
					.setLifetime(15)
					.setRandomOffset(0.05)
					.setSpinData(SpinParticleData.create(0).setSpinOffset((0.15f * world.getTime()) % 6.28f).build())
					.enableNoClip()
					.repeat(world, startPos.x, startPos.y, startPos.z, 1);

			WorldParticleBuilder.create(LodestoneParticleRegistry.STAR_PARTICLE)
					.setTransparencyData(GenericParticleData.create(alpha * 3, 0f).build())
					.setScaleData(GenericParticleData.create(0.45f + world.random.nextFloat() * 0.15f, 0).build())
					.setColorData(ColorParticleData.create(color, endColor).build())
					.setLifetime(15)
					.setRandomOffset(0.05)
					.setSpinData(SpinParticleData.create(0).setSpinOffset((0.075f * world.getTime()) % 6.28f).build())
					.enableNoClip()
					.repeat(world, startPos.x, startPos.y, startPos.z, 1);
        }
    }


    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand) {
        inventory.interact(player.getWorld(), player, hand);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(@Nullable PlayerEntity player) {
        inventory.dumpItems(world, BlockHelper.fromBlockPos(pos).add(0.5f, 0.5f, 0.5f));
        super.onBreak(player);
    }

    public static Vec3d getItemPos(SpiritCatalyzerCoreBlockEntity blockEntity) {
        return BlockHelper.fromBlockPos(blockEntity.getPos()).add(blockEntity.itemOffset());
    }

    public Vec3d itemOffset() {
        return new Vec3d(0.5f, 1.95f, 0.5f);
    }
}

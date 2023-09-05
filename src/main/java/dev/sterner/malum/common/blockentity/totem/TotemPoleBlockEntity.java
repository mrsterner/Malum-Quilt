package dev.sterner.malum.common.blockentity.totem;

import com.google.common.collect.Sets;
import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntity;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.SimpleParticleEffect;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.malum.common.block.totem.TotemPoleBlock;
import dev.sterner.malum.common.blockentity.storage.ItemStandBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.block.BlockParticlePacket;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Set;

public class TotemPoleBlockEntity extends LodestoneBlockEntity {
    public MalumSpiritType type;
    public boolean haunted;
    public int desiredColor;
    public int currentColor;
    public int baseWorld;
    public TotemBaseBlockEntity totemBase;
    public boolean corrupted;
    public Block logBlock;
    public Direction direction;

    public TotemPoleBlockEntity(BlockEntityType<? extends TotemPoleBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.corrupted = ((TotemPoleBlock<?>) state.getBlock()).corrupted;
        this.logBlock = ((TotemPoleBlock<?>) state.getBlock()).logBlock;
        this.direction = state.get(Properties.HORIZONTAL_FACING);
    }

    public TotemPoleBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.TOTEM_POLE, pos, state);
    }

    public void onStrip(BlockState state, World world, BlockPos pos) {
        //noinspection ConstantConditions
        world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public void setCachedBaseBlock(@Nullable TotemBaseBlockEntity cachedBaseBlock) {
        this.totemBase = cachedBaseBlock;
    }

    @Nullable
    public TotemBaseBlockEntity getCachedBaseBlock() {
        if (totemBase != null) {
            return totemBase;
        }

        BlockPos down = pos.down();
        //noinspection ConstantConditions
        while (down.getY() >= world.getBottomY()) {
            if (!(world.getBlockEntity(down) instanceof TotemBaseBlockEntity totemBaseBlockEntity)) {
                down = down.down();
                continue;
            }
            totemBase = totemBaseBlockEntity;
            break;
        }
        return totemBase;
    }

    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getStackInHand(hand);
        if (heldStack.getItem().equals(MalumObjects.HEX_ASH) && !haunted) {
            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }
            if (!player.isCreative()) {
                heldStack.decrement(1);
            }
            haunted = true;
            desiredColor = 20;
            if(world instanceof ServerWorld serverWorld){
                PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> BlockParticlePacket.send(track, type.getColor(), pos));
            }
            world.playSound(null, pos, MalumSoundRegistry.TOTEM_ENGRAVE, SoundCategory.BLOCKS, 1, MathHelper.nextFloat(world.random, 0.9f, 1.1f));
            world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1, 1);
            if (corrupted) {
                world.playSound(null, pos, MalumSoundRegistry.MAJOR_BLIGHT_MOTIF, SoundCategory.BLOCKS, 1, 1);
            }
            BlockHelper.updateState(world, pos);
            return ActionResult.SUCCESS;
        }

        Item held = heldStack.getItem();
        if(!(held instanceof MiningToolItem tool)) {
            return ActionResult.FAIL;
        }


        if (world != null && (tool.getMiningSpeedMultiplier(heldStack, world.getBlockState(pos)) > 1.0F)) {
            if (haunted) {
                if (world.isClient()) {
                    return ActionResult.SUCCESS;
                }
                desiredColor = 0;
                haunted = false;
                if (world instanceof ServerWorld serverWorld) {
                    PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> BlockParticlePacket.send(track, type.getColor(), pos));
                }
                world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1, 1);
                if (corrupted) {
                    world.playSound(null, pos, MalumSoundRegistry.MAJOR_BLIGHT_MOTIF, SoundCategory.BLOCKS, 1, 1);
                }
                BlockHelper.updateState(world, pos);
                return ActionResult.SUCCESS;
            }
            if (type != null) {
                if (world.isClient()) {
                    return ActionResult.SUCCESS;
                }
                world.setBlockState(pos, logBlock.getDefaultState());
                if (world instanceof ServerWorld serverWorld) {
                    PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> BlockParticlePacket.send(track, type.getColor(), pos));
                }
                world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1, 1);
                if (corrupted) {
                    world.playSound(null, pos, MalumSoundRegistry.MAJOR_BLIGHT_MOTIF, SoundCategory.BLOCKS, 1, 1);
                }
                onBreak(null);
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(player, hand);
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        if (type != null) {
            compound.putString("type", type.identifier);
        }
        if (desiredColor != 0) {
            compound.putInt("desiredColor", desiredColor);
        }
        if (currentColor != 0) {
            compound.putInt("currentColor", currentColor);
        }
        if (baseWorld != 0) {
            compound.putInt("baseWorld", baseWorld);
        }
        compound.putBoolean("corrupted", corrupted);
        super.writeNbt(compound);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        if (compound.contains("type")) {
            type = SpiritHelper.getSpiritType(compound.getString("type"));
        }
        desiredColor = compound.getInt("desiredColor");
        currentColor = compound.getInt("currentColor");
        baseWorld = compound.getInt("baseWorld");
        corrupted = compound.getBoolean("corrupted");
        super.readNbt(compound);
    }

    @Override
    public void init() {
        super.init();
        if (world.getBlockEntity(new BlockPos(getPos().getX(), baseWorld, getPos().getZ())) instanceof TotemBaseBlockEntity totemBaseBlockEntity) {
            totemBase = totemBaseBlockEntity;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (currentColor > desiredColor) {
            currentColor--;
        }
        if (currentColor < desiredColor) {
            currentColor++;
        }
        if (world.isClient()) {
            if (type != null && desiredColor != 0) {
                passiveParticles();
                if (totemBase != null && totemBase.rite != null) {
                    getFilters().forEach(this::filterParticles);
                }
            }
        }
    }

    public Set<ItemStandBlockEntity> getFilters() {
        Set<ItemStandBlockEntity> standBlockEntities = Sets.newHashSet();
        for (Direction value : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(pos.offset(value));
            if (blockEntity instanceof ItemStandBlockEntity standBlockEntity) {
                standBlockEntities.add(standBlockEntity);
            }
        }
        return standBlockEntities;
    }

    public void create(MalumSpiritType type) {
        world.playSound(null, pos, MalumSoundRegistry.TOTEM_ENGRAVE, SoundCategory.BLOCKS, 1, MathHelper.nextFloat(world.random, 0.9f, 1.1f));
        world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1, MathHelper.nextFloat(world.random, 0.9f, 1.1f));
        if(world instanceof ServerWorld serverWorld){
            PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> BlockParticlePacket.send(track, type.getColor(), pos));
        }
        this.type = type;
        this.currentColor = 10;
        BlockHelper.updateState(world, pos);
    }

    public void riteStarting(TotemBaseBlockEntity totemBase, int height) {
        world.playSound(null, pos, MalumSoundRegistry.TOTEM_CHARGE, SoundCategory.BLOCKS, 1, 0.9f + 0.2f * height);
        if(world instanceof ServerWorld serverWorld){
            PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> BlockParticlePacket.send(track, type.getColor(), pos));
        }
        this.desiredColor = 10;
        this.baseWorld = pos.getY() - height;
        this.totemBase = totemBase;
        this.haunted = false;
        BlockHelper.updateState(world, pos);
    }

    public void riteComplete() {
        this.desiredColor = 20;
        BlockHelper.updateState(world, pos);
    }

    public void riteEnding() {
        this.desiredColor = 0;
        this.haunted = false;
        BlockHelper.updateState(world, pos);
    }

    @Override
    public void onBreak(@Nullable PlayerEntity player) {
        if (world.isClient()) {
            return;
        }
        BlockPos basePos = new BlockPos(pos.getX(), baseWorld, pos.getZ());
        if (world.getBlockEntity(basePos) instanceof TotemBaseBlockEntity base) {
            if (base.active) {
                base.endRite();
            }
        }
    }

    public void filterParticles(ItemStandBlockEntity itemStandBlockEntity) {
        if (world.getTime() % 6L == 0) {
            if (!itemStandBlockEntity.inventory.getStack(0).isEmpty()) {
                Vec3d itemPos = itemStandBlockEntity.getItemPos();
				WorldParticleBuilder.create(LodestoneParticleRegistry.STAR_PARTICLE)
						.setTransparencyData(GenericParticleData.create(0.04f, 0.1f, 0f).build())
						.setScaleData(GenericParticleData.create(0.5f, 1f + world.random.nextFloat() * 0.1f, 0).setEasing(Easing.QUINTIC_IN, Easing.CUBIC_IN_OUT).build())
						.setSpinData(SpinParticleData.create(0, 0.2f, 0).setSpinOffset((world.getTime()*0.02f)%360).setEasing(Easing.CUBIC_IN, Easing.EXPO_IN).build())
						.setColorData(ColorParticleData.create(type.getColor(), type.getEndColor()).setEasing(Easing.BOUNCE_IN_OUT).setCoefficient(0.5f).build())
						.setLifetime(25)
						.setRandomOffset(0.1)
						.setRandomMotion(0.02f)
						.setRandomMotion(0.0025f, 0.0025f)
						.enableNoClip()
						.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
						.spawn(world, itemPos.x, itemPos.y, itemPos.z);
            }
        }
    }
    public void passiveParticles() {
        if (world.getTime() % 6L == 0) {
            Color color = type.getColor();
            Color endColor = type.getEndColor();
			WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0, 0.06f, 0.12f).build())
					.setSpinData(SpinParticleData.create(0.2f).build())
					.setScaleData(GenericParticleData.create(0, 0.4f, 0).setEasing(Easing.LINEAR, Easing.CIRC_IN_OUT).build())
					.setColorData(ColorParticleData.create(color, endColor).setEasing(Easing.SINE_IN).setCoefficient(0.5f).build())
					.setLifetime(35)
					.addMotion(0, MathHelper.nextFloat(world.random, -0.03f, 0.03f), 0)
					.enableNoClip()
					.setRandomOffset(0.1f, 0.2f)
					.setRandomMotion(0.01f, 0.02f)
					.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
					.surroundBlock(world, pos, Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH);

			WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0, 0.06f, 0.03f).build())
					.setSpinData(SpinParticleData.create(0.1f).build())
					.setScaleData(GenericParticleData.create(0f, 0.55f, 0.3f).build())
					.setColorData(ColorParticleData.create(color, endColor).setCoefficient(0.5f).build())
					.setLifetime(60)
					.addMotion(0, MathHelper.nextFloat(world.random, -0.03f, 0.03f), 0)
					.setRandomOffset(0.1f, 0.2f)
					.enableNoClip()
					.setRandomMotion(0.01f, 0.02f)
					.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
					.surroundBlock(world, pos, Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH);
        }
    }

    public void clientTick() {
    }
}

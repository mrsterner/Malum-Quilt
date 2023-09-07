package dev.sterner.malum.common.blockentity.totem;

import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.lodestone.systems.blockentity.LodestoneBlockEntity;
import dev.sterner.malum.common.block.totem.TotemBaseBlock;
import dev.sterner.malum.common.blockentity.storage.ItemStandBlockEntity;
import dev.sterner.malum.common.network.packet.s2c.block.TotemBaseActivationParticlePacket;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumRiteRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.spiritrite.MalumRiteType;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class TotemBaseBlockEntity extends LodestoneBlockEntity {

    public MalumRiteType rite;
    public List<MalumSpiritType> spirits = new ArrayList<>();
    public Set<BlockPos> poles = new HashSet<>();
    public Set<TotemPoleBlockEntity> cachedTotemPoleInstances = new HashSet<>();
    public Set<BlockPos> filters = new HashSet<>();
    public Set<ItemStandBlockEntity> cachedFilterInstances = new HashSet<>();
    public boolean active;
    public int progress;
    public int height;
    public boolean corrupted;
    public Direction direction;

    public TotemBaseBlockEntity(BlockEntityType<? extends TotemBaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.corrupted = ((TotemBaseBlock<?>) state.getBlock()).corrupted;
        if (world == null) {
            return;
        }

        BlockPos up = pos.up();
        while (world.getBlockEntity(up) instanceof TotemPoleBlockEntity totemPoleBlockEntity) {
            totemPoleBlockEntity.setCachedBaseBlock(this);
            up = up.up();
        }
    }
    public TotemBaseBlockEntity(BlockPos pos, BlockState state) {
        this(MalumBlockEntityRegistry.TOTEM_BASE, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isClient()) {
            if (rite != null) {
                progress++;
                if (progress >= rite.getRiteTickRate(corrupted)) {
                    rite.executeRite(this);
                    progress = 0;
                    BlockHelper.updateAndNotifyState(world, pos);
                }
            } else if (active) {
                progress--;
                if (progress <= 0) {
                    height++;
                    BlockPos polePos = pos.up(height);
                    if (world.getBlockEntity(polePos) instanceof TotemPoleBlockEntity pole) {
                        addPole(pole);
                    } else {
                        MalumRiteType rite = MalumRiteRegistry.getRite(spirits);
                        if (rite == null) {
                            endRite();
                        } else {
                            completeRite(rite);
                            markDirty();
                        }
                    }
                    progress = 20;
                    BlockHelper.updateState(world, pos);
                }
            }
        }
    }

    @Override
    public void onBreak(@Nullable PlayerEntity player) {
        if (!world.isClient) {
            poles.forEach(p -> {
                if (world.getBlockEntity(p) instanceof TotemPoleBlockEntity pole) {
                    pole.riteEnding();
                }
            });
            if (height > 1) {
                world.playSound(null, pos, MalumSoundRegistry.TOTEM_CHARGE, SoundCategory.BLOCKS, 1, 0.5f);
                if(world instanceof ServerWorld serverWorld){
                    PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> TotemBaseActivationParticlePacket.send(track, spirits.stream().map(MalumSpiritType::getColor).collect(Collectors.toCollection(ArrayList::new)), pos.up()));
                }
            }
        }
    }


    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand) {
        if (active && rite == null) {
            return ActionResult.FAIL;
        }
        if (!world.isClient) {
            if (active) {
                endRite();
            } else {
                startRite();
            }
            BlockHelper.updateState(world, pos);
        }
        player.swingHand(Hand.MAIN_HAND, true);
        return ActionResult.SUCCESS;
    }

    @Override
    public void init() {
        super.init();
        poles.forEach(p -> {
            if (world.getBlockEntity(p) instanceof TotemPoleBlockEntity totemPole) {
                cachedTotemPoleInstances.add(totemPole);
                totemPole.getFilters().forEach(this::addFilter);
            }
        });
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        if (rite != null) {
            compound.putString("rite", rite.identifier);
        }
        if (!spirits.isEmpty()) {
            compound.putInt("spiritCount", spirits.size());
            for (int i = 0; i < spirits.size(); i++) {
                MalumSpiritType type = spirits.get(i);
                compound.putString("spirit_" + i, type.identifier);
            }
        }
        compound.putBoolean("active", active);
        if (active) {
            compound.putInt("progress", progress);
            compound.putInt("height", height);
        }
        if (direction != null) {
            compound.putString("direction", direction.name());
        }
        compound.putBoolean("corrupted", corrupted);
        super.writeNbt(compound);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        rite = MalumRiteRegistry.getRite(compound.getString("rite"));
        int size = compound.getInt("spiritCount");
        spirits.clear();
        for (int i = 0; i < size; i++) {
            spirits.add(SpiritHelper.getSpiritType(compound.getString("spirit_" + i)));
        }
        active = compound.getBoolean("active");
        progress = compound.getInt("progress");
        height = compound.getInt("height");
        poles.clear();
        filters.clear();
        cachedTotemPoleInstances.clear();
        for (int i = 1; i <= height; i++) {
            poles.add(new BlockPos(pos.getX(), pos.getY() + i, pos.getZ()));
        }
        direction = Direction.byName(compound.getString("direction"));
        corrupted = compound.getBoolean("corrupted");
        progress = compound.getInt("progress");
        super.readNbt(compound);
    }
    public void addFilter(ItemStandBlockEntity itemStand) {
        filters.add(itemStand.getPos());
        cachedFilterInstances.add(itemStand);
    }
    public void addPole(TotemPoleBlockEntity pole) {
        Direction direction = pole.getCachedState().get(HORIZONTAL_FACING);
        if (poles.isEmpty()) {
            this.direction = direction;
        }
        if (pole.corrupted == corrupted && direction.equals(this.direction)) {
            if (pole.type != null) {
                spirits.add(pole.type);
                poles.add(pole.getPos());
                filters.addAll(pole.getFilters().stream().map(BlockEntity::getPos).toList());
                cachedFilterInstances.addAll(pole.getFilters());
                pole.riteStarting(this, height);
            }
        }
    }

    public void completeRite(MalumRiteType rite) {
        if (world != null) {
            world.playSound(null, pos, MalumSoundRegistry.TOTEM_ACTIVATED, SoundCategory.BLOCKS, 1, 0.75f + height * 0.1f);
            if(world instanceof ServerWorld serverWorld){
                PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> TotemBaseActivationParticlePacket.send(track, spirits.stream().map(MalumSpiritType::getColor).collect(Collectors.toCollection(ArrayList::new)), pos.up()));
            }
        }

        poles.forEach(p -> {
            if (world.getBlockEntity(p) instanceof TotemPoleBlockEntity pole) {
                pole.riteComplete();
            }
        });
        progress = 0;
        rite.executeRite(this);
        if (rite.isOneAndDone(corrupted)) {
            return;
        }
        this.rite = rite;
        disableOtherRites();
    }

    public void disableOtherRites() {
        int range = rite.getRiteRadius(corrupted);
        BlockHelper.getBlockEntitiesStream(TotemBaseBlockEntity.class, world, rite.getRiteEffectCenter(this), range).filter(blockEntity -> !blockEntity.equals(this) && rite.equals(blockEntity.rite) && corrupted == blockEntity.corrupted).forEach(TotemBaseBlockEntity::endRite);

        BlockHelper.getBlockEntitiesStream(TotemBaseBlockEntity.class, world, pos, 10).filter(blockEntity -> !blockEntity.equals(this) && rite.equals(blockEntity.rite) && corrupted == blockEntity.corrupted).forEach(b -> {
            b.tryDisableRite(this);
        });

    }
    public void tryDisableRite(TotemBaseBlockEntity target) {
        int range = rite.getRiteRadius(corrupted);

        Collection<TotemBaseBlockEntity> otherTotems = BlockHelper.getBlockEntities(TotemBaseBlockEntity.class, world, rite.getRiteEffectCenter(this), range);
        if (otherTotems.contains(target)) {
            endRite();
        }
    }

    public void startRite() {
        resetValues();
        active = true;
    }

    public void endRite() {
        if (height > 1) {
            if(world != null){
                world.playSound(null, pos, MalumSoundRegistry.TOTEM_CANCELLED, SoundCategory.BLOCKS, 1, 1);
                if(world instanceof ServerWorld serverWorld){
                    PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> TotemBaseActivationParticlePacket.send(track, spirits.stream().map(MalumSpiritType::getColor).collect(Collectors.toCollection(ArrayList::new)), pos.up()));
                }
            }
        }
        resetRite();
    }

    public void resetRite() {
        poles.forEach(p -> {
            if (world.getBlockEntity(p) instanceof TotemPoleBlockEntity pole) {
                pole.riteEnding();
            }
        });
        resetValues();
    }

    public void resetValues() {
        height = 0;
        rite = null;
        active = false;
        progress = 0;
        spirits.clear();
        poles.clear();
    }
}

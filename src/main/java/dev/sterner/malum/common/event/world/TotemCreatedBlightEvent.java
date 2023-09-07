package dev.sterner.malum.common.event.world;

import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.malum.common.blockentity.totem.TotemBaseBlockEntity;
import dev.sterner.malum.common.blockentity.totem.TotemPoleBlockEntity;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.spirit.MalumSpiritType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static dev.sterner.malum.common.registry.MalumObjects.SOULWOOD_TOTEM_BASE;
import static dev.sterner.malum.common.registry.MalumObjects.SOULWOOD_TOTEM_POLE;

public class TotemCreatedBlightEvent extends ActiveBlightEvent {
	public int totemTakeoverTimer;

	@Override
	public void tick(World world) {
		if (totemTakeoverTimer == 0) {
			BlockState state = BlockHelper.setBlockStateWithExistingProperties(world, sourcePos, SOULWOOD_TOTEM_BASE.getDefaultState(), Block.NOTIFY_ALL);
			world.addBlockEntity(new TotemBaseBlockEntity(sourcePos, state));
			world.syncWorldEvent(null, 2001, sourcePos, Block.getRawIdFromState(state));
			world.playSound(null, sourcePos, MalumSoundRegistry.MINOR_BLIGHT_MOTIF, SoundCategory.BLOCKS, 1f, 1.8f);
		}
		totemTakeoverTimer++;
		if (totemTakeoverTimer % rate == 0) {
			int offset = totemTakeoverTimer / 4;
			BlockPos totemPos = sourcePos.up(offset);
			if (world.getBlockEntity(totemPos) instanceof TotemPoleBlockEntity totemPoleTile) {
				MalumSpiritType type = totemPoleTile.type;
				BlockState state = BlockHelper.setBlockStateWithExistingProperties(world, totemPos, SOULWOOD_TOTEM_POLE.getDefaultState(), Block.NOTIFY_ALL);
				TotemPoleBlockEntity newTileEntity = new TotemPoleBlockEntity(totemPos, state);
				newTileEntity.setWorld(world);
				newTileEntity.create(type);
				world.addBlockEntity(newTileEntity);
				world.setBlockState(totemPos, state);
				world.syncWorldEvent(null, 2001, totemPos, Block.getRawIdFromState(state));
				world.playSound(null, sourcePos, MalumSoundRegistry.MINOR_BLIGHT_MOTIF, SoundCategory.BLOCKS, 1f, 1.8f);
			}
		}
		super.tick(world);
	}

	@Override
	public void end(World world) {
		if (totemTakeoverTimer >= 24) {
			super.end(world);
		}
	}
}

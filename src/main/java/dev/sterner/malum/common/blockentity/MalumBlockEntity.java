package dev.sterner.malum.common.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MalumBlockEntity extends BlockEntity {
	public boolean needsSync;
	public MalumBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}


	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound tag = super.toInitialChunkDataNbt();
		this.writeNbt(tag);
		return tag;
	}

	@Override
	public void readNbt(NbtCompound pTag) {
		needsSync = true;
		super.readNbt(pTag);
	}

	public void sync(World world, BlockPos pos) {
		if (world != null && !world.isClient) {
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
			toUpdatePacket();
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();
		sync(world, pos);
	}



	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	/**
	 * Called on both sides to tick the block entity as part of the ticker
	 */
	public void tick() {
		if (needsSync) {
			init();
			needsSync = false;
		}
	}

	/**
	 * Called only for server side when ticking
	 */
	public void serverTick() {

	}

	/**
	 * Called only for client side when ticking
	 */
	public void clientTick() {

	}

	/**
	 * Called on both sides to force an udpate after reload
	 */
	public void init() {

	}
}

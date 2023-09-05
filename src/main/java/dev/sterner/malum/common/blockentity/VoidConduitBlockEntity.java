package dev.sterner.malum.common.blockentity;

import com.sammy.lodestone.helpers.BlockHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntity;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.SimpleParticleEffect;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import com.sammy.lodestone.systems.particle.world.LodestoneWorldParticleTextureSheet;
import dev.sterner.malum.common.network.packet.s2c.block.VoidConduitParticlePacket;
import dev.sterner.malum.common.recipe.FavorOfTheVoidRecipe;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VoidConduitBlockEntity extends LodestoneBlockEntity {

	public final List<ItemStack> eatenItems = new ArrayList<>();
	public int progress;
	public int streak;

	protected static final VoxelShape WELL_SHAPE = Block.createCuboidShape(-16.0D, 11.0D, -16.0D, 32.0D, 13.0D, 32.0D);

	public VoidConduitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public VoidConduitBlockEntity(BlockPos pos, BlockState state) {
		this(MalumBlockEntityRegistry.VOID_CONDUIT, pos, state);
	}


	@Override
	protected void writeNbt(NbtCompound compound) {
		if (!eatenItems.isEmpty()) {
			compound.putInt("itemCount", eatenItems.size());
			for (int i = 0; i < eatenItems.size(); i++) {
				NbtCompound itemTag = new NbtCompound();
				ItemStack stack = eatenItems.get(i);
				stack.writeNbt(itemTag);
				compound.put("item_"+i, itemTag);
			}
		}
		compound.putInt("progress", progress);
		compound.putInt("streak", streak);
		super.writeNbt(compound);
	}

	@Override
	public void readNbt(NbtCompound compound) {
		eatenItems.clear();
		for (int i = 0; i < compound.getInt("itemCount"); i++) {
			NbtCompound itemTag = compound.getCompound("item_"+i);
			eatenItems.add(ItemStack.fromNbt(itemTag));
		}
		progress = compound.getInt("progress");
		streak = compound.getInt("streak");
		super.readNbt(compound);
	}

	@Override
	public void serverTick() {
		super.serverTick();
		if (world.getTime() % 100L == 0) {
			world.playSound(null, pos, MalumSoundRegistry.UNCANNY_VALLEY, SoundCategory.HOSTILE, 1f, MathHelper.nextFloat(world.getRandom(), 0.55f, 1.75f));
		}
		if (world.getTime() % 20L == 0) {
			world.playSound(null, pos, MalumSoundRegistry.VOID_HEARTBEAT, SoundCategory.HOSTILE, 1.5f, MathHelper.nextFloat(world.getRandom(), 0.95f, 1.15f));
		}
		if (world.getTime() % 40L == 0) {
			List<ItemEntity> items = world.getNonSpectatingEntities(
							ItemEntity.class,
							new Box(pos.add(1, -3, 1), pos.add(-1, -1, -1)).expand(2))
					.stream().sorted(Comparator.comparingInt(itemEntity -> itemEntity.age)).toList();
			for (ItemEntity entity : items) {
				ItemStack item = entity.getStack();
				if (item.getItem().equals(MalumObjects.BLIGHTED_GUNK)) {
					progress+=20;
				}
				eatenItems.add(item);
				entity.discard();
			}
			BlockHelper.updateAndNotifyState(world, pos);
		}
		if (!eatenItems.isEmpty()) {
			progress++;
			if (progress >= 80) {
				int resultingProgress = 65;
				ItemStack stack = eatenItems.get(eatenItems.size()-1);
				if (stack.getItem().equals(MalumObjects.BLIGHTED_GUNK)) {
					resultingProgress = 72+streak/4;
					streak++;
					world.playSound(null, pos, MalumSoundRegistry.HUNGRY_BELT_FEEDS, SoundCategory.PLAYERS, 0.7f, 0.6f + world.random.nextFloat() * 0.3f+streak*0.05f);
					world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.7f, 0.6f + world.random.nextFloat() * 0.2f+streak*0.05f);
				} else {
					FavorOfTheVoidRecipe recipe = FavorOfTheVoidRecipe.getRecipe(world, stack);
					float pitch = MathHelper.nextFloat(world.getRandom(), 0.85f, 1.35f) + streak * 0.1f;
					if (recipe != null) {
						streak++;
						int amount = recipe.output.getCount() * stack.getCount();
						while (amount > 0) {
							int count = Math.min(64, amount);
							ItemStack outputStack = new ItemStack(recipe.output.getItem(), count);
							outputStack.setNbt(recipe.output.getNbt());
							ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, outputStack);
							entity.setVelocity(0, 0.65f, 0.15f);
							world.spawnEntity(entity);
							amount -= count;
						}
						world.playSound(null, pos, MalumSoundRegistry.VOID_TRANSMUTATION, SoundCategory.HOSTILE, 2f, pitch);
					} else {
						ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, stack);
						entity.setVelocity(0, 0.65f, 0.15f);
						world.spawnEntity(entity);
						world.playSound(null, pos, MalumSoundRegistry.VOID_REJECTION, SoundCategory.HOSTILE, 2f, pitch);
					}
				}
				progress = resultingProgress;
				if(world instanceof ServerWorld serverWorld){
					PlayerLookup.tracking(serverWorld, serverWorld.getWorldChunk(pos).getPos()).forEach(track -> VoidConduitParticlePacket.send(track, pos.getX()+0.5f, pos.getY()+0.75f, pos.getZ()+0.5f));
				}
				eatenItems.remove(eatenItems.size()-1);
				BlockHelper.updateAndNotifyState(world, pos);
			}
			if (eatenItems.isEmpty()) {
				progress = 0;
			}
		}else if (streak != 0) {
			streak = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (world.isClient && world.getTime() % 6L == 0) {
			ClientOnly.spawnParticles(world, pos);
		}
	}
	public static class ClientOnly {
		public static void spawnParticles(World level, BlockPos blockPos) {
			float multiplier = MathHelper.nextFloat(level.random, 0.4f, 1f);
			Color color = new Color((int) (12 * multiplier), (int) (3 * multiplier), (int) (12 * multiplier));
			Color endColor = color.darker();
			WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
					.setTransparencyData(GenericParticleData.create(0, 0.2f, 0f).setEasing(Easing.SINE_IN, Easing.SINE_OUT).build())
					.setLifetime(60)
					.setSpinData(SpinParticleData.create(0.1f, 0.4f, 0).setEasing(Easing.SINE_IN, Easing.SINE_OUT).build())
					.setScaleData(GenericParticleData.create(0f, 0.9f, 0.5f).setEasing(Easing.SINE_IN, Easing.SINE_OUT).build())
					.setColorData(ColorParticleData.create(color, endColor).setCoefficient(0.5f).build())
					.addMotion(0, level.random.nextFloat() * 0.01f, 0)
					.setRandomOffset(3f, 0.02f)
					.enableNoClip()
					.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
					.setRenderType(LodestoneWorldParticleTextureSheet.TRANSPARENT)
					.surroundVoxelShape(level, blockPos, WELL_SHAPE, 12);
		}
	}
}

package dev.sterner.malum.common.blockentity;

import com.sammy.lodestone.helpers.ColorHelper;
import com.sammy.lodestone.helpers.NBTHelper;
import com.sammy.lodestone.setup.LodestoneParticleRegistry;
import com.sammy.lodestone.systems.blockentity.LodestoneBlockEntity;
import com.sammy.lodestone.systems.easing.Easing;
import com.sammy.lodestone.systems.particle.SimpleParticleEffect;
import com.sammy.lodestone.systems.particle.WorldParticleBuilder;
import com.sammy.lodestone.systems.particle.data.ColorParticleData;
import com.sammy.lodestone.systems.particle.data.GenericParticleData;
import com.sammy.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.malum.common.block.ether.EtherBrazierBlock;
import dev.sterner.malum.common.block.ether.EtherTorchBlock;
import dev.sterner.malum.common.block.ether.EtherWallTorchBlock;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumParticleRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;


public class EtherBlockEntity extends LodestoneBlockEntity {
    public int firstColorRGB;
    public Color firstColor;
    public int secondColorRGB;
    public Color secondColor;

    public EtherBlockEntity(BlockPos pos, BlockState state, int firstColor, int secondColor) {
        this(MalumBlockEntityRegistry.ETHER, pos, state, firstColor, secondColor);
    }

    public EtherBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 15712278, 4607909);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.firstColorRGB = nbt.getInt("FirstColor");
        this.firstColor = new Color(firstColorRGB);
        this.secondColorRGB = nbt.getInt("SecondColor");
        this.secondColor = new Color(secondColorRGB);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("FirstColor", firstColorRGB);
        nbt.putInt("SecondColor", secondColorRGB);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = super.toInitialChunkDataNbt();
        this.writeNbt(tag);
        return tag;
    }


	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        firstColorRGB = NBTHelper.getOrDefaultInt(nbt -> nbt.getCompound("display").getInt("FirstColor"), 15712278, itemStack.getNbt());
        this.firstColor = new Color(firstColorRGB);
        secondColorRGB = NBTHelper.getOrDefaultInt(nbt -> nbt.getCompound("display").getInt("SecondColor"), 4607909, itemStack.getNbt());
        this.secondColor = new Color(secondColorRGB);
    }

    public EtherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int firstColor, int secondColor) {
        super(type, pos, state);
        this.firstColorRGB = firstColor;
        this.secondColorRGB = secondColor;
        this.firstColor = new Color(firstColorRGB);
        this.secondColor = new Color(secondColorRGB);
    }

	@Override
	public void tick() {
		super.tick();
		if (world.isClient) {
			if (firstColor == null) {
				return;
			}
			Color firstColor = ColorHelper.darker(this.firstColor, 1);
			Color secondColor = this.secondColor == null ? firstColor : ColorHelper.brighter(this.secondColor, 1);
			double x = pos.getX() + 0.5;
			double y = pos.getY() + 0.6;
			double z = pos.getZ() + 0.5;
			int lifeTime = 14 + world.random.nextInt(4);
			float scale = 0.17f + world.random.nextFloat() * 0.03f;
			float velocity = 0.04f + world.random.nextFloat() * 0.02f;
			if (getCachedState().getBlock() instanceof EtherWallTorchBlock) {
				Direction direction = getCachedState().get(WallTorchBlock.FACING);
				x += direction.getVector().getX() * -0.28f;
				y += 0.2f;
				z += direction.getVector().getZ() * -0.28f;
				lifeTime -= 6;
			}

			if (getCachedState().getBlock() instanceof EtherTorchBlock || getCachedState().getBlock() instanceof EtherWallTorchBlock) {
				lifeTime -= 4;
			}
			if (getCachedState().getBlock() instanceof EtherBrazierBlock) {
				y -= 0.2f;
				lifeTime -= 2;
				scale *= 1.25f;
			}
			WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
					.setScaleData(GenericParticleData.create(scale, 0).build())
					.setTransparencyData(GenericParticleData.create(0.75f, 0.25f).build())
					.setColorData(ColorParticleData.create(firstColor, secondColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
					.setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
					.setLifetime(lifeTime)
					.addMotion(0, velocity, 0)
					.enableNoClip()
					.spawn(world, x, y, z);

			WorldParticleBuilder.create(LodestoneParticleRegistry.TWINKLE_PARTICLE)
					.setScaleData(GenericParticleData.create(scale * 2, scale * 0.1f).build())
					.setTransparencyData(GenericParticleData.create(0.25f, 0).build())
					.setColorData(ColorParticleData.create(firstColor, secondColor).setEasing(Easing.SINE_IN).setCoefficient(2.25f).build())
					.setSpinData(SpinParticleData.create(0, 2).setEasing(Easing.QUARTIC_IN).build())
					.setLifetime(lifeTime)
					.enableNoClip()
					.spawn(world, x, y, z);
			if (world.getTime() % 2L == 0) {
				y += 0.15f;
				if (world.random.nextFloat() < 0.5f) {
					WorldParticleBuilder.create(MalumParticleRegistry.SPIRIT_FLAME_PARTICLE)
							.setScaleData(GenericParticleData.create(0.5f, 0.75f, 0).build())
							.setColorData(ColorParticleData.create(firstColor, secondColor).setEasing(Easing.CIRC_IN_OUT).setCoefficient(2.5f).build())
							.setTransparencyData(GenericParticleData.create(0.2f, 1f, 0).setEasing(Easing.SINE_IN, Easing.QUAD_IN).setCoefficient(3.5f).build())
							.setRandomOffset(0.15f, 0.2f)
							.addMotion(0, 0.0035f, 0)
							.setRandomMotion(0.001f, 0.005f)
							.addActor(p -> p.setVelocity(p.getVelocity().multiply(0.985f-world.random.nextFloat() * 0.04f)))
							.enableNoClip()
							.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
							.spawn(world, x, y, z);
				}
				if (world.random.nextFloat() < 0.25f) {
					WorldParticleBuilder.create(MalumParticleRegistry.SPIRIT_FLAME_PARTICLE)
							.setScaleData(GenericParticleData.create(0.3f, 0.5f, 0).build())
							.setColorData(ColorParticleData.create(firstColor, secondColor).setEasing(Easing.CIRC_IN_OUT).setCoefficient(3.5f).build())
							.setTransparencyData(GenericParticleData.create(0.2f, 1f, 0).setEasing(Easing.SINE_IN, Easing.CIRC_IN_OUT).setCoefficient(3.5f).build())
							.setRandomOffset(0.1f, 0.225f)
							.addMotion(0, velocity / 2f, 0)
							.setRandomMotion(0, 0.015f)
							.addActor(p -> p.setVelocity(p.getVelocity().multiply(0.97f-world.random.nextFloat() * 0.025f)))
							.enableNoClip()
							.setDiscardFunction(SimpleParticleEffect.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
							.spawn(world, x, y, z);
				}
			}
		}
	}


}

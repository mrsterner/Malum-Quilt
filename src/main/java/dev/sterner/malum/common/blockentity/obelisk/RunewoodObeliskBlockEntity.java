package dev.sterner.malum.common.blockentity.obelisk;

import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.lodestone.setup.LodestoneParticleRegistry;
import dev.sterner.lodestone.systems.multiblock.MultiBlockStructure;
import dev.sterner.lodestone.systems.particle.WorldParticleBuilder;
import dev.sterner.lodestone.systems.particle.data.ColorParticleData;
import dev.sterner.lodestone.systems.particle.data.GenericParticleData;
import dev.sterner.lodestone.systems.particle.data.SpinParticleData;
import dev.sterner.malum.common.blockentity.spirit_altar.IAltarAccelerator;
import dev.sterner.malum.common.registry.MalumBlockEntityRegistry;
import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.function.Supplier;

public class RunewoodObeliskBlockEntity extends ObeliskCoreBlockEntity implements IAltarAccelerator {
	public static final AltarAcceleratorType OBELISK = new AltarAcceleratorType(4, "obelisk");
	public static final Supplier<MultiBlockStructure> STRUCTURE = () ->  (MultiBlockStructure.of(new MultiBlockStructure.StructurePiece(0, 1, 0, MalumObjects.RUNEWOOD_OBELISK_COMPONENT.getDefaultState())));

	public RunewoodObeliskBlockEntity(BlockPos pos, BlockState state) {
		super(MalumBlockEntityRegistry.RUNEWOOD_OBELISK, STRUCTURE.get(), pos, state);
	}

	@Override
	public AltarAcceleratorType getAcceleratorType() {
		return OBELISK;
	}

	@Override
	public float getAcceleration() {
		return 1f;
	}

	@Override
	public void addParticles(Color color, Color endColor, float alpha, BlockPos altarPos, Vec3d altarItemPos) {
		Vec3d startPos = BlockHelper.fromBlockPos(pos).add(0.5f, 2.15f, 0.5f);
		WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
				.setTransparencyData(GenericParticleData.create(alpha, 0f).build())
				.setScaleData(GenericParticleData.create(0.2f + world.random.nextFloat() * 0.1f, 0).build())
				.setLifetime(35)
				.setRandomOffset(0.1f)
				.setRandomMotion(0.01f, 0.01f)
				.setColorData(ColorParticleData.create(color, endColor).build())
				.setSpinData(SpinParticleData.create(0.1f + world.random.nextFloat() * 0.2f).build())
				.setRandomMotion(0.0025f, 0.0025f)
				.enableNoClip()
				.repeat(world, startPos.x, startPos.y, startPos.z, 1);

		WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
				.setTransparencyData(GenericParticleData.create(alpha, 0f).build())
				.setScaleData(GenericParticleData.create(0.5f, 0).build())
				.setLifetime(25)
				.setRandomOffset(0.1, 0.1)
				.setRandomMotion(0.02f, 0.02f)
				.setColorData(ColorParticleData.create(color, endColor).build())
				.setRandomMotion(0.0025f, 0.0025f)
				.enableNoClip()
				.repeat(world, startPos.x, startPos.y, startPos.z, 2);

	}
}

package dev.sterner.malum.common.world.gen.feature;

import com.google.common.collect.ImmutableList;
import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.lodestone.systems.worldgen.LodestoneBlockFiller;
import dev.sterner.malum.common.block.BlockTagRegistry;
import dev.sterner.malum.common.block.MalumLeavesBlock;
import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static dev.sterner.malum.common.world.gen.feature.RunewoodTreeFeature.canPlace;
import static dev.sterner.malum.common.world.gen.feature.RunewoodTreeFeature.updateLeaves;
import static net.minecraft.registry.tag.BlockTags.*;

public class SoulwoodTreeFeature extends Feature<DefaultFeatureConfig> {
    private static final OctaveSimplexNoiseSampler BLIGHT_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(new CheckedRandom(1234L)), ImmutableList.of(0));

    private static final int minimumSapBlockCount = 3;
    private static final int extraSapBlockCount = 5;

    private static final int minimumTrunkHeight = 12;
    private static final int extraTrunkHeight = 6;
    private static final int minimumTwistCooldown = 3;
    private static final int extraTwistCooldown = 1;
    private static final int minimumTrunkTwists = 2;
    private static final int extraTrunkTwists = 4;
    private static final int minimumSideTrunkHeight = 1;
    private static final int extraSideTrunkHeight = 2;

    private static final int minimumDownwardsBranchOffset = 2;
    private static final int extraDownwardsBranchOffset = 3;
    private static final int minimumBranchCoreOffset = 2;
    private static final int branchCoreOffsetExtra = 3;
    private static final int minimumBranchTwists = 0;
    private static final int extraBranchTwists = 2;
    private static final int minimumBranchHeight = 5;
    private static final int branchHeightExtra = 2;

    public SoulwoodTreeFeature() {
        super(DefaultFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> ctx) {
        var world = ctx.getWorld();
        BlockPos pos = ctx.getOrigin();
        Random rand = ctx.getRandom();
        if (world.isAir(pos.down()) || !MalumObjects.SOULWOOD_GROWTH.getDefaultState().canPlaceAt(world, pos)) {
            return false;
        }
        BlockState defaultLog = MalumObjects.SOULWOOD_LOG.getDefaultState();
        BlockState blightedLog = MalumObjects.BLIGHTED_SOULWOOD.getDefaultState();

        LodestoneBlockFiller treeFiller = new LodestoneBlockFiller(false);
        LodestoneBlockFiller blightFiller = new LodestoneBlockFiller(false);
        LodestoneBlockFiller leavesFiller = new LodestoneBlockFiller(true);

        int trunkHeight = minimumTrunkHeight + rand.nextInt(extraTrunkHeight + 1);
        Direction[] directions = new Direction[]{Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST};
        int twistDirection = rand.nextInt(directions.length);
        int twistCooldown = 3;
        int twists = minimumTrunkTwists + rand.nextInt(extraTrunkTwists + 1);
        int lowestPossibleBranch = minimumDownwardsBranchOffset + extraDownwardsBranchOffset;
        BlockPos twistedPos = pos;
        for (int i = 0; i <= trunkHeight; i++) //trunk placement
        {
            if (i < trunkHeight - lowestPossibleBranch) {
                if (twistCooldown == 0 && twists != 0) {
                    twistCooldown = minimumTwistCooldown + rand.nextInt(extraTwistCooldown + 1);
                    BlockPos trunkPos = twistedPos.up(i);
                    if (canPlace(world, trunkPos)) {
                        treeFiller.getEntries().put(trunkPos, new LodestoneBlockFiller.BlockStateEntry(defaultLog));
                        twistCooldown--;
                    } else {
                        return false;
                    }
                    twistedPos = twistedPos.offset(directions[twistDirection]);
                    if (rand.nextFloat() < 0.85f) {
                        twistDirection++;
                        if (twistDirection == 4) {
                            twistDirection = 0;
                        }
                    }
                    twists--;
                }
            }
            BlockPos trunkPos = twistedPos.up(i);
            if (canPlace(world, trunkPos)) {
                treeFiller.getEntries().put(trunkPos, new LodestoneBlockFiller.BlockStateEntry(i == 0 ? blightedLog : defaultLog));
                twistCooldown--;
            } else {
                return false;
            }
        }
        BlockPos trunkTop = twistedPos.up(trunkHeight);

        makeLeafBlob(leavesFiller, rand, trunkTop);
        for (Direction direction : directions) //side trunk placement
        {
			BlockPos blightedPos = null;
			int sideTrunkHeight = minimumSideTrunkHeight + rand.nextInt(extraSideTrunkHeight + 1);
			for (int i = 0; i < sideTrunkHeight; i++) {
				BlockPos sideTrunkPos = pos.offset(direction).up(i);
				if (canPlace(world, sideTrunkPos)) {
					if (i == 0) {
						blightedPos = sideTrunkPos;
					}
					treeFiller.getEntries().put(sideTrunkPos, new LodestoneBlockFiller.BlockStateEntry(i == 0 ? blightedLog : defaultLog));
				} else {
					return false;
				}
			}
            boolean success = downwardsTrunk(world, treeFiller, pos.offset(direction));
            if (success) {
				treeFiller.replace(blightedPos, e -> new LodestoneBlockFiller.BlockStateEntry(defaultLog));
            }
        }
        for (Direction direction : directions) //tree top placement
        {
            int branchCoreOffset = minimumDownwardsBranchOffset + rand.nextInt(extraDownwardsBranchOffset + 1);
            int branchOffset = minimumBranchCoreOffset + rand.nextInt(branchCoreOffsetExtra + 1);
            BlockPos branchStartPos = trunkTop.down(branchCoreOffset);
            twistCooldown = 1;
            twists = minimumBranchTwists + rand.nextInt(extraBranchTwists + 1);
            for (int i = 1; i < branchOffset; i++) //branch connection placement
            {
                BlockPos branchConnectionPos = branchStartPos.offset(direction, i);
                if (twistCooldown == 0 && twists != 0) {
                    twistCooldown = minimumTwistCooldown + rand.nextInt(extraTwistCooldown + 1);
                    BlockPos offsetPos = branchConnectionPos.up();
                    if (canPlace(world, offsetPos)) {
                        treeFiller.getEntries().put(offsetPos, new LodestoneBlockFiller.BlockStateEntry(defaultLog.with(PillarBlock.AXIS, direction.getAxis())));
                    } else {
                        return false;
                    }
                    branchStartPos = branchStartPos.up();
                    twists--;
                }
                if (canPlace(world, branchConnectionPos)) {
                    treeFiller.getEntries().put(branchConnectionPos, new LodestoneBlockFiller.BlockStateEntry(defaultLog.with(PillarBlock.AXIS, direction.getAxis())));
                    twistCooldown--;
                } else {
                    return false;
                }
            }
            BlockPos branchEndPos = branchStartPos.offset(direction, branchOffset);
            int branchHeight = minimumBranchHeight + rand.nextInt(branchHeightExtra + 1);
            for (int i = 0; i < branchHeight; i++) //branch placement
            {
                BlockPos branchPos = branchEndPos.up(i);
                if (canPlace(world, branchPos)) {
                    treeFiller.getEntries().put(branchPos, new LodestoneBlockFiller.BlockStateEntry(defaultLog));
                } else {
                    return false;
                }
            }
            makeLeafBlob(leavesFiller, rand, branchEndPos.up(1));
        }
        generateBlight(world, blightFiller, pos.down(), 6);

		int sapBlockCount = minimumSapBlockCount + rand.nextInt(extraSapBlockCount + 1);
		ArrayList<BlockPos> sapBlockPositions = new ArrayList<>(treeFiller.getEntries().keySet());
		Collections.shuffle(sapBlockPositions);
		for (BlockPos blockPos : sapBlockPositions.subList(0, sapBlockCount)) {
			treeFiller.replace(blockPos, e -> e.getState().getBlock().equals(MalumObjects.BLIGHTED_SOULWOOD) ? e : new LodestoneBlockFiller.BlockStateEntry(BlockHelper.getBlockStateWithExistingProperties(e.getState(), MalumObjects.EXPOSED_SOULWOOD_LOG.getDefaultState())));
		}

        blightFiller.fill(world);
        treeFiller.fill(world);
        leavesFiller.fill(world);
		updateLeaves(world, treeFiller.getEntries().keySet());
        return true;
    }

    public static boolean downwardsTrunk(StructureWorldAccess worldAccess, LodestoneBlockFiller filler, BlockPos pos) {
        BlockState defaultLog = MalumObjects.SOULWOOD_LOG.getDefaultState();
        BlockState blightedLog = MalumObjects.BLIGHTED_SOULWOOD.getDefaultState();
        int i = 0;
        do {
            i++;
            BlockPos trunkPos = pos.down(i);
            if (canPlace(worldAccess, trunkPos)) {
                boolean blighted = !canPlace(worldAccess, trunkPos.down());
                filler.getEntries().put(trunkPos, new LodestoneBlockFiller.BlockStateEntry(blighted ? blightedLog : defaultLog));
            } else {
                break;
            }
            if (i > worldAccess.getTopY()) {
                break;
            }
        }
        while (true);
        return i > 1;
    }

    public static void makeLeafBlob(LodestoneBlockFiller filler, Random rand, BlockPos pos) {
        makeLeafSlice(filler, rand, pos, 1, 0);
        makeLeafSlice(filler, rand, pos.up(1), 2, 1);
        makeLeafSlice(filler, rand, pos.up(2), 3, 2);
        makeLeafSlice(filler, rand, pos.up(3), 3, 3);
        makeLeafSlice(filler, rand, pos.up(3), 3, 3 - rand.nextInt(1));
        makeLeafSlice(filler, rand, pos.up(4), 3, 3 - rand.nextInt(1));
        makeLeafSlice(filler, rand, pos.up(5), 2, 3 + rand.nextInt(1));
        makeLeafSlice(filler, rand, pos.up(6), 1, 4);
    }

    public static void makeLeafSlice(LodestoneBlockFiller filler, Random rand, BlockPos pos, int leavesSize, int leavesColor) {
        for (int x = -leavesSize; x <= leavesSize; x++) {
            for (int z = -leavesSize; z <= leavesSize; z++) {
                if (Math.abs(x) == leavesSize && Math.abs(z) == leavesSize) {
                    continue;
                }
                BlockPos leavesPos = new BlockPos(pos).add(x, 0, z);
                int offsetColor = leavesColor + MathHelper.nextInt(rand, leavesColor == 0 ? 0 : -1, leavesColor == 4 ? 0 : 1);
                filler.getEntries().put(leavesPos, new LodestoneBlockFiller.BlockStateEntry(MalumObjects.SOULWOOD_LEAVES.getDefaultState().with(MalumLeavesBlock.COLOR, offsetColor)));
            }
        }
    }

    public static Map<Integer, Double> generateBlight(StructureWorldAccess worldAccess, LodestoneBlockFiller filler, BlockPos pos, int radius) {
        Map<Integer, Double> noiseValues = new HashMap<>();
        for (int i = 0; i <= 360; i++) {
            noiseValues.put(i, BLIGHT_NOISE.sample(pos.getX() + pos.getZ() + i * 0.02f, pos.getY() / 0.05f, true) * 2.5f);
        }
        generateBlight(worldAccess, filler, noiseValues, pos, radius);
        return noiseValues;
    }

    public static void generateBlight(StructureWorldAccess worldAccess, LodestoneBlockFiller filler, Map<Integer, Double> noiseValues, BlockPos pos, int radius) {
        generateBlight(worldAccess, filler, pos, radius*2, radius, noiseValues);
        filler.fill(worldAccess);
    }

    public static void generateBlight(StructureWorldAccess worldAccess, LodestoneBlockFiller filler, BlockPos center, int radius, int effectiveRadius, Map<Integer, Double> noiseValues) {
        int x = center.getX();
        int z = center.getZ();
        BlockPos.Mutable blockPos = new BlockPos.Mutable();
        int saplingsPlaced = 0;
        Vec3d lastSaplingPos = null;
        for (int i = 0; i < radius * 2 + 1; i++) {
            for (int j = 0; j < radius * 2 + 1; j++) {
                int xp = x + i - radius;
                int zp = z + j - radius;
                blockPos.set(xp, center.getY(), zp);
                double theta = 180 + 180 / Math.PI * Math.atan2(x - xp, z - zp);
                double naturalNoiseValue = noiseValues.get(MathHelper.floor(theta));
                if (naturalNoiseValue > 1f) {
                    naturalNoiseValue *= naturalNoiseValue;
                }
                int floor = (int) Math.floor(pointDistancePlane(xp, zp, x, z));
                if (floor <= (effectiveRadius + Math.floor(naturalNoiseValue) - 1)) {
                    int verticalRange = 4;
                    for (int i1 = 0; worldAccess.testBlockState(blockPos, (s) -> !s.isAir()) && i1 < verticalRange; ++i1) {
                        blockPos.move(Direction.UP);
                    }
                    for (int k = 0; worldAccess.testBlockState(blockPos, AbstractBlock.AbstractBlockState::isAir) && k < verticalRange; ++k) {
                        blockPos.move(Direction.DOWN);
                    }
                    do {
                        BlockState plantState = worldAccess.getBlockState(blockPos);
                        if (plantState.isAir()) {
                            break;
                        }
                        if (plantState.isIn(BlockTagRegistry.BLIGHTED_PLANTS)) {
                            break;
                        }
                        if ((plantState.isReplaceable() || plantState.isIn(REPLACEABLE) || plantState.isIn(FLOWERS))) {
                            filler.getEntries().put(blockPos.toImmutable(), new LodestoneBlockFiller.BlockStateEntry(Blocks.AIR.getDefaultState()));
                            blockPos.move(Direction.DOWN);
                        } else {
                            break;
                        }
                    }
                    while (true);

                    BlockPos immutable = blockPos.toImmutable();
                    if (worldAccess.getBlockState(immutable).isIn(MOSS_REPLACEABLE)) {
                        filler.getEntries().put(immutable, new LodestoneBlockFiller.BlockStateEntry(MalumObjects.BLIGHTED_SOIL.getDefaultState()));
                        if (worldAccess.getBlockState(immutable.down()).isIn(DIRT)) {
                            filler.getEntries().put( immutable.down(), new LodestoneBlockFiller.BlockStateEntry(MalumObjects.BLIGHTED_EARTH.getDefaultState()));
                        }
                        if (worldAccess.getRandom().nextFloat() < 0.8f) {
                            BlockPos plantPos = immutable.up();
                            BlockState blockState = worldAccess.getBlockState(plantPos);
                            if (naturalNoiseValue > 2.5f) {
                                if (lastSaplingPos == null || lastSaplingPos.squaredDistanceTo(plantPos.getX(), plantPos.getY(), plantPos.getZ()) > 5) {
                                    if (BlockHelper.fromBlockPos(center).squaredDistanceTo(plantPos.getX(), plantPos.getY(), plantPos.getZ()) > 4) {
                                        if (worldAccess.getRandom().nextFloat() < 0.5f / (Math.pow(saplingsPlaced + 1, 2))) {
                                            filler.getEntries().put(plantPos, new LodestoneBlockFiller.BlockStateEntry(MalumObjects.SOULWOOD_GROWTH.getDefaultState()));
                                            lastSaplingPos = new Vec3d(plantPos.getX(), plantPos.getY(), plantPos.getZ());
                                            saplingsPlaced++;
                                        }
                                        continue;
                                    }
                                }
                            }
                            if (blockState.isAir() && !blockState.isIn(BlockTagRegistry.BLIGHTED_PLANTS)) {
                                filler.getEntries().put(plantPos, new LodestoneBlockFiller.BlockStateEntry((worldAccess.getRandom().nextFloat() < 0.2f ? MalumObjects.BLIGHTED_TUMOR : MalumObjects.BLIGHTED_WEED).getDefaultState()));
                            }
                        }
                    }
                }
            }
        }
    }

    public static float pointDistancePlane(double x1, double z1, double x2, double z2) {
        return (float) Math.hypot(x1 - x2, z1 - z2);
    }
}

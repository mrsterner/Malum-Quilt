package dev.sterner.malum.common.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.sterner.lodestone.helpers.BlockHelper;
import dev.sterner.lodestone.systems.worldgen.LodestoneBlockFiller;
import dev.sterner.malum.common.block.MalumLeavesBlock;
import dev.sterner.malum.common.block.MalumSaplingBlock;
import dev.sterner.malum.common.registry.MalumObjects;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RunewoodTreeFeature extends Feature<DefaultFeatureConfig> {
    private static final int minimumSapBlockCount = 2;
    private static final int extraSapBlockCount = 1;

    private static final int minimumTrunkHeight = 7;
    private static final int extraTrunkHeight = 3;
    private static final int minimumSideTrunkHeight = 0;
    private static final int extraSideTrunkHeight = 2;

    private static final int minimumDownwardsBranchOffset = 2;
    private static final int extraDownwardsBranchOffset = 2;
    private static final int minimumBranchCoreOffset = 2;
    private static final int branchCoreOffsetExtra = 1;
    private static final int minimumBranchHeight = 3;
    private static final int branchHeightExtra = 2;

    public RunewoodTreeFeature() {
        super(DefaultFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        var pos = context.getOrigin();
        var rand = context.getRandom();
        if (world.isAir(pos.down()) || !MalumObjects.RUNEWOOD_SAPLING.getDefaultState().canPlaceAt(world, pos)) {
            return false;
        }
        BlockState defaultLog = MalumObjects.RUNEWOOD_LOG.getDefaultState();

        LodestoneBlockFiller treeFiller = new LodestoneBlockFiller(false);
        LodestoneBlockFiller leavesFiller = new LodestoneBlockFiller(true);

        int trunkHeight = minimumTrunkHeight + rand.nextInt(extraTrunkHeight + 1);
        BlockPos trunkTop = pos.up(trunkHeight);
        Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

        for (int i = 0; i <= trunkHeight; i++) //trunk placement
        {
            BlockPos trunkPos = pos.up(i);
            if (canPlace(world, trunkPos)) {
                treeFiller.getEntries().put(trunkPos, new LodestoneBlockFiller.BlockStateEntry(defaultLog));
            } else {
                return false;
            }
        }
        makeLeafBlob(leavesFiller, rand, trunkTop);
        for (Direction direction : directions) //side trunk placement
        {
            int sideTrunkHeight = minimumSideTrunkHeight + rand.nextInt(extraSideTrunkHeight + 1);
            for (int i = 0; i < sideTrunkHeight; i++) {
                BlockPos sideTrunkPos = pos.offset(direction).up(i);
                if (canPlace(world, sideTrunkPos)) {
                    treeFiller.getEntries().put(sideTrunkPos, new LodestoneBlockFiller.BlockStateEntry(defaultLog));
                } else {
                    return false;
                }
            }
            downwardsTrunk(world, treeFiller, pos.offset(direction));
        }
        for (Direction direction : directions) //tree top placement
        {
            int branchCoreOffset = minimumDownwardsBranchOffset + rand.nextInt(extraDownwardsBranchOffset + 1);
            int branchOffset = minimumBranchCoreOffset + rand.nextInt(branchCoreOffsetExtra + 1);
            BlockPos branchStartPos = trunkTop.down(branchCoreOffset).offset(direction, branchOffset);
            for (int i = 0; i < branchOffset; i++) //branch connection placement
            {
                BlockPos branchConnectionPos = branchStartPos.offset(direction.getOpposite(), i);
                if (canPlace(world, branchConnectionPos)) {
                    treeFiller.getEntries().put(branchConnectionPos, new LodestoneBlockFiller.BlockStateEntry(defaultLog.with(PillarBlock.AXIS, direction.getAxis())));
                } else {
                    return false;
                }
            }
            int branchHeight = minimumBranchHeight + rand.nextInt(branchHeightExtra + 1);
            for (int i = 0; i < branchHeight; i++) //branch placement
            {
                BlockPos branchPos = branchStartPos.up(i);
                if (canPlace(world, branchPos)) {
                    treeFiller.getEntries().put(branchPos, new LodestoneBlockFiller.BlockStateEntry(defaultLog));
                } else {
                    return false;
                }
            }
            makeLeafBlob(leavesFiller, rand, branchStartPos.up(1));
        }
        int sapBlockCount = minimumSapBlockCount + rand.nextInt(extraSapBlockCount + 1);
		ArrayList<BlockPos> sapBlockPositions = new ArrayList<>(treeFiller.getEntries().keySet());
		for (BlockPos blockPos : sapBlockPositions.subList(0, sapBlockCount)) {
			treeFiller.replace(blockPos, e -> new LodestoneBlockFiller.BlockStateEntry(BlockHelper.getBlockStateWithExistingProperties(e.getState(), MalumObjects.EXPOSED_RUNEWOOD_LOG.getDefaultState())));
		}
        treeFiller.fill(world);
        leavesFiller.fill(world);
        updateLeaves(world, treeFiller.getEntries().keySet());
        return true;
    }

    public static void downwardsTrunk(StructureWorldAccess world, LodestoneBlockFiller filler, BlockPos pos) {
        int i = 0;
        do {
            i++;
            BlockPos trunkPos = pos.down(i);
            if (canPlace(world, trunkPos)) {
                filler.getEntries().put(trunkPos, new LodestoneBlockFiller.BlockStateEntry(MalumObjects.RUNEWOOD_LOG.getDefaultState()));
            } else {
                break;
            }
            if (i > world.getTopY()) {
                break;
            }
        }
        while (true);
    }

    public static void makeLeafBlob(LodestoneBlockFiller filler, Random rand, BlockPos pos) {
        makeLeafSlice(filler, pos, 1, 0);
        makeLeafSlice(filler, pos.up(1), 2, 1);
        makeLeafSlice(filler, pos.up(2), 2, 2);
        makeLeafSlice(filler, pos.up(3), 2, 3);
        makeLeafSlice(filler, pos.up(4), 1, 4);
    }

    public static void makeLeafSlice(LodestoneBlockFiller filler, BlockPos pos, int leavesSize, int leavesColor) {
        for (int x = -leavesSize; x <= leavesSize; x++) {
            for (int z = -leavesSize; z <= leavesSize; z++) {
                if (Math.abs(x) == leavesSize && Math.abs(z) == leavesSize) {
                    continue;
                }
                BlockPos leavesPos = new BlockPos(pos).add(x, 0, z);
                filler.getEntries().put(leavesPos, new LodestoneBlockFiller.BlockStateEntry(MalumObjects.RUNEWOOD_LEAVES.getDefaultState().with(MalumLeavesBlock.COLOR, leavesColor)));
            }
        }
    }

    public static boolean canPlace(StructureWorldAccess world, BlockPos pos) {
        if (world.isOutOfHeightLimit(pos)) {
            return false;
        }
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof MalumSaplingBlock || world.isAir(pos) || state.isReplaceable();
    }

    public static void updateLeaves(WorldAccess pWorld, Set<BlockPos> logPositions) {
        List<Set<BlockPos>> list = Lists.newArrayList();
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos pos : Lists.newArrayList(logPositions)) {
            for (Direction direction : Direction.values()) {
                mutable.set(pos, direction);
                if (!logPositions.contains(mutable)) {
                    BlockState blockstate = pWorld.getBlockState(mutable);
                    if (blockstate.contains(Properties.DISTANCE_1_7)) {
                        list.get(0).add(mutable.toImmutable());
                        pWorld.setBlockState(mutable, blockstate.with(Properties.DISTANCE_1_7, 1), 19);
                    }
                }
            }
        }

        for (int l = 1; l < 6; ++l) {
            Set<BlockPos> set = list.get(l - 1);
            Set<BlockPos> set1 = list.get(l);

            for (BlockPos pos : set) {
                for (Direction direction1 : Direction.values()) {
                    mutable.set(pos, direction1);
                    if (!set.contains(mutable) && !set1.contains(mutable)) {
                        BlockState blockstate1 = pWorld.getBlockState(mutable);
                        if (blockstate1.contains(Properties.DISTANCE_1_7)) {
                            int k = blockstate1.get(Properties.DISTANCE_1_7);
                            if (k > l + 1) {
                                BlockState blockstate2 = blockstate1.with(Properties.DISTANCE_1_7, l + 1);
                                pWorld.setBlockState(mutable, blockstate2, 19);
                                set1.add(mutable.toImmutable());
                            }
                        }
                    }
                }
            }
        }
    }
}

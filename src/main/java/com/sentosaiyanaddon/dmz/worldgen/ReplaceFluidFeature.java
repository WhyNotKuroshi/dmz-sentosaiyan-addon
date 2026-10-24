package com.sentosaiyanaddon.dmz.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ReplaceFluidFeature extends Feature<NoneFeatureConfiguration> {

    private static final boolean ONLY_POCKET_ZONE = true;

    private static final float DEPTH_MIN = 0.15F;
    private static final float DEPTH_MAX = 0.65F;
    private static final float WEIRDNESS_MAX = -0.35F;

    private static final ResourceKey<Biome> CAVES = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath("sentosaiyanaddon", "sento_tropical_caves"));
    private static final ResourceLocation HOT_SPRING = ResourceLocation.fromNamespaceAndPath("sentosaiyanaddon",
            "hot_spring_block");

    public ReplaceFluidFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();

        Block hot = ForgeRegistries.BLOCKS.getValue(HOT_SPRING);
        if (hot == null || hot == Blocks.AIR) {
            return false;
        }
        BlockState hotState = hot.defaultBlockState();

        Climate.Sampler sampler = ONLY_POCKET_ZONE
                ? level.getLevel().getChunkSource().randomState().sampler()
                : null;
        Map<Long, Boolean> zoneCache = new HashMap<>();

        int minX = ctx.origin().getX() & ~15;
        int minZ = ctx.origin().getZ() & ~15;
        int minY = level.getMinBuildHeight();
        int maxY = Math.min(level.getMaxBuildHeight(), 160);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        boolean changed = false;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int floorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, minX + x, minZ + z);
                int topY = Math.min(maxY, floorY - 1);

                for (int y = minY; y < topY; y++) {
                    pos.set(minX + x, y, minZ + z);

                    BlockState state = level.getBlockState(pos);
                    FluidState fluid = state.getFluidState();
                    if (fluid.isEmpty()
                            || (!fluid.getType().isSame(Fluids.WATER) && !fluid.getType().isSame(Fluids.LAVA))) {
                        continue;
                    }
                    if (!level.getBiome(pos).is(CAVES)) {
                        continue;
                    }
                    if (ONLY_POCKET_ZONE && !fluid.getType().isSame(Fluids.LAVA)) {
                        int qx = QuartPos.fromBlock(pos.getX());
                        int qy = QuartPos.fromBlock(pos.getY());
                        int qz = QuartPos.fromBlock(pos.getZ());
                        boolean inZone = zoneCache.computeIfAbsent(
                                BlockPos.asLong(qx, qy, qz), k -> inPocketZone(sampler, qx, qy, qz));
                        if (!inZone) {
                            continue;
                        }
                    }
                    level.setBlock(pos, hotState, Block.UPDATE_CLIENTS);
                    changed = true;
                }
            }
        }
        return changed;
    }

    private static boolean inPocketZone(Climate.Sampler sampler, int qx, int qy, int qz) {
        Climate.TargetPoint t = sampler.sample(qx, qy, qz);
        float depth = Climate.unquantizeCoord(t.depth());
        float weirdness = Climate.unquantizeCoord(t.weirdness());
        return depth >= DEPTH_MIN && depth <= DEPTH_MAX && weirdness < WEIRDNESS_MAX;
    }
}

package com.sentosaiyanaddon.dmz.block;

import com.sentosaiyanaddon.dmz.SentoSaiyanAddon;
import com.sentosaiyanaddon.dmz.item.SentoItems;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;

import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SentoFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister
            .create(ForgeRegistries.Keys.FLUID_TYPES, SentoSaiyanAddon.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister
            .create(ForgeRegistries.FLUIDS, SentoSaiyanAddon.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister
            .create(ForgeRegistries.BLOCKS, SentoSaiyanAddon.MOD_ID);

    public static final RegistryObject<FluidType> HOT_SPRING_TYPE = FLUID_TYPES.register("hot_spring",
            () -> new HotSpringFluidType(FluidType.Properties.create()
                    .descriptionId("fluid.sentosaiyanaddon.hot_spring")
                    .fallDistanceModifier(0F)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .canHydrate(true)
                    .lightLevel(12)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .density(1000)
                    .viscosity(1000)
                    .temperature(320)));

    public static final RegistryObject<FlowingFluid> HOT_SPRING = FLUIDS.register("hot_spring",
            () -> new HotSpringFluid.Source(props()));

    public static final RegistryObject<FlowingFluid> FLOWING_HOT_SPRING = FLUIDS.register("flowing_hot_spring",
            () -> new ForgeFlowingFluid.Flowing(props()) {
                @Override
                protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
                    if (!state.isSource() && !state.getValue(FALLING)) {
                        if (random.nextInt(64) == 0) {
                            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS,
                                    random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
                        }
                    }
                }
            });
    public static final RegistryObject<LiquidBlock> HOT_SPRING_BLOCK = BLOCKS.register("hot_spring_block",
            () -> new HotSpringBlock(HOT_SPRING,
                    BlockBehaviour.Properties.copy(Blocks.WATER)
                            .lightLevel(state -> 12)
                            .noLootTable()
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)));

    private static ForgeFlowingFluid.Properties props() {
        return new ForgeFlowingFluid.Properties(HOT_SPRING_TYPE, HOT_SPRING, FLOWING_HOT_SPRING)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1)
                .block(HOT_SPRING_BLOCK)
                .bucket(() -> SentoItems.HOT_SPRING_BUCKET.get());

    }
}
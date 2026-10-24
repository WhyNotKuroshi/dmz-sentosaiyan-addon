package com.sentosaiyanaddon.dmz.block;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class HotSpringFluid extends ForgeFlowingFluid {
    protected HotSpringFluid(Properties properties) { super(properties); }

    public static class Source extends HotSpringFluid {
        public Source(Properties p) { super(p); }
        @Override public boolean isSource(FluidState s) { return true; }
        @Override public int getAmount(FluidState s) { return 8; }
    }

    public static class Flowing extends HotSpringFluid {
        public Flowing(Properties p) {
            super(p);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> b) {
            super.createFluidStateDefinition(b);
            b.add(LEVEL);
        }
        @Override public boolean isSource(FluidState s) { return false; }
        @Override public int getAmount(FluidState s) { return s.getValue(LEVEL); }
    }
}
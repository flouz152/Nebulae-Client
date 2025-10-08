package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public interface ILiquidContainer
{
// leaked by itskekoff; discord.gg/sk3d RfLfiRDh
    boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn);

    boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn);
}

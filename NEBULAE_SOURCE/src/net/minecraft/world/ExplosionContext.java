package net.minecraft.world;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

public class ExplosionContext
{
// leaked by itskekoff; discord.gg/sk3d vs5vA5Kt
    public Optional<Float> getExplosionResistance(Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, FluidState fluid)
    {
        return state.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(state.getBlock().getExplosionResistance(), fluid.getExplosionResistance()));
    }

    public boolean canExplosionDestroyBlock(Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, float power)
    {
        return true;
    }
}

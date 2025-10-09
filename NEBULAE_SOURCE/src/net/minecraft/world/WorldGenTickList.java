package net.minecraft.world;

import java.util.function.Function;
import net.minecraft.util.math.BlockPos;

public class WorldGenTickList<T> implements ITickList<T>
{
// leaked by itskekoff; discord.gg/sk3d wVpE1PoD
    private final Function<BlockPos, ITickList<T>> tickListProvider;

    public WorldGenTickList(Function<BlockPos, ITickList<T>> tickListProviderIn)
    {
        this.tickListProvider = tickListProviderIn;
    }

    public boolean isTickScheduled(BlockPos pos, T itemIn)
    {
        return this.tickListProvider.apply(pos).isTickScheduled(pos, itemIn);
    }

    public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority)
    {
        this.tickListProvider.apply(pos).scheduleTick(pos, itemIn, scheduledTime, priority);
    }

    /**
     * Checks if this position/item is scheduled to be updated this tick
     */
    public boolean isTickPending(BlockPos pos, T obj)
    {
        return false;
    }
}

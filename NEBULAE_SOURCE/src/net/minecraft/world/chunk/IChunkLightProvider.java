package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;

public interface IChunkLightProvider
{
// leaked by itskekoff; discord.gg/sk3d YyvPO711
    @Nullable
    IBlockReader getChunkForLight(int chunkX, int chunkZ);

default void markLightChanged(LightType type, SectionPos pos)
    {
    }

    IBlockReader getWorld();
}

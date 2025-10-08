package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IForgeRenderChunk
{
// leaked by itskekoff; discord.gg/sk3d 2k6ABJed
default ChunkRenderCache createRegionRenderCache(World world, BlockPos from, BlockPos to, int subtract)
    {
        return ChunkRenderCache.generateCache(world, from, to, subtract);
    }
}

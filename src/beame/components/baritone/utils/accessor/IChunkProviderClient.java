package beame.components.baritone.utils.accessor;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.chunk.Chunk;

public interface IChunkProviderClient {
// leaked by itskekoff; discord.gg/sk3d kywxxev7

    Long2ObjectMap<Chunk> loadedChunks();
}

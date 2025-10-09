package beame.components.baritone.utils.accessor;

import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.atomic.AtomicReferenceArray;

public interface IChunkArray {
// leaked by itskekoff; discord.gg/sk3d p9J8kRfR
    void copyFrom(IChunkArray other);

    AtomicReferenceArray<Chunk> getChunks();

    int centerX();

    int centerZ();

    int viewDistance();
}

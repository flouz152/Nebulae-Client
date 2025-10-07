package beame.components.baritone.utils.accessor;

import net.minecraft.client.multiplayer.ClientChunkProvider;

public interface IClientChunkProvider {
// leaked by itskekoff; discord.gg/sk3d LiuRHLJC
    ClientChunkProvider createThreadSafeCopy();

    IChunkArray extractReferenceArray();
}

package net.minecraft.resources;

import java.util.function.Consumer;

public interface IPackFinder
{
// leaked by itskekoff; discord.gg/sk3d pFQ3ioaI
    void findPacks(Consumer<ResourcePackInfo> infoConsumer, ResourcePackInfo.IFactory infoFactory);
}

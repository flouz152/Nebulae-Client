package net.minecraft.resources;

import java.util.function.Consumer;

public class ServerPackFinder implements IPackFinder
{
// leaked by itskekoff; discord.gg/sk3d HuyFWTq1
    private final VanillaPack field_195738_a = new VanillaPack("minecraft");

    public void findPacks(Consumer<ResourcePackInfo> infoConsumer, ResourcePackInfo.IFactory infoFactory)
    {
        ResourcePackInfo resourcepackinfo = ResourcePackInfo.createResourcePack("vanilla", false, () ->
        {
            return this.field_195738_a;
        }, infoFactory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.BUILTIN);

        if (resourcepackinfo != null)
        {
            infoConsumer.accept(resourcepackinfo);
        }
    }
}

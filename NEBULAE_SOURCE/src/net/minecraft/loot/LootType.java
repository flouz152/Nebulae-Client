package net.minecraft.loot;

public class LootType<T>
{
// leaked by itskekoff; discord.gg/sk3d ru87449c
    private final ILootSerializer <? extends T > serializer;

    public LootType(ILootSerializer <? extends T > serializer)
    {
        this.serializer = serializer;
    }

    public ILootSerializer <? extends T > getSerializer()
    {
        return this.serializer;
    }
}

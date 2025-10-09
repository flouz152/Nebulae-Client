package net.minecraft.loot;

import net.minecraft.util.ResourceLocation;

public class LootParameter<T>
{
// leaked by itskekoff; discord.gg/sk3d ISoDyf6K
    private final ResourceLocation id;

    public LootParameter(ResourceLocation idIn)
    {
        this.id = idIn;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public String toString()
    {
        return "<parameter " + this.id + ">";
    }
}

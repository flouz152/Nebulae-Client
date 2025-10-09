package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum DoubleBlockHalf implements IStringSerializable
{
// leaked by itskekoff; discord.gg/sk3d 5zdd9o3O
    UPPER,
    LOWER;

    public String toString()
    {
        return this.getString();
    }

    public String getString()
    {
        return this == UPPER ? "upper" : "lower";
    }
}

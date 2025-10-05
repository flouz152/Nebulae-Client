package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum BedPart implements IStringSerializable
{
// leaked by itskekoff; discord.gg/sk3d r5v9txtH
    HEAD("head"),
    FOOT("foot");

    private final String name;

    private BedPart(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }

    public String getString()
    {
        return this.name;
    }
}

package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum Half implements IStringSerializable
{
// leaked by itskekoff; discord.gg/sk3d Xk2wY3H3
    TOP("top"),
    BOTTOM("bottom");

    private final String name;

    private Half(String name)
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

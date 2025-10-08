package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum RedstoneSide implements IStringSerializable
{
// leaked by itskekoff; discord.gg/sk3d 3g3rHqbO
    UP("up"),
    SIDE("side"),
    NONE("none");

    private final String name;

    private RedstoneSide(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.getString();
    }

    public String getString()
    {
        return this.name;
    }

    public boolean func_235921_b_()
    {
        return this != NONE;
    }
}

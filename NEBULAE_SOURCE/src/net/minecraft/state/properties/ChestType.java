package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum ChestType implements IStringSerializable
{
// leaked by itskekoff; discord.gg/sk3d m5rMGWgg
    SINGLE("single", 0),
    LEFT("left", 2),
    RIGHT("right", 1);

    public static final ChestType[] VALUES = values();
    private final String name;
    private final int opposite;

    private ChestType(String name, int oppositeIn)
    {
        this.name = name;
        this.opposite = oppositeIn;
    }

    public String getString()
    {
        return this.name;
    }

    public ChestType opposite()
    {
        return VALUES[this.opposite];
    }
}

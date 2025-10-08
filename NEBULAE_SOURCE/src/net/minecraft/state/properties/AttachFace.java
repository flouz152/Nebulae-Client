package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum AttachFace implements IStringSerializable
{
// leaked by itskekoff; discord.gg/sk3d IQQwNkX7
    FLOOR("floor"),
    WALL("wall"),
    CEILING("ceiling");

    private final String name;

    private AttachFace(String name)
    {
        this.name = name;
    }

    public String getString()
    {
        return this.name;
    }
}

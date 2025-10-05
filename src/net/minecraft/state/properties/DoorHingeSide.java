package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum DoorHingeSide implements IStringSerializable
{
// leaked by itskekoff; discord.gg/sk3d zgxqecBy
    LEFT,
    RIGHT;

    public String toString()
    {
        return this.getString();
    }

    public String getString()
    {
        return this == LEFT ? "left" : "right";
    }
}

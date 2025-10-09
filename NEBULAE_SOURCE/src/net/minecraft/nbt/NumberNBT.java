package net.minecraft.nbt;

public abstract class NumberNBT implements INBT
{
// leaked by itskekoff; discord.gg/sk3d buHLKpLn
    protected NumberNBT()
    {
    }

    public abstract long getLong();

    public abstract int getInt();

    public abstract short getShort();

    public abstract byte getByte();

    public abstract double getDouble();

    public abstract float getFloat();

    public abstract Number getAsNumber();
}

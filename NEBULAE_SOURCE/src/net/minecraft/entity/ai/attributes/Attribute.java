package net.minecraft.entity.ai.attributes;

public class Attribute
{
// leaked by itskekoff; discord.gg/sk3d ERvLSjXZ
    private final double defaultValue;
    private boolean shouldWatch;
    private final String attributeName;

    protected Attribute(String attributeName, double defaultValue)
    {
        this.defaultValue = defaultValue;
        this.attributeName = attributeName;
    }

    public double getDefaultValue()
    {
        return this.defaultValue;
    }

    public boolean getShouldWatch()
    {
        return this.shouldWatch;
    }

    public Attribute setShouldWatch(boolean watch)
    {
        this.shouldWatch = watch;
        return this;
    }

    public double clampValue(double value)
    {
        return value;
    }

    public String getAttributeName()
    {
        return this.attributeName;
    }
}

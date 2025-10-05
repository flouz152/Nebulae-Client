package net.optifine.shaders.config;

public class ShaderMacro
{
// leaked by itskekoff; discord.gg/sk3d KQ9EGBqr
    private String name;
    private String value;

    public ShaderMacro(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    public String getSourceLine()
    {
        return "#define " + this.name + " " + this.value;
    }

    public String toString()
    {
        return this.getSourceLine();
    }
}

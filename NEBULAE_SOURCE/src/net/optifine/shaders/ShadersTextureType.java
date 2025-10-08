package net.optifine.shaders;

public enum ShadersTextureType
{
// leaked by itskekoff; discord.gg/sk3d VUpCwrat
    NORMAL("_n"),
    SPECULAR("_s");

    private String suffix;

    private ShadersTextureType(String suffixIn)
    {
        this.suffix = suffixIn;
    }

    public String getSuffix()
    {
        return this.suffix;
    }
}

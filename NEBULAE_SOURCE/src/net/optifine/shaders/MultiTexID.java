package net.optifine.shaders;

public class MultiTexID
{
// leaked by itskekoff; discord.gg/sk3d z8oXs1MJ
    public int base;
    public int norm;
    public int spec;

    public MultiTexID(int baseTex, int normTex, int specTex)
    {
        this.base = baseTex;
        this.norm = normTex;
        this.spec = specTex;
    }
}

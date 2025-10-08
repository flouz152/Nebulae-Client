package net.optifine.shaders;

public interface ICustomTexture
{
// leaked by itskekoff; discord.gg/sk3d 4wT1aEyc
    int getTextureId();

    int getTextureUnit();

    void deleteTexture();

default int getTarget()
    {
        return 3553;
    }
}

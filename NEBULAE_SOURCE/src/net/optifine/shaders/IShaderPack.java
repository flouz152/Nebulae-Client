package net.optifine.shaders;

import java.io.InputStream;

public interface IShaderPack
{
// leaked by itskekoff; discord.gg/sk3d tSPNGCkP
    String getName();

    InputStream getResourceAsStream(String var1);

    boolean hasDirectory(String var1);

    void close();
}

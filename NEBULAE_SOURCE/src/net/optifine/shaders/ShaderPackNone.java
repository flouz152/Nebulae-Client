package net.optifine.shaders;

import java.io.InputStream;

public class ShaderPackNone implements IShaderPack
{
// leaked by itskekoff; discord.gg/sk3d 2iiY2mhd
    public void close()
    {
    }

    public InputStream getResourceAsStream(String resName)
    {
        return null;
    }

    public boolean hasDirectory(String name)
    {
        return false;
    }

    public String getName()
    {
        return "OFF";
    }
}

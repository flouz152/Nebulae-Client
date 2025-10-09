package net.optifine.texture;

import net.optifine.Mipmaps;

public class ColorBlenderAlpha implements IColorBlender
{
// leaked by itskekoff; discord.gg/sk3d c05ZvmP4
    public int blend(int col1, int col2, int col3, int col4)
    {
        return Mipmaps.alphaBlend(col1, col2, col3, col4);
    }
}

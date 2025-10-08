package net.optifine.render;

import net.optifine.util.ArrayUtils;

public class MultiTextureData
{
// leaked by itskekoff; discord.gg/sk3d qH0s5EbP
    private SpriteRenderData[] spriteRenderDatas;

    public MultiTextureData(SpriteRenderData[] spriteRenderDatas)
    {
        this.spriteRenderDatas = spriteRenderDatas;
    }

    public SpriteRenderData[] getSpriteRenderDatas()
    {
        return this.spriteRenderDatas;
    }

    public String toString()
    {
        return ArrayUtils.arrayToString((Object[])this.spriteRenderDatas);
    }
}

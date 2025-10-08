package net.minecraft.block;

import net.minecraft.item.DyeColor;

public class StainedGlassBlock extends AbstractGlassBlock implements IBeaconBeamColorProvider
{
// leaked by itskekoff; discord.gg/sk3d RuR8yjbp
    private final DyeColor color;

    public StainedGlassBlock(DyeColor colorIn, AbstractBlock.Properties properties)
    {
        super(properties);
        this.color = colorIn;
    }

    public DyeColor getColor()
    {
        return this.color;
    }
}
